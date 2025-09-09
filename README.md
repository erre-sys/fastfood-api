# API – Plantilla de servicio (Spring Boot + Docker + GHCR + CI/CD)

## ¿Qué es esto?

* **Un esqueleto **multi-módulo** de Spring Boot con:
* **Ejecución local** (perfil `dev`).
* **Build de imagen** Docker **multi-stage**.
* **Publicación** en GHCR.
* **Despliegue** automatizado por GitHub Actions (self-hosted runner). 

>Se diseñó para ser **reutilizable**: clónalo para nuevos servicios/microservicios cambiando nombres, `MODULE`, puertos y properties.

---
## Estructura del proyecto
   .
   ├─ pom.xml                     # POM raíz (gestiona módulos/versión/java)
   ├─ Dockerfile                  # Build multi-stage (recibe ARG MODULE=...)
   ├─ .dockerignore               # Evita enviar basura al build context
   ├─ bootstrap/                  # Módulo ejecutable (tiene @SpringBootApplication)
   │  ├─ pom.xml
   │  └─ src/main/resources/
   │     ├─ application.properties        # config base
   │     ├─ application-dev.properties    # dev (opcional)
   │     └─ logback-spring.xml            # si usas logback custom (opcional)
   ├─ domain/                     # Dominio/entidades/casos de uso (sin frameworks)
   │  └─ pom.xml
   ├─ infrastructure/             # Adaptadores (JPA, REST, Security, etc.)
   │  └─ pom.xml
   └─ share/                      # Utilidades compartidas (DTOs, utils)
   └─ pom.xml


> Regla: la clase @SpringBootApplication vive en bootstrap.

## Perfiles y configuración

* `application.properties` (base):
* `server.port`=`${SERVER_PORT:8080}`
* `server.servlet.context-path`=`/fastfood/api`
* `spring.jpa.hibernate.ddl-auto`=`validate`
* `management.health.jms.enabled`=`false` si no usas JMS
* `spring.devtools.restart.enabled`=`false` si DevTools molesta
* `application-dev.properties` (local):
>Datasource a tu túnel SSH (ej. jdbc:mysql://127.0.0.1:3307/fastfood).

>Para seguridad con Keycloak: `issuer-uri` en `prod` y, si es self-signed, confiar el cert o usar mkcert en dev.

----
## Dockerfile 

>Compilar en una imagen con Maven (rápida, con caché), y ejecutar en una JRE mínima.

Puntos clave:

* `ARG MODULE=bootstrap` → indica qué módulo compila el JAR ejecutable.

* Primero copiamos solo POMs para caché de dependencias `go-offline`.
Luego copiamos el código y corremos `mvn package`.

* En runtime, copiamos `/app/${MODULE}/target/*.jar` como `/opt/app/app.jar`.

* Creamos carpeta de logs (`LOG_DIR`) y usuario no root (`UID 1000`).

* Healthcheck consulta `/actuator/health` (ajústalo si usas `context-path` distinto).

Build local rápido:

```bash 
docker build -t fastfood:dev --build-arg MODULE=bootstrap .
docker run --rm -e LOG_DIR=/opt/app/logs -p 8080:8080 fastfood:dev
curl http://127.0.0.1:8080/fastfood/api/actuator/health
```


`.dockerignore` mínimo:
```bash
.git
**/target
.mvn
.mvnw*
.idea
.vscode
```
---
# Ejecución local (DEV)

BD remota por túnel SSH (seguro, sin exponer MySQL):

```bash
ssh -N -L 3307:127.0.0.1:3306 deploy@<IP_del_servidor>
```

Levantar el API con perfil dev:

```bash
./mvnw -pl bootstrap -am spring-boot:run -Dspring-boot.run.profiles=dev
```

Probar:
```bash
curl http://localhost:9055/fastfood/api/actuator/health
```
---
# GitHub Actions (CI/CD)

Workflow típico (`.github/workflows/api-ci.yml`):

* **Triggers**
  * `push` a `main` y `dev`.
  * Tags: `dev-*` (deploy a dev), `prod-*` (deploy a prod).
  * `workflow_dispatch` manual.


* **Jobs**
  1. **build-push** (ubuntu):
     * checkout + java 17
     * `mvn -pl bootstrap -am -DskipTests package`
     * `docker buildx build` con `MODULE=bootstrap`
     * push a GHCR: `ghcr.io/<owner>/fastfood-api:<tag>`
     
  2. **deploy-dev** (self-hosted, sólo si tag dev-*):
    * SSH al server
    * `docker compose pull api-fastfood && up -d api-fastfood`
    * Healthcheck `https://api.<tu_dominio>/health`

  3. deploy-prod (self-hosted, sólo si tag prod-*):
     * Igual que dev, apuntando a prod (idealmente con aprobación manual).


* **Secrets necesarios**

    * `SSH_HOST`, `SSH_USER`, `SSH_KEY`, `SSH_PORT`
    * `GITHUB_TOKEN` (lo inyecta Actions para GHCR)


* **Convención de tags**
  * Merge a `main` → publica `:dev` y `:sha-XXXXXXX`. 
  * Crear tag `dev-YYYYMMDD` → despliega a dev. 
  * Crear tag `prod-YYYYMMDD` → despliega a prod (con gate manual recomendado).

---
# Integración con el server (compose + nginx)

* El compose del server referencia la imagen: `image: ghcr.io/<owner>/fastfood-api:dev`
y expone el puerto interno **8090** hacia Nginx (reverse proxy TLS).

* `nginx` enruta:
  * `https://api.tu-dominio/` → `http://127.0.0.1:8090/fastfood/api/`

* **Health** esperado en servidor:
  * `https://api.tu-dominio/health` → `UP`

---
# Troubleshooting mínimo

* **“Main class not found” →** define `<mainClass>` en `bootstrap/pom.xml`.
* **“/src not found” en Docker →** revisa `MODULE` y `context`; no ignores `src/`.
* **TLS con Keycloak** (issuer HTTPS self-signed) → importar cert al JDK o usar mkcert.
* **JMS WARN** → desactiva `management.health.jms.enabled=false` si no usas broker.
* **Puerto ocupado** → fija `server.port` por perfil y desactiva `devtools.restart` si reinicia en bucle.

---
# Generar

* Se puede crear el servicio así:
  ```bash
  mvn archetype:generate \
  -DarchetypeGroupId=<tu_group> \
  -DarchetypeArtifactId=fastboot-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.empresa \
  -DartifactId=pedido-service \
  -Dpackage=com.empresa.pedido
  ```

* Crear desde el proyecto:
  ```bash
    mvn -DskipTests archetype:create-from-project
    cd target/generated-sources/archetype
    mvn install    # publica el archetype en tu repo local (o a un registry interno)

  ```