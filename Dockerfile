# ===== Builder =====
FROM maven:3.9.7-eclipse-temurin-17 AS build
WORKDIR /workspace

# Descarga dependencias a cache
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -DskipTests dependency:go-offline

# Compila
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -DskipTests package

# ===== Runtime =====
FROM eclipse-temurin:17-jre-jammy
WORKDIR /opt/app

RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates wget \
  && rm -rf /var/lib/apt/lists/*

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=25 -Duser.timezone=UTC"

# copia jar
COPY --from=build /workspace/target/*.jar /opt/app/app.jar

# usuario no root
RUN useradd -r -u 1000 appuser
USER 1000:1000

EXPOSE 8080

# healthcheck a actuator
HEALTHCHECK --interval=15s --timeout=5s --retries=20 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /opt/app/app.jar"]
