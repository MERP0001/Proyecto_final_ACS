# Sistema de Gestión de Inventarios

Este proyecto es una aplicación web full-stack diseñada para la gestión de un inventario de productos. La arquitectura sigue un enfoque moderno de microservicios, con un backend robusto desarrollado en Spring Boot y un frontend interactivo construido con Next.js y TypeScript.

Todo el sistema está contenerizado con Docker, lo que garantiza un despliegue fácil, consistente y escalable, y sigue las mejores prácticas de Aseguramiento de la Calidad del Software (QAS) a través de un completo ecosistema de pruebas.

## 🏗️ Arquitectura del Proyecto

El sistema se compone de tres servicios principales orquestados a través de `docker-compose`. La comunicación fluye desde el cliente (navegador) hacia el frontend de Next.js, que a su vez consume la API REST del backend de Spring Boot. El backend maneja toda la lógica de negocio y persiste los datos en la base de datos PostgreSQL.

```mermaid
graph TD
    subgraph "Cliente"
        B(Navegador Web)
    end

    subgraph "Infraestructura Docker"
        subgraph "Servicio Frontend"
            F[Next.js]
        end

        subgraph "Servicio Backend"
            S[Spring Boot]
        end

        subgraph "Servicio de Base de Datos"
            DB[(PostgreSQL)]
        end
    end

    B --&gt;|Peticiones HTTP| F
    F --&gt;|Llamadas a la API REST| S
    S &lt;--&gt;|JPA/JDBC| DB
```

1.  **`backend`**: Un servicio RESTful construido con **Spring Boot** que maneja toda la lógica de negocio, la seguridad y la comunicación con la base de datos.
2.  **`frontend`**: Una Single Page Application (SPA) desarrollada con **Next.js** que consume la API del backend para ofrecer una interfaz de usuario moderna y reactiva.
3.  **`db`**: Una instancia de **PostgreSQL** que sirve como la base de datos principal del sistema.

---

## ☕ Backend (Spring Boot)

El backend, ubicado en el directorio raíz, está desarrollado en **Java 21** con el framework **Spring Boot 3**. Es el núcleo de la aplicación, responsable de la lógica de negocio, la persistencia de datos y la seguridad.

### Principales Dependencias
- **Spring Web**: Para construir la API RESTful.
- **Spring Data JPA**: Para la capa de persistencia de datos de forma simplificada.
- **Spring Security**: Para gestionar la autenticación y autorización.
- **JJwt**: Para la implementación de JSON Web Tokens.
- **PostgreSQL Driver**: Para la conexión con la base de datos.
- **Flyway**: Para la gestión de migraciones de la base de datos.
- **Lombok**: Para reducir el código repetitivo en las entidades y DTOs.

### Estructura del Código

- **`config`**: Clases de configuración para Spring Security, JWT, CORS y mapeo de contraseñas (BCrypt).
- **`controller`**: Controladores REST (`ProductoController`, `AuthController`) que exponen los endpoints de la API.
- **`dto`**: Data Transfer Objects (DTOs) para desacoplar la API de las entidades de la base de datos.
- **`entity`**: Entidades JPA (`Producto`, `User`) que mapean las tablas de la base de datos.
- **`exception`**: Clases para el manejo de excepciones personalizadas y un manejador global (`GlobalExceptionHandler`) que centraliza las respuestas de error.
- **`mapper`**: Mapeadores (`ProductoMapper`) para convertir entre DTOs y Entidades, utilizando MapStruct.
- **`repository`**: Repositorios de Spring Data JPA (`ProductoRepository`, `UserRepository`) para el acceso a datos.
- **`service`**: Lógica de negocio (`ProductoService`, `UserService`).

### 🔐 Seguridad con JWT

La seguridad de la API se gestiona a través de `Spring Security`. Las rutas protegidas requieren un token JWT válido en la cabecera `Authorization: Bearer <token>`.

- **`JwtAuthenticationFilter`**: Un filtro que se ejecuta en cada petición para interceptar, validar el token JWT y establecer el contexto de seguridad.
- **`JwtService`**: Utilidad para la creación (usando los `claims` del usuario) y validación de los tokens (firma, expiración).
- **`SecurityConfig`**: Configuración central donde se define la cadena de filtros de seguridad, se desactiva CSRF (ya que es una API sin estado), se configuran las políticas de CORS y se definen las rutas públicas (`/auth/**`) y las privadas (`/api/**`).
- **`AuthController`**: Endpoints para el registro (`/auth/register`) y login (`/auth/login`). Al hacer login, se devuelve un `AuthResponse` con el token JWT.

### 🗃️ API Endpoints

El `ProductoController` expone una API REST para las operaciones CRUD sobre los productos. Todos los endpoints bajo `/api` requieren autenticación.

