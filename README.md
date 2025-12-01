# Gestión Docente - Backend

API REST desarrollada con Spring Boot para el sistema de gestión académica. Proporciona endpoints para la gestión de profesores, cursos, estudiantes, evaluaciones, notas y asistencias.

## Sobre el Programa

Este backend proporciona la API REST para el sistema de gestión académica. El programa maneja la autenticación de usuarios mediante JWT, donde cada profesor puede gestionar sus propios cursos y los administradores tienen permisos adicionales.

El sistema organiza la información de manera jerárquica: cada profesor tiene múltiples cursos, cada curso tiene estudiantes y evaluaciones, y cada evaluación tiene calificaciones asociadas a los estudiantes. Las asistencias se registran por fecha y curso.

El backend calcula automáticamente los promedios de los estudiantes considerando los pesos asignados a cada tipo de evaluación. También gestiona las notas de aprobación y promoción, permitiendo valores por defecto a nivel de curso que pueden ser sobrescritos por evaluación.

El sistema incluye funcionalidades de envío de emails para notificar a los estudiantes sobre sus calificaciones o para enviar mensajes personalizados. Además, permite exportar las planillas de notas y asistencias a formato Excel.

La base de datos se crea automáticamente al iniciar la aplicación, y todas las operaciones están protegidas por autenticación JWT. Los endpoints están organizados por recurso (cursos, estudiantes, evaluaciones, etc.) y siguen las convenciones REST.

## Requisitos

- Java JDK 21 o superior
- Maven 3.6+ (opcional, el proyecto incluye Maven Wrapper)
- MySQL 8.0+ o H2 (para desarrollo)

## Configuración

Edita `src/main/resources/application.properties` y configura la base de datos:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/GestionDocenteDB?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=tu_contraseña
server.port=8080
```

La base de datos se crea automáticamente si no existe.

## Ejecución

### Windows
```bash
.\mvnw.cmd spring-boot:run
```

### Linux/Mac
```bash
./mvnw spring-boot:run
```

El servidor estará disponible en http://localhost:8080

## Estructura del Proyecto

```
src/main/java/com/gestion/docente/backend/Gestion/Docente/Backend/
├── config/        # Configuraciones (CORS, Security)
├── controller/   # Controladores REST
├── dto/          # Data Transfer Objects
├── model/        # Entidades JPA
├── repository/   # Repositorios JPA
├── security/     # Configuración JWT
└── service/      # Lógica de negocio
```

## Endpoints Principales

### Autenticación
- `POST /api/auth/register` - Registro de profesor
- `POST /api/auth/login` - Inicio de sesión
- `GET /api/auth/me` - Información del usuario autenticado

### Cursos
- `GET /api/courses` - Listar cursos
- `POST /api/courses` - Crear curso
- `PUT /api/courses/{id}` - Actualizar curso
- `DELETE /api/courses/{id}` - Eliminar curso

### Estudiantes
- `GET /api/students/course/{courseId}` - Estudiantes por curso
- `POST /api/students` - Crear estudiante
- `PUT /api/students/{id}` - Actualizar estudiante
- `DELETE /api/students/{id}` - Eliminar estudiante

### Evaluaciones
- `GET /api/evaluations/course/{courseId}` - Evaluaciones por curso
- `POST /api/evaluations` - Crear evaluación
- `DELETE /api/evaluations/{id}` - Eliminar evaluación

### Notas
- `POST /api/grades` - Crear/actualizar nota
- `GET /api/grades/course/{courseId}` - Notas por curso
- `GET /api/excel/course/{courseId}/grades` - Exportar notas a Excel

### Asistencias
- `POST /api/attendances` - Crear/actualizar asistencia
- `GET /api/attendances/course/{courseId}` - Asistencias por curso
- `GET /api/excel/course/{courseId}/attendances` - Exportar asistencias a Excel

## Autenticación

El sistema usa JWT (JSON Web Tokens). Después de hacer login, incluye el token en el header de las peticiones:

```
Authorization: Bearer {token}
```

## Base de Datos

Las tablas se crean automáticamente al iniciar la aplicación. La configuración de JPA está en `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=update
```

## Tecnologías

- Spring Boot 3.5.7
- Spring Security
- Spring Data JPA
- JWT
- MySQL / H2
- Lombok
- Maven

## Build

```bash
mvn clean package
```

El JAR se genera en `target/Gestion-Docente-Backend-0.0.1-SNAPSHOT.jar`
