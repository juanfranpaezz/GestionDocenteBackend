# Explicación Detallada de la Arquitectura del Backend - Gestión Docente

## 📋 Índice
1. [Visión General](#visión-general)
2. [Arquitectura MVC](#arquitectura-mvc)
3. [Capas del Sistema](#capas-del-sistema)
4. [Flujo de Datos](#flujo-de-datos)
5. [Relaciones entre Entidades](#relaciones-entre-entidades)
6. [Análisis en Lenguaje Coloquial](#análisis-en-lenguaje-coloquial)

---

## 🎯 Visión General

El backend de **Gestión Docente** está construido siguiendo el patrón **MVC (Modelo-Vista-Controlador)** adaptado para una API REST. La aplicación permite a los profesores gestionar sus cursos, estudiantes, evaluaciones, notas y asistencias.

### Componentes Principales:
- **Models (Entidades)**: Representan las tablas de la base de datos
- **DTOs**: Objetos para transferir datos entre frontend y backend
- **Repositories**: Acceso a la base de datos
- **Services**: Lógica de negocio
- **Controllers**: Endpoints REST que reciben peticiones HTTP

---

## 🏗️ Arquitectura MVC

### 1. **MODEL (Modelo) - Las Entidades**

Las entidades son las clases que representan las tablas de la base de datos. Cada entidad tiene:
- **Atributos**: Campos que se guardan en la base de datos
- **Relaciones**: Conexiones con otras entidades usando JPA/Hibernate
- **Anotaciones JPA**: `@Entity`, `@Table`, `@Id`, `@Column`, etc.

#### Entidades del Sistema:

**Professor (Profesor)**
- Representa a un docente del sistema
- Tiene información personal: nombre, apellido, email, contraseña, celular, foto
- Un profesor puede tener muchos cursos (relación 1 a muchos)

**Course (Curso)**
- Representa un curso que dicta un profesor
- Tiene: nombre, escuela, descripción
- Pertenece a un profesor (relación muchos a 1)
- Contiene estudiantes, evaluaciones, notas y asistencias (relación 1 a muchos)

**Student (Estudiante)**
- Representa a un alumno
- Tiene: nombre, apellido, celular, email, documento
- Pertenece a un curso (relación muchos a 1)
- Tiene muchas notas y asistencias (relación 1 a muchos)

**Evaluation (Evaluación)**
- Representa una evaluación/examen de un curso
- Tiene: nombre, fecha
- Pertenece a un curso (relación muchos a 1)
- Tiene muchas notas asociadas (relación 1 a muchos)

**Grade (Nota)**
- Representa la calificación de un estudiante en una evaluación
- Tiene: valor de la nota (puede ser null si no fue evaluado)
- Relaciona: Estudiante + Evaluación + Curso
- Pertenece a un curso, un estudiante y una evaluación (relación muchos a 1 con cada uno)

**Attendance (Asistencia)**
- Representa el registro de asistencia de un estudiante
- Tiene: fecha, presente/ausente (boolean)
- Pertenece a un curso y un estudiante (relación muchos a 1 con cada uno)

---

### 2. **DTO (Data Transfer Object) - Objetos de Transferencia**

Los DTOs son objetos simples que se usan para:
- **Recibir datos** del frontend (en las peticiones HTTP)
- **Enviar datos** al frontend (en las respuestas HTTP)
- **No exponer** la estructura interna de las entidades
- **No incluir** información sensible (como contraseñas)

#### Tipos de DTOs:

**DTOs de Entidades:**
- `ProfessorDTO`: Datos del profesor (sin contraseña)
- `CourseDTO`: Datos del curso
- `StudentDTO`: Datos del estudiante
- `EvaluationDTO`: Datos de la evaluación
- `GradeDTO`: Datos de la nota
- `AttendanceDTO`: Datos de la asistencia

**DTOs de Request (Petición):**
- `LoginRequest`: Email y contraseña para iniciar sesión
- `RegisterRequest`: Datos para registrar un nuevo profesor

---

### 3. **REPOSITORY (Repositorio) - Acceso a Datos**

Los repositorios son **interfaces** que extienden `JpaRepository` de Spring Data JPA. Se encargan de:
- **Guardar** entidades en la base de datos
- **Buscar** entidades por diferentes criterios
- **Eliminar** entidades
- **Verificar** existencia de datos

#### Características:
- Spring Data JPA **implementa automáticamente** los métodos básicos (save, findById, delete, etc.)
- Puedes agregar **métodos personalizados** usando convenciones de nombres:
  - `findByEmail(String email)` → busca por email
  - `findByCourseId(Long courseId)` → busca por ID de curso
  - `existsByEmail(String email)` → verifica si existe un email

#### Repositorios del Sistema:
- `ProfessorRepository`: Busca profesores por email, verifica existencia
- `CourseRepository`: Busca cursos por profesor
- `StudentRepository`: Busca estudiantes por curso
- `EvaluationRepository`: Busca evaluaciones por curso
- `GradeRepository`: Busca notas por curso o por estudiante y curso
- `AttendanceRepository`: Busca asistencias por curso, estudiante o ambos

---

### 4. **SERVICE (Servicio) - Lógica de Negocio**

Los servicios son **interfaces** que definen la lógica de negocio. Se encargan de:
- **Convertir** entidades a DTOs y viceversa
- **Validar** datos antes de guardarlos
- **Aplicar reglas de negocio** (ej: no puede haber dos profesores con el mismo email)
- **Calcular** valores (promedios, porcentajes)
- **Coordinar** operaciones entre múltiples repositorios

#### Servicios del Sistema:

**ProfessorService:**
- `register()`: Registra un nuevo profesor (encripta contraseña, envía email)
- `login()`: Autentica un profesor y retorna un token JWT
- `getCurrentProfessor()`: Obtiene el profesor autenticado
- `updateProfessor()`: Actualiza datos del profesor
- `emailExists()`: Verifica si un email ya está registrado

**CourseService:**
- `getCoursesByProfessor()`: Obtiene todos los cursos de un profesor
- `getCourseById()`: Obtiene un curso por ID
- `createCourse()`: Crea un nuevo curso
- `updateCourse()`: Actualiza un curso
- `deleteCourse()`: Elimina un curso (y todos sus datos relacionados en cascada)

**StudentService:**
- `getStudentsByCourse()`: Obtiene todos los estudiantes de un curso
- `addStudentToCourse()`: Agrega un estudiante a un curso
- `updateStudent()`: Actualiza datos de un estudiante
- `removeStudent()`: Elimina un estudiante (y sus notas/asistencias)

**EvaluationService:**
- `getEvaluationsByCourse()`: Obtiene todas las evaluaciones de un curso
- `addEvaluation()`: Crea una nueva evaluación
- `deleteEvaluation()`: Elimina una evaluación (y sus notas)

**GradeService:**
- `getGradesByCourse()`: Obtiene todas las notas de un curso
- `setGrade()`: Asigna o actualiza una nota
- `calculateAverage()`: Calcula el promedio de notas de un estudiante en un curso

**AttendanceService:**
- `getAttendancesByCourse()`: Obtiene todas las asistencias de un curso
- `getAttendancesByStudent()`: Obtiene todas las asistencias de un estudiante
- `markAttendance()`: Registra una asistencia
- `updateAttendance()`: Actualiza una asistencia
- `calculateAttendancePercentage()`: Calcula el porcentaje de asistencia

**EmailService:**
- `sendRegistrationEmail()`: Envía email de bienvenida al registrarse
- `sendGradesEmail()`: Envía email con las notas a un estudiante

**ExcelService:**
- `generateGradesExcel()`: Genera un archivo Excel con la planilla de notas

---

### 5. **CONTROLLER (Controlador) - Endpoints REST**

Los controladores son clases que:
- **Reciben** peticiones HTTP del frontend
- **Validan** que los datos estén correctos
- **Llaman** a los servicios correspondientes
- **Retornan** respuestas HTTP (JSON, códigos de estado)

#### Características:
- Usan anotaciones como `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.
- Cada endpoint tiene una URL específica (ej: `/api/courses`)
- Retornan códigos HTTP apropiados (200 OK, 201 Created, 404 Not Found, etc.)

#### Controladores del Sistema:

**AuthController** (`/api/auth`):
- `POST /register`: Registra un nuevo profesor
- `POST /login`: Inicia sesión
- `POST /logout`: Cierra sesión
- `GET /me`: Obtiene el profesor autenticado

**ProfessorController** (`/api/professors`):
- `GET /{id}`: Obtiene un profesor por ID
- `PUT /{id}`: Actualiza un profesor
- `GET /email-exists`: Verifica si un email existe

**CourseController** (`/api/courses`):
- `GET /`: Obtiene todos los cursos del profesor autenticado
- `GET /{id}`: Obtiene un curso por ID
- `POST /`: Crea un nuevo curso
- `PUT /{id}`: Actualiza un curso
- `DELETE /{id}`: Elimina un curso
- `GET /professor/{professorId}`: Obtiene cursos de un profesor

**StudentController** (`/api/students`):
- `GET /course/{courseId}`: Obtiene estudiantes de un curso
- `POST /`: Agrega un estudiante
- `PUT /{id}`: Actualiza un estudiante
- `DELETE /{id}`: Elimina un estudiante

**EvaluationController** (`/api/evaluations`):
- `GET /course/{courseId}`: Obtiene evaluaciones de un curso
- `POST /`: Crea una evaluación
- `DELETE /{id}`: Elimina una evaluación

**GradeController** (`/api/grades`):
- `GET /course/{courseId}`: Obtiene notas de un curso
- `POST /`: Asigna una nota
- `PUT /{id}`: Actualiza una nota
- `GET /student/{studentId}/course/{courseId}/average`: Calcula promedio

**AttendanceController** (`/api/attendances`):
- `GET /course/{courseId}`: Obtiene asistencias de un curso
- `GET /student/{studentId}`: Obtiene asistencias de un estudiante
- `POST /`: Registra una asistencia
- `PUT /{id}`: Actualiza una asistencia
- `GET /student/{studentId}/course/{courseId}/percentage`: Calcula porcentaje

**ExcelController** (`/api/excel`):
- `GET /courses/{courseId}/grades`: Descarga Excel con notas

---

## 🔄 Flujo de Datos

### Ejemplo: Crear un Curso

1. **Frontend** envía petición HTTP:
   ```
   POST /api/courses
   Body: {
     "name": "4toC",
     "school": "EES69",
     "description": "Curso de prueba",
     "professorId": 1
   }
   ```

2. **CourseController** recibe la petición:
   - Valida que el body esté correcto
   - Llama a `courseService.createCourse(courseDTO)`

3. **CourseService** procesa:
   - Convierte `CourseDTO` a entidad `Course`
   - Valida que el profesor exista
   - Llama a `courseRepository.save(course)`

4. **CourseRepository** guarda:
   - JPA/Hibernate convierte la entidad a SQL
   - Ejecuta `INSERT INTO courses (...) VALUES (...)`
   - Retorna la entidad guardada con el ID generado

5. **CourseService** convierte:
   - Toma la entidad guardada
   - La convierte a `CourseDTO`
   - Retorna el DTO

6. **CourseController** responde:
   - Retorna el `CourseDTO` como JSON
   - Código HTTP: 201 Created

7. **Frontend** recibe la respuesta:
   ```json
   {
     "id": 1,
     "name": "4toC",
     "school": "EES69",
     "description": "Curso de prueba",
     "professorId": 1
   }
   ```

---

## 🔗 Relaciones entre Entidades

### Relaciones 1 a Muchos (One-to-Many):

1. **Professor → Course** (1 a muchos)
   - Un profesor tiene muchos cursos
   - Un curso pertenece a un profesor
   - En la base de datos: tabla `courses` tiene columna `professorId`

2. **Course → Student** (1 a muchos)
   - Un curso tiene muchos estudiantes
   - Un estudiante pertenece a un curso
   - En la base de datos: tabla `students` tiene columna `courseId`

3. **Course → Evaluation** (1 a muchos)
   - Un curso tiene muchas evaluaciones
   - Una evaluación pertenece a un curso
   - En la base de datos: tabla `evaluations` tiene columna `courseId`

4. **Course → Grade** (1 a muchos)
   - Un curso tiene muchas notas
   - Una nota pertenece a un curso
   - En la base de datos: tabla `grades` tiene columna `courseId`

5. **Course → Attendance** (1 a muchos)
   - Un curso tiene muchas asistencias
   - Una asistencia pertenece a un curso
   - En la base de datos: tabla `attendances` tiene columna `courseId`

6. **Student → Grade** (1 a muchos)
   - Un estudiante tiene muchas notas
   - Una nota pertenece a un estudiante
   - En la base de datos: tabla `grades` tiene columna `studentId`

7. **Student → Attendance** (1 a muchos)
   - Un estudiante tiene muchas asistencias
   - Una asistencia pertenece a un estudiante
   - En la base de datos: tabla `attendances` tiene columna `studentId`

8. **Evaluation → Grade** (1 a muchos)
   - Una evaluación tiene muchas notas (una por cada estudiante)
   - Una nota pertenece a una evaluación
   - En la base de datos: tabla `grades` tiene columna `evaluationId`

### Eliminación en Cascada:

Cuando se elimina una entidad, se eliminan automáticamente sus relaciones:

- **Eliminar un Course** → Elimina todos sus Students, Evaluations, Grades y Attendances
- **Eliminar un Student** → Elimina todas sus Grades y Attendances
- **Eliminar una Evaluation** → Elimina todas sus Grades

Esto se configura con `cascade = CascadeType.ALL` y `orphanRemoval = true` en las anotaciones `@OneToMany`.

---

## 💬 Análisis en Lenguaje Coloquial

### ¿Cómo funciona todo esto en criollo?

Imaginate que el backend es como un **restaurante**:

#### 🏢 **Las Entidades (Models) = Los Ingredientes**
Son la "materia prima" que tenés en la cocina. Cada entidad es como un tipo de ingrediente:
- **Professor** = El dueño del restaurante
- **Course** = Un plato del menú
- **Student** = Un cliente que pide ese plato
- **Evaluation** = Una receta específica
- **Grade** = La calificación que le da el cliente al plato
- **Attendance** = Si el cliente vino o no al restaurante

#### 📦 **Los DTOs = Los Platos Servidos**
Los DTOs son como los platos que le servís al cliente. No le mostrás toda la cocina (las entidades completas), solo le mostrás el plato terminado (el DTO). Por ejemplo, no le mostrás la contraseña del profesor (eso queda en la cocina).

#### 🗄️ **Los Repositorios = La Despensa**
Los repositorios son como la despensa donde guardás los ingredientes. Cuando necesitás algo, vas a la despensa y lo buscás:
- "Dame el profesor con email X"
- "Dame todos los cursos del profesor Y"
- "¿Existe un estudiante con documento Z?"

Spring Data JPA es como tener un ayudante que ya sabe dónde está todo y te lo trae automáticamente.

#### 👨‍🍳 **Los Servicios = Los Cocineros**
Los servicios son los cocineros que preparan los platos. Ellos:
- Toman los ingredientes (entidades) de la despensa (repositorios)
- Los preparan según las recetas (lógica de negocio)
- Los convierten en platos servidos (DTOs)
- Se aseguran de que todo esté bien (validaciones)

Por ejemplo, cuando querés crear un curso:
1. El cocinero (CourseService) toma los datos del plato (CourseDTO)
2. Va a la despensa (CourseRepository) y busca si el profesor existe
3. Prepara el plato (crea la entidad Course)
4. Lo guarda en la despensa
5. Lo convierte en plato servido (CourseDTO) y lo entrega

#### 🍽️ **Los Controladores = Los Meseros**
Los controladores son los meseros que atienden a los clientes (el frontend):
- Reciben el pedido del cliente (petición HTTP)
- Se lo pasan al cocinero (servicio)
- Esperan a que el cocinero termine
- Le llevan el plato al cliente (respuesta HTTP)

Por ejemplo:
- Cliente: "Quiero crear un curso"
- Mesero (CourseController): "Dale, se lo paso al cocinero"
- Cocinero (CourseService): "Listo, acá está el curso creado"
- Mesero: "Acá tenés tu curso" (retorna el CourseDTO)

### 🔄 **El Flujo Completo en Criollo:**

**Escenario: Un profesor quiere crear un curso**

1. **El frontend (cliente)** le dice al mesero (CourseController): "Che, quiero crear un curso con estos datos"

2. **El mesero (CourseController)** le dice al cocinero (CourseService): "Ey, preparame un curso con estos datos"

3. **El cocinero (CourseService)**:
   - Toma los datos (CourseDTO)
   - Va a la despensa (CourseRepository) y verifica que el profesor exista
   - Prepara el curso (crea la entidad Course)
   - Lo guarda en la despensa (courseRepository.save())
   - Lo convierte en plato servido (CourseDTO)
   - Se lo da al mesero

4. **El mesero (CourseController)** le lleva el plato al cliente (retorna el CourseDTO como JSON)

5. **El frontend (cliente)** recibe el curso creado y lo muestra en pantalla

### 🎯 **¿Por qué está todo separado así?**

**Separación de Responsabilidades:**
- Cada "persona" tiene su trabajo específico
- El mesero no cocina, solo atiende
- El cocinero no atiende, solo cocina
- La despensa no cocina, solo guarda

Esto hace que:
- Si querés cambiar cómo se cocina, solo cambiás al cocinero (servicio)
- Si querés cambiar cómo se guarda, solo cambiás la despensa (repositorio)
- Si querés cambiar cómo se atiende, solo cambiás al mesero (controlador)

**Facilita el mantenimiento y las pruebas.**

### 🔗 **Las Relaciones en Criollo:**

**Professor → Course (1 a muchos):**
- Un profesor puede tener muchos cursos
- Como un chef que puede tener muchos platos en su menú
- Cada plato (curso) pertenece a un solo chef (profesor)

**Course → Student (1 a muchos):**
- Un curso tiene muchos estudiantes
- Como un plato que puede ser pedido por muchos clientes
- Cada cliente (estudiante) puede estar en un plato (curso)

**Grade (Nota):**
- Es como la calificación que le da un cliente a un plato específico
- Relaciona: Cliente (Student) + Plato (Course) + Receta (Evaluation) = Calificación (Grade)
- "El cliente Juan le dio 8 puntos al plato 4toC en la evaluación Parcial 1"

**Attendance (Asistencia):**
- Es como el registro de si el cliente vino o no al restaurante
- Relaciona: Cliente (Student) + Plato (Course) + Fecha = Presente/Ausente
- "El cliente Juan del plato 4toC estuvo presente el 15/03/2024"

### 🗑️ **Eliminación en Cascada en Criollo:**

Si eliminás un curso, es como si eliminás un plato del menú:
- Automáticamente se eliminan todos los clientes que pedían ese plato
- Se eliminan todas las recetas de ese plato
- Se eliminan todas las calificaciones de ese plato
- Se eliminan todos los registros de asistencia de ese plato

Esto evita que queden datos "huérfanos" (datos que no tienen sentido sin su padre).

---

## ✅ Resumen Final

El backend está organizado en **4 capas principales**:

1. **Models (Entidades)**: Los datos que se guardan en la base de datos
2. **Repositories**: Acceso a la base de datos
3. **Services**: Lógica de negocio y conversión entre entidades y DTOs
4. **Controllers**: Endpoints REST que reciben peticiones HTTP

**Flujo típico:**
```
Frontend → Controller → Service → Repository → Base de Datos
         ←            ←          ←            ←
```

Cada capa tiene una responsabilidad específica, lo que hace el código más mantenible, testeable y escalable.