| Método HTTP | Ruta                | Descripción                              |
| :---------- | :------------------ | :--------------------------------------- |
| `GET`       | `/api/productos`    | Obtiene una lista de todos los productos.  |
| `GET`       | `/api/productos/{id}` | Obtiene un producto por su ID.           |
| `POST`      | `/api/productos`    | Crea un nuevo producto.                  |
| `PUT`       | `/api/productos/{id}` | Actualiza un producto existente.         |
| `DELETE`    | `/api/productos/{id}` | Elimina un producto.                     |

### 🐘 Gestión de Base de Datos con Flyway

El versionado del esquema de la base de datos se maneja con **Flyway**.
- **Ubicación**: Los scripts de migración SQL se encuentran en `src/main/resources/db/migration`.
- **Nomenclatura**: Siguen el formato `V<VERSION>__<DESCRIPCION>.sql` (e.g., `V1__Create_productos_table.sql`).
- **Ejecución**: Flyway los aplica automáticamente en orden al iniciar el backend, asegurando que la base de datos siempre esté sincronizada con la versión del código.

---

## 🧪 Estrategia de Pruebas (QAS)

El proyecto pone un fuerte énfasis en la calidad del software (QAS) a través de una estrategia de pruebas completa que abarca tanto el backend como el frontend.

### Pruebas de Backend (Cucumber y JUnit)

1.  **Pruebas de Comportamiento (BDD) con Cucumber**:
    - **Filosofía**: Se definen escenarios de prueba en lenguaje Gherkin (fácil de entender para perfiles no técnicos) en archivos `.feature` ubicados en `src/test/resources/features/`.
    - **Ejemplo de Escenario (`producto-simple.feature`)**:
      ```gherkin
      Scenario: Crear un nuevo producto
        Given que soy un usuario autenticado
        When creo un nuevo producto con nombre "Laptop", descripción "Potente laptop", precio 1500.00 y stock 10
        Then el producto es creado exitosamente
      ```
    - **Implementación**: Los pasos (steps) de Gherkin se implementan en Java en la ruta `src/test/java/org/example/proyectofinal/cucumber/steps/`. Estos steps utilizan `TestRestTemplate` para realizar llamadas HTTP reales a la aplicación durante las pruebas.
    - **Configuración**: `CucumberSpringConfiguration` arranca un contexto de Spring Boot para las pruebas en un puerto aleatorio, asegurando un entorno de prueba realista.

2.  **Pruebas de Integración con JUnit**:
    - Se utilizan para probar la integración entre las capas de la aplicación (Controller, Service, Repository).
    - Se configuran con el perfil de Spring `test` (`application-test.properties`), que utiliza una base de datos en memoria H2 para aislar las pruebas y no depender de una instancia de PostgreSQL.

### Pruebas de Frontend (Playwright)

Para el frontend, se realizan pruebas **End-to-End (E2E)** utilizando **Playwright** para simular la interacción real de un usuario.

- **Ubicación**: Las pruebas están en `front-inventario/tests/`.
- **Configuración (`playwright.config.ts`)**: Se define la URL base de la aplicación, los navegadores a probar (Chromium, Firefox, WebKit) y los viewports (escritorio y móvil).
- **Autenticación Optimizada (`global.setup.ts`)**: Antes de ejecutar las pruebas, este script realiza un login programático y guarda el estado de autenticación (cookies, local storage) en un archivo. Cada prueba carga este estado, evitando tener que hacer login en cada test y acelerando la ejecución.
- **Cobertura de Flujos**:
    - `login.spec.ts`: Iniciar sesión con credenciales válidas e inválidas.
    - `productos.spec.ts`: Navegar a la página de productos, crear un nuevo producto y verificar que aparece en la tabla.
    - `logout.spec.ts`: Cerrar la sesión del usuario.
    - `responsive.spec.ts`: Validar que la interfaz se adapta correctamente a dispositivos móviles.

---

## 🐳 Contenerización con Docker

El proyecto está completamente contenerizado, lo que permite un entorno de desarrollo, pruebas y producción consistente y reproducible.

- **`Dockerfile`**: Define la imagen del backend. Utiliza una **build multi-etapa**:
    1.  **Etapa `build`**: Usa una imagen de `maven` para compilar el código fuente y empaquetar la aplicación en un archivo JAR.
    2.  **Etapa final**: Copia el JAR generado a una imagen base ligera de Java (`eclipse-temurin:21-jre-jammy`), resultando en una imagen final optimizada y más segura.

