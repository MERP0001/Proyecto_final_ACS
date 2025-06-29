# [cite_start]Pontificia Universidad Católica Madre y Maestra [cite: 1]

## [cite_start]Facultad de Ciencias e Ingeniería [cite: 1]

### [cite_start]Escuela de Ingeniería en Computación y Telecomunicaciones [cite: 1]

## [cite_start]Aseguramiento Calidad Software [cite: 1]

## [cite_start]Avance Proyecto Final [cite: 1]

### [cite_start]Sistema de Gestión de Inventarios con QAS [cite: 1]

**Descripción General:**

[cite_start]El objetivo del proyecto es desarrollar un sistema de gestión de inventarios para una pequeña empresa, que cubra todas las etapas del ciclo de vida del aseguramiento de la calidad del software (QAS). [cite: 1] [cite_start]El sistema debe incluir funcionalidades básicas de gestión de productos, control de stock y más, además de cumplir con altos estándares de calidad, seguridad y usabilidad. [cite: 2] [cite_start]El proyecto debe ser realizado en grupos de máximo dos estudiantes. [cite: 3]

### [cite_start]Funcionalidades del Sistema de Gestión de Inventarios [cite: 4]

1.  [cite_start]**Gestión de Productos** [cite: 4]
    * [cite_start]**Agregar Producto**: Permitir a los usuarios agregar nuevos productos al inventario, incluyendo detalles como nombre, descripción, categoría, precio y cantidad inicial. [cite: 5]
    * [cite_start]**Editar Producto**: Permitir la edición de la información de un producto existente. [cite: 6]
    * [cite_start]**Eliminar Producto**: Permitir la eliminación de productos del inventario. [cite: 7]
    * [cite_start]**Visualizar Productos**: Mostrar una lista de todos los productos en el inventario con opciones de búsqueda y filtrado. [cite: 8]

2.  [cite_start]**Control de Stock** [cite: 9]
    * [cite_start]No requerido. [cite: 9]

3.  [cite_start]**Integración con Otros Sistemas** [cite: 10]
    * [cite_start]**API de Integración**: Proporcionar una API para integrar el sistema con otras aplicaciones (por ejemplo, sistemas de contabilidad o puntos de venta). [cite: 10]

4.  [cite_start]**Interfaz de Usuario Amigable** [cite: 11]
    * [cite_start]No requerido. [cite: 11]

### Roles y Niveles de Acceso

1.  [cite_start]**Administrador** [cite: 12]
    * [cite_start]**Descripción**: Tiene acceso completo a todas las funcionalidades del sistema. [cite: 12]
    * **Permisos**:
        * [cite_start]**Gestión de Productos**: Agregar, editar, eliminar y visualizar productos. [cite: 13]
        * [cite_start]**Control de Stock**: Actualizar stock y visualizar historial de movimientos. [cite: 13]

2.  [cite_start]**Empleado** [cite: 14]
    * [cite_start]No requerido. [cite: 14]

3.  [cite_start]**Usuario Invitado/Cliente** [cite: 14]
    * [cite_start]No requerido. [cite: 14]

| Funcionalidad         | Administrador | Empleado | Invitado |
| :-------------------- | :------------ | :------- | :------- |
| Gestión de Productos  | CRUD          | CRUD     | R        |
| Control de Stock      | CRUD          | CRUD     |          |
| API de Integración    | JWT           |          |          |
[cite_start][cite: 15]

### [cite_start]Etapas del Ciclo de Vida del Aseguramiento de la Calidad del Software (QAS) [cite: 16]

1.  [cite_start]**Planificación y Gestión de Proyectos**: [cite: 16]
    * [cite_start]**Plan de Proyecto**: Definir claramente el alcance, los objetivos, los entregables y el cronograma del proyecto. [cite: 17]
    * [cite_start]**Gestión de Tareas**: Utilizar herramientas como Jira, Trello o Asana para gestionar las tareas del proyecto y realizar un seguimiento del progreso. [cite: 18]
    * [cite_start]**Gestión de Riesgos**: Identificar y documentar los posibles riesgos y planificar estrategias de mitigación. [cite: 19]

2.  [cite_start]**Pruebas de Calidad**: [cite: 16]
    * [cite_start]**Pruebas de Seguridad**: No requerido. [cite: 20]
    * [cite_start]**Pruebas de Usabilidad**: No requerido. [cite: 20]
    * [cite_start]**Pruebas de Compatibilidad**: Asegurar que el sistema funcione correctamente en diferentes navegadores y dispositivos. [cite: 21]
    * [cite_start]**Pruebas de Regresión**: No requerido. [cite: 22]
    * [cite_start]**Pruebas de Estrés**: No requerido. [cite: 22]
    * [cite_start]**Pruebas de Aceptación**: Implementar pruebas de aceptación basadas en Cucumber para asegurar que el sistema cumple con los requisitos del usuario. [cite: 22]
    * [cite_start]**Pruebas de Navegadores**: Utilizar Playwright para realizar pruebas automatizadas en diferentes navegadores y dispositivos, asegurando la compatibilidad. [cite: 23]

