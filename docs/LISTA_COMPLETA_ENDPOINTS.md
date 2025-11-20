# 📋 Lista Completa de Endpoints - Gestión Docente Backend

**Fecha de revisión:** $(date)  
**Base URL:** `http://localhost:8080/api`

---

## ✅ ENDPOINTS IMPLEMENTADOS Y FUNCIONALES

### 🔐 Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Estado | Paginación |
|--------|----------|-------------|--------|------------|
| `POST` | `/api/auth/register` | Registra un nuevo profesor | ✅ Implementado | ❌ No aplica |

**Detalles:**
- Valida que el email no esté registrado
- Encripta la contraseña con BCrypt
- Retorna `ProfessorDTO` (sin password)

**Body Request:**
```json
{
  "name": "string",
  "lastname": "string",
  "email": "string",
  "password": "string",
  "cel": "string (opcional)",
  "photoUrl": "string (opcional)"
}
```

---

### 📚 Cursos (`/api/courses`)

| Método | Endpoint | Descripción | Estado | Paginación |
|--------|----------|-------------|--------|------------|
| `GET` | `/api/courses` | Obtiene todos los cursos | ✅ Implementado | ✅ Sí |
| `GET` | `/api/courses/{id}` | Obtiene un curso por ID | ✅ Implementado | ❌ No aplica |
| `POST` | `/api/courses` | Crea un nuevo curso | ✅ Implementado | ❌ No aplica |
| `GET` | `/api/courses/professor/{professorId}` | Obtiene cursos de un profesor | ✅ Implementado | ✅ Sí |

**Parámetros de paginación (opcionales):**
- `?page=0` - Número de página (0-indexed)
- `?size=20` - Tamaño de página (default: 20)
- `?sort=name,asc` - Ordenamiento
- `?paginated=true` - Forzar paginación

**Ejemplos:**
```
GET /api/courses
GET /api/courses?page=0&size=10&sort=name,asc
GET /api/courses/professor/1?paginated=true
```

**Pendientes:**
- `PUT /api/courses/{id}` - Actualizar curso
- `DELETE /api/courses/{id}` - Eliminar curso

---

### 📝 Evaluaciones (`/api/evaluations`)

| Método | Endpoint | Descripción | Estado | Paginación |
|--------|----------|-------------|--------|------------|
| `GET` | `/api/evaluations/course/{courseId}` | Obtiene evaluaciones de un curso | ✅ Implementado | ✅ Sí |
| `POST` | `/api/evaluations` | Crea una nueva evaluación | ✅ Implementado | ❌ No aplica |
| `DELETE` | `/api/evaluations/{id}` | Elimina una evaluación | ✅ Implementado | ❌ No aplica |

**Parámetros de paginación (opcionales):**
- `?page=0&size=20&sort=date,desc` - Ordena por fecha descendente por defecto

**Body Request (POST):**
```json
{
  "nombre": "string",
  "date": "YYYY-MM-DD",
  "tipo": "string (examen, práctica, tarea)",
  "courseId": 1
}
```

---

### 📊 Notas (`/api/grades`)

| Método | Endpoint | Descripción | Estado | Paginación |
|--------|----------|-------------|--------|------------|
| `GET` | `/api/grades/course/{courseId}` | Obtiene notas de un curso | ✅ Implementado | ✅ Sí |
| `GET` | `/api/grades/evaluation/{evaluationId}` | Obtiene notas de una evaluación | ✅ Implementado | ✅ Sí |
| `POST` | `/api/grades` | Crea o actualiza una nota | ✅ Implementado | ❌ No aplica |
| `PUT` | `/api/grades/{id}` | Actualiza una nota por ID | ✅ Implementado | ❌ No aplica |
| `GET` | `/api/grades/student/{studentId}/course/{courseId}/average` | Calcula promedio de estudiante | ✅ Implementado | ❌ No aplica |

**Características especiales:**
- ✅ Validación de rango de notas (0-10)
- ✅ Validación de consistencia: verifica que studentId y evaluationId pertenezcan al mismo courseId
- ✅ Si ya existe nota para estudiante+evaluación, la actualiza automáticamente