- **`docker-compose.yml`**: Orquesta el despliegue de todos los servicios:
    - **`db`**:
        - `image: postgres:15.2`: Usa la imagen oficial de PostgreSQL.
        - `volumes`: Monta un volumen (`postgres_data`) para persistir los datos de la base de datos entre reinicios del contenedor. Monta el script `init-db` para la creación inicial de la base de datos.
        - `environment`: Configura las credenciales de la base de datos.
    - **`backend`**:
        - `build: .`: Construye la imagen a partir del `Dockerfile` en el directorio actual.
        - `ports`: Mapea el puerto 8080 del contenedor al 8080 del host.
        - `depends_on`: Especifica que el backend no debe iniciar hasta que el contenedor `db` esté saludable.
    - **`frontend`**:
        - `build: ./front-inventario`: Construye la imagen a partir del `Dockerfile` del frontend.
        - `ports`: Mapea el puerto 3000 del contenedor al 3000 del host.
        - `depends_on`: Depende del `backend` para asegurar que la API esté disponible.

---

## ⚛️ Frontend (Next.js)

La interfaz de usuario, ubicada en `front-inventario/`, es una aplicación web moderna construida con **Next.js 14**, **React** y **TypeScript**.

### Principales Dependencias
- **React y React DOM**: Para construir la interfaz de usuario.
- **Tailwind CSS**: Para un estilizado rápido y utilitario.
- **shadcn/ui**: Colección de componentes de UI reutilizables, accesibles y personalizables.
- **Zod**: Para la validación de esquemas y tipos.
- **React Hook Form**: Para la gestión eficiente de formularios.
- **Lucide React**: Para los iconos.

### Estructura de Rutas y Componentes
La aplicación utiliza el **App Router** de Next.js:
- **`app/layout.tsx`**: Layout raíz de la aplicación.
- **`app/page.tsx`**: Página de inicio pública.
- **`app/login/page.tsx`**: Página de inicio de sesión.
- **`app/(dashboard)`**: Es un **grupo de rutas**. Las páginas dentro de este directorio (`dashboard`, `productos`) compartirán el layout `app/(dashboard)/layout.tsx`, que contiene la barra de navegación y el menú lateral.
- **`components`**:
    - **`layout`**: Componentes de la estructura principal (sidebar, navegación).
    - **`ui`**: Componentes de bajo nivel de `shadcn/ui` (botones, inputs, tablas, etc.).
    - **`auth`**: Componentes relacionados con la autenticación.
- **`lib`**: Contiene utilidades (`utils.ts`) y el cliente de API centralizado (`api.ts`) que gestiona las peticiones `fetch` y el manejo de tokens.
- **`services`**: Lógica de negocio del frontend (`auth.ts`, `productos.ts`) que utiliza `api.ts` para comunicarse con el backend.
- **`contexts`**: `AuthContext` para gestionar el estado de autenticación (usuario, token) de forma global.

### Rutas Protegidas

El componente `src/components/auth/auth-guard.tsx` actúa como un guardián para las rutas protegidas. Utiliza el `AuthContext` para verificar si el usuario está autenticado. Si no lo está, utiliza el hook `useRouter` de Next.js para redirigirlo a la página de `/login`. Este componente envuelve a los layouts de las rutas que requieren autenticación.

---

## 🚀 Cómo Ejecutar el Proyecto

### Prerrequisitos

- **Docker** y **Docker Compose**
- **Node.js** y **npm** (para ejecución local del frontend)
- **JDK 21** y **Gradle** (para ejecución local del backend)

### Método 1: Ejecución con Docker (Recomendado)

1.  **Clona el repositorio:**
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    cd <NOMBRE_DEL_PROYECTO>
    ```

2.  **Construye y levanta los contenedores:**
    Desde la raíz del proyecto, ejecuta:
    ```bash
    docker-compose up --build
    ```
    Este comando construirá y iniciará todos los servicios.

3.  **Accede a la aplicación:**
    - **Frontend**: `http://localhost:3000`
    - **Backend API**: `http://localhost:8080`

4.  **Credenciales de prueba:**
    - **Usuario:** `admin@example.com`
    - **Contraseña:** `password`

### Método 2: Ejecución Local (Para Desarrollo)

#### Backend
```bash
# Desde la raíz del proyecto
./gradlew bootRun
```

#### Frontend
```bash
# Desde el directorio front-inventario
cd front-inventario
npm install
npm run dev
```

### Ejecutar las Pruebas

#### Pruebas de Backend
```bash
# Desde la raíz del proyecto
./gradlew test
# El reporte de Cucumber se genera en target/cucumber-reports/Cucumber.html
```

#### Pruebas de Frontend
```bash
# Desde el directorio front-inventario
cd front-inventario
npx playwright test
# Para ver el reporte UI
npx playwright show-report
```

### Detener la Aplicación (Docker)
Presiona `Ctrl + C` en la terminal donde se está ejecutando `docker-compose` y luego ejecuta:
```bash
docker-compose down
```
Este comando detendrá y eliminará los contenedores. Para eliminar también los volúmenes (perder los datos de la base de datos), usa `docker-compose down -v`. 