# ===================================================================
# DOCKERFILE - SISTEMA DE GESTIÓN DE INVENTARIOS
# Contenedorización con Docker para QAS
# ===================================================================

# Etapa 1: Build de la aplicación
FROM gradle:8.5-jdk21-alpine AS build

# Información del mantenedor
LABEL maintainer="Sistema de Gestión de Inventarios"
LABEL version="1.0"
LABEL description="Sistema de gestión de inventarios con QAS completo"

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copiar código fuente
COPY src ./src

# Dar permisos de ejecución al wrapper de Gradle
RUN chmod +x ./gradlew

# Compilar la aplicación y generar el JAR
RUN ./gradlew clean build -x test --no-daemon

# Etapa 2: Runtime de la aplicación
FROM openjdk:21-jdk-slim

# Instalar herramientas adicionales para debugging (opcional)
RUN apt-get update && \
    apt-get install -y curl netcat-traditional && \
    rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Cambiar propietario del archivo
RUN chown appuser:appuser app.jar

# Cambiar a usuario no-root
USER appuser

# Exponer puerto de la aplicación
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Metadatos adicionales
LABEL org.opencontainers.image.title="Sistema de Gestión de Inventarios"
LABEL org.opencontainers.image.description="Sistema de gestión de inventarios con enfoque QAS"
LABEL org.opencontainers.image.version="1.0"
LABEL org.opencontainers.image.source="https://github.com/tu-usuario/proyecto-final" 