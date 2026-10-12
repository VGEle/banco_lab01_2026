# Laboratorio 2: Implementación de CI/CD

[![CI/CD Pipeline](https://github.com/VGEle/banco_lab01_2026/actions/workflows/build.yml/badge.svg?branch=laboratorio-2)](https://github.com/VGEle/banco_lab01_2026/actions/workflows/build.yml?query=branch%3Alaboratorio-2)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=VGEle_banco_lab01_2026&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=VGEle_banco_lab01_2026)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=VGEle_banco_lab01_2026&metric=coverage)](https://sonarcloud.io/summary/new_code?id=VGEle_banco_lab01_2026)
[![Known Vulnerabilities](https://snyk.io/test/github/VGEle/banco_lab01_2026/badge.svg)](https://snyk.io/test/github/VGEle/banco_lab01_2026)

Este laboratorio toma como aplicación base el proyecto bancario desarrollado en el Laboratorio 1 y le incorpora un entorno de integración y entrega continuas (CI/CD) mediante GitHub Actions y herramientas de calidad, cobertura, seguridad y despliegue.

La primera parte del laboratorio agrega endpoints de demostración con Java Faker para validar el funcionamiento del backend y del pipeline antes de automatizar las siguientes etapas.

## Aplicación base: Laboratorio 1

## Introducción

Este proyecto implementa una aplicación web bancaria para administrar clientes, cuentas y transferencias de dinero. La solución está compuesta por un backend desarrollado con Spring Boot, un frontend desarrollado con React y una base de datos MySQL.

La aplicación permite crear, consultar, actualizar y eliminar clientes, realizar transferencias entre cuentas y consultar el historial de movimientos asociado a cada cuenta.

## Objetivos

### Objetivo general

Desarrollar una aplicación web full stack que simule las operaciones básicas de un banco utilizando Spring Boot, React, Axios y MySQL.

### Objetivos específicos

- Implementar una API REST con Spring Boot.
- Persistir clientes, saldos y transacciones en MySQL.
- Organizar el backend mediante una arquitectura por capas.
- Aplicar DTOs y mappers para separar los contratos de la API de las entidades persistentes.
- Validar los datos de entrada y las reglas de negocio de las transferencias.
- Construir un frontend React con las vistas de clientes, transferencias e historial.
- Integrar el frontend y el backend mediante Axios y el proxy de Vite.

## Herramientas de software empleadas

- IntelliJ IDEA para el desarrollo del backend.
- Visual Studio Code para el desarrollo del frontend.
- Java 17 y Spring Boot 3.5.14.
- Maven Wrapper para la construcción y ejecución del backend.
- Spring Web para la creación de la API REST.
- Spring Data JPA e Hibernate para la persistencia.
- MySQL como sistema gestor de base de datos.
- React 19.2.8 para la interfaz de usuario.
- Vite 8.2.2 como herramienta de desarrollo y construcción del frontend.
- Axios para las solicitudes HTTP.
- Lombok para reducir código repetitivo.
- MapStruct para convertir entidades y DTOs.
- Bean Validation para validar los datos recibidos por la API.
- Git y GitHub para el control de versiones.

## Arquitectura propuesta

La aplicación utiliza una arquitectura por capas. Cada capa tiene una responsabilidad específica:

```text
Frontend React
      │ Axios / proxy /api
      ▼
Controladores REST
      ▼
Servicios y reglas de negocio
      ▼
Repositorios Spring Data JPA
      ▼
Base de datos MySQL
```

### Capas del backend

- **Controller:** recibe las solicitudes HTTP y devuelve las respuestas de la API.
- **Service:** contiene la lógica de negocio, las validaciones y la operación transaccional de las transferencias.
- **Repository:** proporciona el acceso a las tablas mediante Spring Data JPA.
- **Entity:** representa las tablas `customers` y `transactions`.
- **DTO:** define los datos que se reciben y se envían mediante la API.
- **Mapper:** convierte entidades en DTOs y DTOs en entidades mediante MapStruct.

El frontend está organizado en componentes y vistas independientes para clientes, transferencias e historial. Vite redirige las solicitudes `/api` hacia el backend que se ejecuta en el puerto 8080.

## Procedimiento

### 1. Configuración de la base de datos

Se creó la base de datos `lab12026p` y el usuario específico `lab_user`. La configuración de conexión se encuentra en `src/main/resources/application.properties`.

La contraseña se proporciona mediante la variable de entorno `DB_PASSWORD`, evitando guardarla directamente en el repositorio. Hibernate utiliza `ddl-auto=update` para crear o actualizar las tablas sin eliminar los datos existentes.

### 2. Desarrollo del backend

Se implementaron las entidades `Customer` y `Transaction`, junto con sus respectivos repositorios, DTOs, mappers, servicios y controladores.

La transferencia de dinero se ejecuta dentro de una operación `@Transactional`. Antes de guardar la transacción se verifica que:

- Las dos cuentas hayan sido indicadas.
- Las cuentas de origen y destino sean diferentes.
- El monto sea positivo y válido.
- Ambas cuentas existan.
- La cuenta de origen tenga saldo suficiente.

Si ocurre un error, la operación se revierte para evitar saldos inconsistentes.

### 3. Endpoints implementados

#### Clientes

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/api/customers` | Lista todos los clientes. |
| GET | `/api/customers/{id}` | Consulta un cliente por ID. |
| POST | `/api/customers` | Crea un cliente. |
| PUT | `/api/customers/{id}` | Actualiza un cliente. |
| DELETE | `/api/customers/{id}` | Elimina un cliente. |

#### Transacciones

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/api/transactions` | Lista todas las transacciones. |
| GET | `/api/transactions/{id}` | Consulta una transacción por ID. |
| GET | `/api/transactions/account/{accountNumber}` | Consulta el historial de una cuenta. |
| POST | `/api/transactions` | Realiza una transferencia. |
| PUT | `/api/transactions/{id}` | Actualiza una transferencia y ajusta los saldos. |
| DELETE | `/api/transactions/{id}` | Elimina una transferencia y revierte sus saldos. |

### 4. Desarrollo del frontend

El frontend contiene tres vistas principales:

1. **Clientes:** consulta la lista, crea clientes, edita sus datos y permite eliminarlos.
2. **Transferencias:** permite seleccionar las cuentas de origen y destino e indicar el monto.
3. **Historial:** muestra los movimientos de una cuenta, indicando si corresponden a una entrada o una salida.

### 5. Ejecución local

Con MySQL iniciado, el backend se ejecuta desde la raíz del proyecto con:

```bash
export DB_PASSWORD='contraseña_de_lab_user'
./mvnw spring-boot:run
```

El frontend se ejecuta en otra terminal con:

```bash
cd frontend
npm ci
npm run dev
```

La aplicación queda disponible en `http://localhost:5173` y la API en `http://localhost:8080`.

### Endpoints de datos aleatorios del Lab 2

El Lab 2 incorpora endpoints de demostración generados con Java Faker:

- `GET /` — verificación de estado.
- `GET /version` — versión de la aplicación.
- `GET /nations` — diez naciones aleatorias.
- `GET /currencies` — veinte monedas aleatorias.
- `GET /aviation` — veinte registros de aviación aleatorios.

### 6. Verificación

Se verificó que el backend compila correctamente. También se ejecutaron satisfactoriamente las validaciones del frontend:

```bash
npm run lint
npm run build
```

Las pruebas automatizadas del backend se ejecutan con:

```bash
./mvnw test
```

Estas pruebas utilizan H2 únicamente durante el perfil `test`, por lo que no reemplazan la base MySQL de producción. Actualmente verifican el arranque de Spring, una transferencia exitosa y el rechazo de una transferencia sin saldo suficiente.

### Configuración de SonarCloud

El workflow incluye un trabajo de análisis SonarCloud que se ejecuta después de las pruebas. Para activarlo en GitHub, configura en `Settings > Secrets and variables > Actions`:

- Variable `SONAR_PROJECT_KEY`: clave del proyecto creado en SonarCloud.
- Variable `SONAR_ORGANIZATION`: clave de la organización de SonarCloud.
- Secreto `SONAR_TOKEN`: token generado en SonarCloud.

El token no se guarda en el repositorio y el análisis depende del trabajo `tests`, por lo que no se ejecuta si las pruebas fallan.

### Cobertura de código con JaCoCo

Maven genera el reporte XML de cobertura en `target/site/jacoco/jacoco.xml`. El trabajo de SonarCloud utiliza este archivo para mostrar la cobertura del código analizado.

Para generar el reporte localmente:

```bash
./mvnw clean test
```

### Artefacto JAR

Después de las pruebas y del análisis de calidad, GitHub Actions construye el archivo `lab12026p-0.0.1-SNAPSHOT.jar` y lo publica como artefacto descargable del workflow.

### Publicación de la imagen Docker

Después de construir el JAR, GitHub Actions construye y publica la imagen `banco-lab01-2026:latest` en Docker Hub. El repositorio requiere los secretos `DOCKER_USERNAME` y `DOCKER_PASSWORD` en `Settings > Secrets and variables > Actions`.

Además, se probó manualmente la creación de clientes, la realización de transferencias y la consulta del historial desde la interfaz web.

### Análisis de seguridad con Snyk

El workflow ejecuta un análisis de las dependencias Maven con Snyk y reporta vulnerabilidades de severidad alta o crítica. Para activarlo en GitHub, genera un token en [Snyk](https://snyk.io/) y agrégalo en `Settings > Secrets and variables > Actions` con el nombre:

- Secreto `SNYK_TOKEN`: token de autenticación de Snyk.

El token se utiliza únicamente desde GitHub Actions y no se guarda en el repositorio.

El análisis de Snyk está configurado con `continue-on-error: true`. Esto permite que el pipeline informe las vulnerabilidades encontradas sin impedir la construcción y el despliegue de la aplicación.

### Flujo CI/CD completo

El archivo `.github/workflows/build.yml` contiene las siguientes etapas:

1. Ejecutar las pruebas unitarias con Maven.
2. Analizar la calidad y cobertura del código con SonarCloud.
3. Analizar las dependencias con Snyk.
4. Construir el archivo JAR.
5. Construir y publicar la imagen Docker en Docker Hub.
6. Ejecutar el despliegue automático en Railway mediante Railway CLI.

Los trabajos de publicación y despliegue se ejecutan en eventos `push`. En Pull Requests se ejecutan las validaciones, pero no se publican imágenes ni se despliega la aplicación.

### Imagen Docker

La imagen pública utilizada para desplegar la aplicación es:

```text
elenavg26/banco-lab01-2026:latest
```

Repositorio en Docker Hub:

<https://hub.docker.com/r/elenavg26/banco-lab01-2026>

### Despliegue en Railway

La aplicación se despliega en Railway a partir de la imagen Docker publicada en Docker Hub. El servicio utiliza el puerto `8080` y está conectado a un servicio MySQL administrado por Railway.

URL pública de la aplicación:

<https://awake-radiance-production.up.railway.app>

Las variables de conexión se configuran en Railway mediante referencias al servicio MySQL:

```text
SERVER_PORT=8080
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

El despliegue automático se activa desde GitHub Actions mediante el secreto `RAILWAY_TOKEN`. Este secreto permite ejecutar:

```bash
railway redeploy --service awake-radiance --yes
```

Las credenciales y tokens se mantienen únicamente en los secretos de GitHub y no se almacenan en el repositorio.

## Conclusiones

El laboratorio permitió aplicar los conceptos fundamentales de Spring Boot en una aplicación funcional. Se implementó una API REST con persistencia en MySQL y una interfaz React integrada mediante Axios.

La separación por capas facilita el mantenimiento del sistema y permite separar la presentación, las reglas de negocio y el acceso a datos. El uso de transacciones garantiza que una transferencia actualice los saldos y registre el movimiento de forma consistente.

La aplicación cumple con las funcionalidades solicitadas: gestión completa de clientes, transferencias entre cuentas e historial de movimientos por cliente.

## Bibliografía

- Spring Boot Documentation: <https://docs.spring.io/spring-boot/>
- Spring Data JPA Documentation: <https://spring.io/projects/spring-data-jpa>
- Spring Initializr: <https://start.spring.io/>
- React Documentation: <https://react.dev/>
- Vite Documentation: <https://vite.dev/>
- MySQL Documentation: <https://dev.mysql.com/doc/>
- Maven Documentation: <https://maven.apache.org/guides/>
- Guía del laboratorio: `Lab1_2026-2_Introduccion_SpringBoot.md`.

## Proyecto anexo en GitHub

Repositorio del proyecto:

<https://github.com/VGEle/banco_lab01_2026>