**Body Request (POST/PUT):**
```json
{
  "grade": 8.5,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

---

## 🚧 ENDPOINTS PENDIENTES DE IMPLEMENTAR

### 👨‍🎓 Estudiantes (`/api/students`)

| Método | Endpoint | Descripción | Estado |
|--------|----------|-------------|--------|
| `GET` | `/api/students/course/{courseId}` | Obtiene estudiantes de un curso | ⏳ Pendiente |
| `POST` | `/api/students` | Crea un nuevo estudiante | ⏳ Pendiente |
| `PUT` | `/api/students/{id}` | Actualiza un estudiante | ⏳ Pendiente |
| `DELETE` | `/api/students/{id}` | Elimina un estudiante | ⏳ Pendiente |

**Nota:** El controlador existe pero los servicios están comentados.

---

### 👨‍🏫 Profesores (`/api/professors`)

| Método | Endpoint | Descripción | Estado |
|--------|----------|-------------|--------|
| `GET` | `/api/professors/{id}` | Obtiene un profesor por ID | ⏳ Pendiente |
| `PUT` | `/api/professors/{id}` | Actualiza un profesor | ⏳ Pendiente |
| `GET` | `/api/professors/email-exists?email={email}` | Verifica si email existe | ⏳ Pendiente |

**Nota:** El servicio `emailExists()` existe pero no está expuesto como endpoint.

---

### ✅ Asistencias (`/api/attendances`)

| Método | Endpoint | Descripción | Estado |
|--------|----------|-------------|--------|
| `GET` | `/api/attendances/course/{courseId}` | Obtiene asistencias de un curso | ⏳ Pendiente |
| `GET` | `/api/attendances/student/{studentId}` | Obtiene asistencias de un estudiante | ⏳ Pendiente |
| `POST` | `/api/attendances` | Crea una nueva asistencia | ⏳ Pendiente |
| `PUT` | `/api/attendances/{id}` | Actualiza una asistencia | ⏳ Pendiente |
| `GET` | `/api/attendances/student/{studentId}/course/{courseId}/percentage` | Calcula porcentaje de asistencia | ⏳ Pendiente |

**Nota:** El controlador existe pero los servicios están comentados.

---

### 📊 Excel (`/api/excel`)

| Método | Endpoint | Descripción | Estado |
|--------|----------|-------------|--------|
| `GET` | `/api/excel/courses/{courseId}/grades` | Exporta notas a Excel | ⏳ Pendiente |

**Nota:** El controlador existe pero los servicios están comentados.

---

### 🔐 Autenticación - Pendientes

| Método | Endpoint | Descripción | Estado |
|--------|----------|-------------|--------|
| `POST` | `/api/auth/login` | Inicia sesión (JWT) | ⏳ Pendiente |
| `POST` | `/api/auth/logout` | Cierra sesión | ⏳ Pendiente |
| `GET` | `/api/auth/me` | Obtiene profesor actual | ⏳ Pendiente |

**Nota:** Marcados como TODO en el código, pendiente implementación de JWT.

---

## 📊 Resumen Estadístico

### Por Estado:
- ✅ **Implementados:** 13 endpoints
- ⏳ **Pendientes:** 15 endpoints
- **Total:** 28 endpoints

### Por Funcionalidad:
- 🔐 **Autenticación:** 1/4 (25%)
- 📚 **Cursos:** 4/6 (67%)
- 📝 **Evaluaciones:** 3/3 (100%) ✅
- 📊 **Notas:** 5/5 (100%) ✅
- 👨‍🎓 **Estudiantes:** 0/4 (0%)
- 👨‍🏫 **Profesores:** 0/3 (0%)
- ✅ **Asistencias:** 0/5 (0%)
- 📊 **Excel:** 0/1 (0%)

### Por Método HTTP:
- `GET`: 12 endpoints (8 implementados, 4 pendientes)
- `POST`: 8 endpoints (5 implementados, 3 pendientes)
- `PUT`: 5 endpoints (1 implementado, 4 pendientes)
- `DELETE`: 3 endpoints (1 implementado, 2 pendientes)

---

## 🔍 Características Implementadas

### ✅ Validaciones:
- Validación de campos obligatorios con `@Valid`
- Validación de rango de notas (0-10)
- Validación de consistencia de relaciones (studentId, evaluationId, courseId)
- Validación de existencia de entidades relacionadas

### ✅ Paginación:
- Implementada en endpoints de listado
- Compatibilidad hacia atrás (retorna lista completa si no se especifica paginación)
- Parámetros: `page`, `size`, `sort`, `paginated`

### ✅ Manejo de Errores:
- `GlobalExceptionHandler` para validaciones
- Respuestas HTTP apropiadas (400, 404, 500)
- Mensajes de error en español

### ✅ Seguridad:
- Contraseñas encriptadas con BCrypt
- SecurityConfig configurado (actualmente permite todo para desarrollo)

---

## 📝 Notas Importantes

1. **Compatibilidad hacia atrás:** Los endpoints con paginación mantienen compatibilidad retornando listas completas si no se especifica paginación.

2. **Validaciones robustas:** Los endpoints de notas incluyen validación de consistencia que verifica que todas las relaciones sean coherentes.

3. **Pendientes críticos:**
   - Autenticación JWT (login, logout, me)
   - CRUD completo de estudiantes
   - CRUD completo de asistencias
   - Exportación a Excel

4. **Mejoras futuras:**
   - Filtrado por profesor autenticado en `getAllCourses()`
   - Implementación de `updateCourse()` y `deleteCourse()`
   - Implementación de `updateProfessor()`

---

## 🎯 Próximos Pasos Sugeridos

1. **Prioridad Alta:**
   - Implementar CRUD de estudiantes
   - Implementar CRUD de asistencias
   - Implementar login con JWT

2. **Prioridad Media:**
   - Completar CRUD de cursos (PUT, DELETE)
   - Completar CRUD de profesores
   - Exportación a Excel

3. **Prioridad Baja:**
   - Filtrado automático por profesor autenticado
   - Mejoras de paginación (búsqueda, filtros)

