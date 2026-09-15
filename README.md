# Informe de Laboratorio 1: Introducción a Spring Boot

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
- Java 17 y Spring Boot 3.5.11.
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

### 6. Verificación

Se verificó que el backend compila correctamente. También se ejecutaron satisfactoriamente las validaciones del frontend:

```bash
npm run lint
npm run build
```

Además, se probó manualmente la creación de clientes, la realización de transferencias y la consulta del historial desde la interfaz web.

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