3.  [cite_start]**Documentación**: [cite: 16]
    * [cite_start]**Documentación de Requisitos**: Crear un documento detallado de requisitos funcionales y no funcionales. [cite: 24]
    * [cite_start]**Documentación Técnica**: No requerido. [cite: 24]
    * [cite_start]**Guía de Pruebas**: Documentar los casos de prueba, los resultados y cualquier defecto encontrado. [cite: 25]

4.  [cite_start]**Revisión y Validación**: [cite: 16]
    * [cite_start]**Revisiones de Código**: No requerido. [cite: 26]
    * [cite_start]**Validación de Requisitos**: No requerido. [cite: 26]

5.  [cite_start]**Integración Continua y Despliegue Continuo (CI/CD)**: [cite: 27]
    * [cite_start]**Pipeline de CI/CD**: No requerido. [cite: 27]
    * [cite_start]**Entorno de Pruebas**: No requerido. [cite: 27]
    * [cite_start]**Contenedorización**: Utilizar herramientas como Docker o Podman para crear contenedores del sistema que faciliten la consistencia entre diferentes entornos de desarrollo, prueba y producción. [cite: 28]
    * [cite_start]**Migración de Base de Datos**: Implementar herramientas como Flyway o Liquibase para gestionar las migraciones de bases de datos de manera segura y automatizada. [cite: 29]

6.  [cite_start]**Monitoreo y Mantenimiento**: [cite: 30]
    * [cite_start]**Monitoreo en Producción**: No requerido. [cite: 30]
    * [cite_start]**Observabilidad**: No requerido. [cite: 30]
    * [cite_start]**Mantenimiento y Actualizaciones**: Planificar un ciclo regular de mantenimiento y actualizaciones para asegurar que el sistema permanezca seguro y funcional. [cite: 31]

7.  [cite_start]**Gestión de Calidad**: [cite: 32]
    * [cite_start]**Indicadores de Calidad**: Definir métricas de calidad del software (como cobertura de pruebas, densidad de defectos, tiempo de respuesta) y realizar un seguimiento de estas métricas. [cite: 32]
    * [cite_start]**Mejora Continua**: No requerido. [cite: 33]

8.  [cite_start]**Evaluación Post-Implementación**: [cite: 32]
    * [cite_start]**Revisión Post-Mortem**: No requerido. [cite: 32]

### Tecnologías Sugeridas:

* [cite_start]**Backend**: Spring Boot, Quarkus, Django, Node.js, Next.js [cite: 34]
* [cite_start]**Auditoría**: Hibernate Envers [cite: 34]
* [cite_start]**Frontend**: Hilla, Vaadin Flow, React.js, Vue.js, Angular, JHipster [cite: 34]
* [cite_start]**Base de Datos**: MySQL, PostgreSQL, MariaDB [cite: 34]
* [cite_start]**Auditoría**: Hibernate Envers [cite: 34]
* [cite_start]**Autenticación y Autorización**: OAuth2, JWT. [cite: 34]
* [cite_start]**Pruebas**: JUnit/TestNG, Selenium, Cucumber, JMeter, Playwright, Microcks. [cite: 34]
* [cite_start]**CI/CD**: GitHub Actions, Jenkins, GitLab CI. [cite: 34]
* [cite_start]**Contenedorización**: Docker, Podman. [cite: 34]
* [cite_start]**Migración de Base de Datos**: Flyway, Liquibase. [cite: 35]
* [cite_start]**Monitoreo y Observabilidad**: Prometheus, Grafana, Open Telemetry [cite: 34]

### Conclusión:

[cite_start]Este mandato proporciona una guía integral para el desarrollo de un sistema de gestión de inventarios con un enfoque en el aseguramiento de la calidad del software. [cite: 35] [cite_start]Al seguir estas directrices, se garantizará que el sistema no solo sea funcional y eficiente, sino también seguro, confiable y fácil de usar. [cite: 36] [cite_start]El proyecto debe ser realizado en grupos de máximo dos estudiantes, promoviendo el trabajo en equipo y la colaboración. [cite: 37] [cite_start]La valoración de cada miembro del equipo se tomará en cuenta considerando los commits realizados en GitHub. [cite: 38] [cite_start]Asegúrense de que ambos miembros del equipo contribuyan de manera equitativa al proyecto. [cite: 39]