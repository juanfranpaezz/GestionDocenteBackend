# Gestión Docente Backend - API REST con Spring Boot

API REST desarrollada con Spring Boot 3.5.7 que proporciona los endpoints para el sistema de gestión académica. Permite a los profesores gestionar cursos, estudiantes, evaluaciones, notas y asistencias.

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Endpoints](#endpoints)
- [Autenticación JWT](#autenticación-jwt)
- [Base de Datos](#base-de-datos)
- [Documentación Adicional](#documentación-adicional)
- [Contribuidores](#contribuidores)

## 📖 Descripción

Backend REST API para el sistema de gestión académica que permite:

- ✅ **Autenticación y Autorización**: Sistema JWT con roles (PROFESSOR, ADMIN)
- ✅ **Gestión de Profesores**: CRUD completo de profesores
- ✅ **Gestión de Cursos**: CRUD completo de cursos por profesor
- ✅ **Gestión de Estudiantes**: CRUD completo de estudiantes por curso
- ✅ **Sistema de Evaluaciones**: Crear y gestionar evaluaciones
- ✅ **Planilla de Notas**: Sistema completo de calificaciones con promedios
- ✅ **Control de Asistencias**: Registro de asistencias con cálculo de porcentajes
- ✅ **Exportación a Excel**: Exportar planillas de notas y asistencias

## 🚀 Tecnologías Utilizadas

- **Spring Boot 3.5.7**: Framework principal
- **Java 21**: Lenguaje de programación
- **Spring Security**: Seguridad y autenticación
- **JWT (JSON Web Tokens)**: Autenticación stateless
- **Spring Data JPA**: Acceso a datos
- **Hibernate**: ORM para persistencia
- **MySQL**: Base de datos relacional (producción)
- **H2**: Base de datos embebida (desarrollo/testing)
- **Lombok**: Reducción de código boilerplate
- **Maven**: Gestión de dependencias

## 📦 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java JDK 21** o superior
- **Maven 3.6+** (opcional, el proyecto incluye Maven Wrapper)
- **MySQL 8.0+** (para producción) o H2 (incluida, para desarrollo)
- **IDE** (IntelliJ IDEA, VS Code, Eclipse) - recomendado

### Verificar Instalación

```bash
# Verificar Java
java -version
# Debe mostrar: openjdk version "21" o superior

# Verificar Maven (opcional)
mvn -version
```

## 🔧 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/juanfranpaezz/GestionDocenteBackend.git
cd GestionDocenteBackend
```

### 2. Configurar Base de Datos

#### Opción A: MySQL (Recomendado para producción)

1. **Instalar MySQL** si no lo tienes instalado
2. **Crear base de datos** (opcional, se crea automáticamente):
   ```sql
   CREATE DATABASE IF NOT EXISTS GestionDocenteDB;
   ```
3. **Configurar credenciales** en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/GestionDocenteDB?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=tu_contraseña
   ```

#### Opción B: H2 (Para desarrollo rápido)

La configuración H2 está comentada en `application.properties`. Para usarla:

1. Comenta la configuración de MySQL
2. Descomenta la configuración de H2:
   ```properties
   spring.datasource.url=jdbc:h2:mem:gestiondocente
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console
   ```

**Nota**: H2 es en memoria, los datos se pierden al reiniciar el servidor.

## ⚙️ Configuración

### Archivo `application.properties`

Ubicación: `src/main/resources/application.properties`

#### Configuración Principal

```properties
# Puerto del servidor
server.port=8080

# Base de datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/GestionDocenteDB?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=MySuperSecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS512AlgorithmToWorkProperlyAndSecurely
jwt.expiration=3600
```

### Variables de Entorno (Recomendado para producción)

Para mayor seguridad, usa variables de entorno:

```bash
# Windows (PowerShell)
$env:JWT_SECRET="tu_secret_key_muy_largo_y_seguro"
$env:SPRING_DATASOURCE_PASSWORD="tu_password_mysql"

# Linux/Mac
export JWT_SECRET="tu_secret_key_muy_largo_y_seguro"
export SPRING_DATASOURCE_PASSWORD="tu_password_mysql"
```

Y en `application.properties`:
```properties
jwt.secret=${JWT_SECRET:default_secret}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:root1234}
```

## 🏃 Ejecución

### Opción 1: Desde el IDE (Recomendado)

#### IntelliJ IDEA

1. Abre el proyecto en IntelliJ IDEA
2. Espera a que Maven descargue las dependencias
3. Busca el archivo: `src/main/java/com/gestion/docente/backend/Gestion/Docente/Backend/GestionDocenteBackendApplication.java`
4. Haz clic derecho → `Run 'GestionDocenteBackendApplication'`
   - O presiona `Shift + F10`

#### VS Code

1. Abre el proyecto en VS Code
2. Instala la extensión "Extension Pack for Java" si no la tienes
3. Busca el archivo: `GestionDocenteBackendApplication.java`
4. Haz clic en el botón "Run" sobre el método `main()`
   - O presiona `F5`

#### Eclipse

1. Abre el proyecto en Eclipse
2. Busca el archivo: `GestionDocenteBackendApplication.java`
3. Haz clic derecho → `Run As` → `Java Application`
   - O presiona `Ctrl + F11`

### Opción 2: Desde la Terminal

#### Windows

```powershell
# Navegar a la carpeta del proyecto
cd "ruta\a\GestionDocenteBackend"

# Ejecutar con Maven Wrapper
.\mvnw.cmd spring-boot:run

# O si tienes Maven instalado
mvn spring-boot:run
```

#### Linux / Mac

```bash
# Navegar a la carpeta del proyecto
cd /ruta/a/GestionDocenteBackend

# Ejecutar con Maven Wrapper
./mvnw spring-boot:run

# O si tienes Maven instalado
mvn spring-boot:run
```

### Verificar que Está Funcionando

Cuando el servidor arranque correctamente, verás en la consola:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.7)

Started GestionDocenteBackendApplication in X.XXX seconds
```

El servidor estará disponible en: **http://localhost:8080**

### Probar el Backend

Puedes probar que el servidor está funcionando:

```bash
# Con curl
curl http://localhost:8080/api/auth/me

# O abrir en el navegador
http://localhost:8080/api/auth/me
```

## 📁 Estructura del Proyecto

```
GestionDocenteBackend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/gestion/docente/backend/Gestion/Docente/Backend/
│   │   │       ├── config/              # Configuraciones (CORS, Security, etc.)
│   │   │       ├── controller/          # Controladores REST
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       ├── model/               # Entidades JPA
│   │   │       ├── repository/         # Repositorios JPA
│   │   │       ├── security/            # Configuración de seguridad y JWT
│   │   │       ├── service/             # Lógica de negocio
│   │   │       └── GestionDocenteBackendApplication.java
│   │   └── resources/
│   │       └── application.properties    # Configuración
│   └── test/                            # Tests
├── docs/                                # Documentación adicional
├── pom.xml                              # Dependencias Maven
├── mvnw                                 # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                             # Maven Wrapper (Windows)
└── README.md                            # Este archivo
```

## 🔌 Endpoints

### Autenticación

- `POST /api/auth/register` - Registro de profesor
- `POST /api/auth/login` - Inicio de sesión
- `GET /api/auth/me` - Obtener información del usuario autenticado

### Profesores

- `GET /api/professors` - Listar profesores (ADMIN)
- `GET /api/professors/{id}` - Obtener profesor por ID
- `PUT /api/professors/{id}` - Actualizar profesor
- `DELETE /api/professors/{id}` - Eliminar profesor (ADMIN)
- `GET /api/professors/email-exists?email={email}` - Verificar si existe email

### Cursos

- `GET /api/courses` - Listar todos los cursos
- `GET /api/courses/{id}` - Obtener curso por ID
- `POST /api/courses` - Crear curso
- `PUT /api/courses/{id}` - Actualizar curso
- `DELETE /api/courses/{id}` - Eliminar curso
- `GET /api/courses/professor/{professorId}` - Cursos por profesor

### Estudiantes

- `GET /api/students` - Listar estudiantes
- `GET /api/students/{id}` - Obtener estudiante por ID
- `POST /api/students` - Crear estudiante
- `PUT /api/students/{id}` - Actualizar estudiante
- `DELETE /api/students/{id}` - Eliminar estudiante
- `GET /api/students/course/{courseId}` - Estudiantes por curso

### Evaluaciones

- `GET /api/evaluations` - Listar evaluaciones
- `GET /api/evaluations/{id}` - Obtener evaluación por ID
- `POST /api/evaluations` - Crear evaluación
- `DELETE /api/evaluations/{id}` - Eliminar evaluación
- `GET /api/evaluations/course/{courseId}` - Evaluaciones por curso

### Notas

- `GET /api/grades` - Listar notas
- `GET /api/grades/{id}` - Obtener nota por ID
- `POST /api/grades` - Crear/actualizar nota
- `GET /api/grades/course/{courseId}` - Notas por curso
- `GET /api/grades/evaluation/{evaluationId}` - Notas por evaluación
- `GET /api/grades/student/{studentId}/average` - Promedio de estudiante

### Asistencias

- `GET /api/attendances` - Listar asistencias
- `GET /api/attendances/{id}` - Obtener asistencia por ID
- `POST /api/attendances` - Crear/actualizar asistencia
- `GET /api/attendances/course/{courseId}` - Asistencias por curso
- `GET /api/attendances/student/{studentId}` - Asistencias por estudiante

### Exportación

- `GET /api/excel/course/{courseId}/grades` - Exportar notas a Excel
- `GET /api/excel/course/{courseId}/attendances` - Exportar asistencias a Excel

**Nota**: Para ver la documentación completa de endpoints con ejemplos, consulta `docs/ENDPOINTS_POSTMAN.md`

## 🔒 Autenticación JWT

### Flujo de Autenticación

1. **Registro/Login**: El usuario se registra o inicia sesión
2. **Token JWT**: El servidor retorna un token JWT
3. **Peticiones Autenticadas**: El cliente envía el token en el header:
   ```
   Authorization: Bearer {token}
   ```
4. **Validación**: El servidor valida el token en cada petición

### Ejemplo de Uso

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"profesor@example.com","password":"password123"}'

# Respuesta:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "professor": { ... }
# }

# 2. Usar el token en peticiones
curl -X GET http://localhost:8080/api/courses \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 💾 Base de Datos

### MySQL (Producción)

- **Base de datos**: `GestionDocenteDB`
- **Puerto**: `3306`
- **Usuario**: Configurado en `application.properties`
- **Contraseña**: Configurada en `application.properties`

Las tablas se crean automáticamente al iniciar la aplicación gracias a `spring.jpa.hibernate.ddl-auto=update`.

### H2 (Desarrollo)

- **URL de consola**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:gestiondocente`
- **Usuario**: `sa`
- **Contraseña**: (vacío)

**Nota**: Los datos en H2 se pierden al reiniciar el servidor.

### Tablas Creadas Automáticamente

- `professors` - Profesores
- `courses` - Cursos
- `students` - Estudiantes
- `evaluations` - Evaluaciones
- `grades` - Notas
- `attendances` - Asistencias
- `classes` - Clases (asistencias por fecha)

## 📚 Documentación Adicional

Toda la documentación detallada se encuentra en la carpeta `docs/`:

- **`GUIA_COMPLETA_PASO_A_PASO.md`** - ⭐ Guía completa de inicio
- **`COMO_EJECUTAR_EL_PROGRAMA.md`** - Instrucciones de ejecución
- **`ENDPOINTS_POSTMAN.md`** - Endpoints con ejemplos para Postman
- **`EXPLICACION_ARQUITECTURA_BACKEND.md`** - Arquitectura del sistema
- **`EXPLICACION_BASE_DATOS_H2.md`** - Explicación de H2
- **`SOLUCION_ERROR_LOMBOK.md`** - Solución a problemas con Lombok

## ⚠️ Solución de Problemas

### Error: "Puerto 8080 ya está en uso"

**Solución**: Cambia el puerto en `application.properties`:
```properties
server.port=8081
```

### Error: "No se puede conectar a MySQL"

**Solución**:
1. Verifica que MySQL esté corriendo
2. Verifica las credenciales en `application.properties`
3. Verifica que la base de datos exista o que `createDatabaseIfNotExist=true`

### Error: "Java no se reconoce como comando"

**Solución**: 
1. Instala Java JDK 21
2. Configura la variable de entorno `JAVA_HOME`
3. Agrega `%JAVA_HOME%\bin` al PATH

### Error: "Lombok no funciona en el IDE"

**Solución**: Consulta `docs/SOLUCION_ERROR_LOMBOK.md`

### Error: "Maven no descarga dependencias"

**Solución**:
```bash
# Limpiar y descargar dependencias
mvn clean install
```

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

## 📦 Build

```bash
# Compilar proyecto
mvn clean compile

# Crear JAR ejecutable
mvn clean package

# El JAR estará en: target/Gestion-Docente-Backend-0.0.1-SNAPSHOT.jar

# Ejecutar JAR
java -jar target/Gestion-Docente-Backend-0.0.1-SNAPSHOT.jar
```

## 👥 Contribuidores

- [Emmanuel Di Benedetto](https://github.com/emmanueldibenedetto)
- [Juan Francisco Paez](https://github.com/juanfranpaezz)

## 📄 Licencia

Este proyecto fue desarrollado como Trabajo Práctico Final para la materia Programación IV - UTN Mar del Plata.

## 🔗 Enlaces Relacionados

- **Frontend Repository**: https://github.com/emmanueldibenedetto/GestionDocente
- **Backend Repository**: https://github.com/juanfranpaezz/GestionDocenteBackend

---

**Desarrollado con ❤️ usando Spring Boot 3.5.7**

