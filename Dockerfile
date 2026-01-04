# ===== Builder =====
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

# --- Elegir módulo que genera el jar ejecutable ---
# Usa 'bootstrap' si ahí vive tu @SpringBootApplication.
# Si el jar sale de 'infrastructure', pásalo como build-arg MODULE=infrastructure
ARG MODULE=bootstrap

# Copiamos solo poms para calentar caché de dependencias
COPY pom.xml .
COPY bootstrap/pom.xml bootstrap/pom.xml
COPY domain/pom.xml domain/pom.xml
COPY infrastructure/pom.xml infrastructure/pom.xml
COPY share/pom.xml share/pom.xml

# Descarga dependencias a caché sin compilar código aún
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -pl ${MODULE} -am dependency:go-offline

# Ahora sí copiamos el código
COPY . .

# Compila sólo el módulo elegido (y lo necesario con -am)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -pl ${MODULE} -am package

# ===== Runtime =====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /opt/app

# (opcional) certificados / wget para healthcheck
RUN apt-get update \
 && apt-get install -y --no-install-recommends ca-certificates wget \
 && rm -rf /var/lib/apt/lists/*

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=25 -Duser.timezone=UTC"

# ...
ENV LOG_DIR=/opt/app/logs
RUN mkdir -p "$LOG_DIR" && chown -R 1000:1000 "$LOG_DIR"

# copiamos el jar compilado del módulo elegido
ARG MODULE=bootstrap
COPY --from=build /app/${MODULE}/target/*.jar /opt/app/app.jar

# usuario no root
RUN useradd -r -u 1000 appuser
USER 1000:1000

EXPOSE 8080 8090

# Health check con context-path /fastfood/api
# Usa SERVER_PORT variable de entorno, fallback a 8080
HEALTHCHECK --interval=15s --timeout=5s --retries=20 \
  CMD wget -qO- http://127.0.0.1:${SERVER_PORT:-8080}/fastfood/api/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /opt/app/app.jar"]
