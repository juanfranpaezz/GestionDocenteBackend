# 📡 Lista Completa de Endpoints - Gestión Docente Backend

**Base URL:** `http://localhost:8080/api`

> ⚠️ **IMPORTANTE:** La mayoría de estos endpoints están **SOLO PLANIFICADOS** (comentarios en el código).  
> Solo los endpoints de `/api/evaluations/*` están **REALMENTE IMPLEMENTADOS Y FUNCIONALES**.

---

## 🔐 1. Autenticación (`/api/auth`)

### ❌ Estado: **NO IMPLEMENTADO** (solo comentarios en el código)

---

#### **POST** `/api/auth/register`
Registra un nuevo profesor en el sistema.

**Request Body:**
```json
{
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "password": "password123",
  "cel": "2236805313",
  "photoUrl": "https://example.com/photo.jpg"
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "cel": "2236805313",
  "photoUrl": "https://example.com/photo.jpg"
}
```

---

#### **POST** `/api/auth/login`
Inicia sesión y retorna un token JWT.

**Request Body:**
```json
{
  "email": "juan.perez@example.com",
  "password": "password123"
}
```

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "professor": {
    "id": 1,
    "name": "Juan",
    "lastname": "Pérez",
    "email": "juan.perez@example.com",
    "cel": "2236805313",
    "photoUrl": "https://example.com/photo.jpg"
  }
}
```

---

#### **POST** `/api/auth/logout`
Cierra sesión (invalida el token).

**Response 200 OK:**
```json
{
  "message": "Sesión cerrada exitosamente"
}
```

---

#### **GET** `/api/auth/me`
Obtiene la información del profesor autenticado.

**Response 200 OK:**
```json
{
  "id": 1,
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "cel": "2236805313",
  "photoUrl": "https://example.com/photo.jpg"
}
```

---

## 👨‍🏫 2. Profesores (`/api/professors`)

### ❌ Estado: **NO IMPLEMENTADO** (solo comentarios en el código)

---

#### **GET** `/api/professors/{id}`
Obtiene un profesor por ID.

**Response 200 OK:**
```json
{
  "id": 1,
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "cel": "2236805313",
  "photoUrl": "https://example.com/photo.jpg"
}
```

---

#### **PUT** `/api/professors/{id}`
Actualiza la información de un profesor.

**Request Body:**
```json
{
  "name": "Juan Carlos",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "cel": "2236805314",
  "photoUrl": "https://example.com/new-photo.jpg"
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "name": "Juan Carlos",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "cel": "2236805314",
  "photoUrl": "https://example.com/new-photo.jpg"
}
```

---

#### **GET** `/api/professors/email-exists?email={email}`
Verifica si un email ya está registrado.

**Response 200 OK:**
```json
{
  "exists": true
}
```

---

## 📚 3. Cursos (`/api/courses`)

### ❌ Estado: **NO IMPLEMENTADO** (solo comentarios en el código)

---

#### **GET** `/api/courses`
Obtiene todos los cursos del profesor autenticado.

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "name": "4toC",
    "school": "EES69",
    "description": "Curso de prueba",
    "professorId": 1
  },
  {
    "id": 2,
    "name": "5toA",
    "school": "EES69",
    "description": "Curso avanzado",
    "professorId": 1
  }
]
```

---

#### **GET** `/api/courses/{id}`
Obtiene un curso por ID.

**Response 200 OK:**
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

#### **POST** `/api/courses`
Crea un nuevo curso.

**Request Body:**
```json
{
  "name": "4toC",
  "school": "EES69",
  "description": "Curso de prueba",
  "professorId": 1
}
```

**Response 201 Created:**
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

#### **PUT** `/api/courses/{id}`
Actualiza un curso existente.

**Request Body:**
```json
{
  "name": "4toC Actualizado",
  "school": "EES69",
  "description": "Descripción actualizada",
  "professorId": 1
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "name": "4toC Actualizado",
  "school": "EES69",
  "description": "Descripción actualizada",
  "professorId": 1
}
```

---

#### **DELETE** `/api/courses/{id}`
Elimina un curso (y todos sus datos relacionados).

**Response 204 No Content** (sin body)

---

#### **GET** `/api/courses/professor/{professorId}`
Obtiene todos los cursos de un profesor específico.

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "name": "4toC",
    "school": "EES69",
    "description": "Curso de prueba",
    "professorId": 1
  }
]
```

---

## 👨‍🎓 4. Estudiantes (`/api/students`)

### ❌ Estado: **NO IMPLEMENTADO** (solo comentarios en el código)

---

#### **GET** `/api/students/course/{courseId}`
Obtiene todos los estudiantes de un curso.

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "firstName": "Emmanuel",
    "lastName": "Di Benedetto",
    "cel": "2236805313",
    "email": "emmadibe@abc.gob.ar",
    "document": "39966485",
    "courseId": 1
  },
  {
    "id": 2,
    "firstName": "María",
    "lastName": "González",
    "cel": "2236805314",
    "email": "maria.gonzalez@abc.gob.ar",
    "document": "40123456",
    "courseId": 1
  }
]
```

---

#### **POST** `/api/students`
Agrega un estudiante a un curso.

**Request Body:**
```json
{
  "firstName": "Emmanuel",
  "lastName": "Di Benedetto",
  "cel": "2236805313",
  "email": "emmadibe@abc.gob.ar",
  "document": "39966485",
  "courseId": 1
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "firstName": "Emmanuel",
  "lastName": "Di Benedetto",
  "cel": "2236805313",
  "email": "emmadibe@abc.gob.ar",
  "document": "39966485",
  "courseId": 1
}
```

---

#### **PUT** `/api/students/{id}`
Actualiza la información de un estudiante.

**Request Body:**
```json
{
  "firstName": "Emmanuel",
  "lastName": "Di Benedetto",
  "cel": "2236805315",
  "email": "emmadibe.nuevo@abc.gob.ar",
  "document": "39966485",
  "courseId": 1
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "firstName": "Emmanuel",
  "lastName": "Di Benedetto",
  "cel": "2236805315",
  "email": "emmadibe.nuevo@abc.gob.ar",
  "document": "39966485",
  "courseId": 1
}
```

---

#### **DELETE** `/api/students/{id}`
Elimina un estudiante (y todas sus notas y asistencias relacionadas).

**Response 204 No Content** (sin body)

---

## 📝 5. Evaluaciones (`/api/evaluations`)

### ✅ Estado: **✅ IMPLEMENTADO Y FUNCIONAL** (ÚNICO MÓDULO COMPLETO)

---

#### **GET** `/api/evaluations/course/{courseId}`
Obtiene todas las evaluaciones de un curso.

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "nombre": "Parcial 1",
    "date": "2024-03-15",
    "tipo": "examen",
    "courseId": 1
  },
  {
    "id": 2,
    "nombre": "TP 1",
    "date": "2024-03-20",
    "tipo": "práctica",
    "courseId": 1
  }
]
```

---

#### **POST** `/api/evaluations`
Crea una nueva evaluación.

**Request Body:**
```json
{
  "nombre": "Parcial 1",
  "date": "2024-03-15",
  "tipo": "examen",
  "courseId": 1
}
```

**Campos obligatorios:**
- `nombre` (String, no puede estar vacío)
- `date` (String formato ISO: "YYYY-MM-DD", no puede ser null)
- `tipo` (String, no puede estar vacío - ej: "examen", "práctica", "tarea")
- `courseId` (Long, no puede ser null)

**Response 201 Created:**
```json
{
  "id": 1,
  "nombre": "Parcial 1",
  "date": "2024-03-15",
  "tipo": "examen",
  "courseId": 1
}
```

**Response 400 Bad Request (validación fallida):**
```json
{
  "error": "Error de validación",
  "campos": {
    "nombre": "El nombre de la evaluación es obligatorio",
    "tipo": "El tipo de evaluación es obligatorio"
  },
  "mensaje": "Por favor, complete todos los campos obligatorios"
}
```

**Response 400 Bad Request (curso no existe):**
```json
{
  "error": "El curso con ID 999 no existe"
}
```

---

#### **DELETE** `/api/evaluations/{id}`
Elimina una evaluación (y todas las notas relacionadas).

**Response 204 No Content** (sin body)

**Response 404 Not Found:**
```json
{
  "error": "La evaluación con ID 999 no existe"
}
```

---

## 📊 6. Notas (`/api/grades`)

### ❌ Estado: **NO IMPLEMENTADO** (solo comentarios en el código)

---

#### **GET** `/api/grades/course/{courseId}`
Obtiene todas las notas de un curso.

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "grade": 10.0,
    "courseId": 1,
    "studentId": 1,
    "evaluationId": 1
  },
  {
    "id": 2,
    "grade": 8.5,
    "courseId": 1,
    "studentId": 2,
    "evaluationId": 1
  }
]
```

---

#### **POST** `/api/grades`
Crea o actualiza una nota. Si ya existe una nota para ese estudiante y evaluación, la actualiza.

**Request Body:**
```json
{
  "grade": 10.0,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

**Response 200 OK o 201 Created:**
```json
{
  "id": 1,
  "grade": 10.0,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

---

#### **PUT** `/api/grades/{id}`
Actualiza una nota existente.

**Request Body:**
```json
{
  "grade": 8.5,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "grade": 8.5,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

---

#### **GET** `/api/grades/student/{studentId}/course/{courseId}/average`
Calcula el promedio de notas de un estudiante en un curso.

**Response 200 OK:**
```json
{
  "average": 7.5
}
```

---

## ✅ 7. Asistencias (`/api/attendances`)

### ❌ Estado: **NO IMPLEMENTADO** (solo comentarios en el código)

---

#### **GET** `/api/attendances/course/{courseId}`
Obtiene todas las asistencias de un curso.

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "date": "2024-03-15",
    "present": true,
    "courseId": 1,
    "studentId": 1
  },
  {
    "id": 2,
    "date": "2024-03-15",
    "present": false,
    "courseId": 1,
    "studentId": 2
  }
]
```

---

#### **GET** `/api/attendances/student/{studentId}`
Obtiene todas las asistencias de un estudiante.

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "date": "2024-03-15",
    "present": true,
    "courseId": 1,
    "studentId": 1
  },
  {
    "id": 3,
    "date": "2024-03-20",
    "present": true,
    "courseId": 1,
    "studentId": 1
  }
]
```

---

#### **POST** `/api/attendances`
Registra una asistencia.

**Request Body:**
```json
{
  "date": "2024-03-15",
  "present": true,
  "courseId": 1,
  "studentId": 1
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "date": "2024-03-15",
  "present": true,
  "courseId": 1,
  "studentId": 1
}
```

---

#### **PUT** `/api/attendances/{id}`
Actualiza una asistencia.

**Request Body:**
```json
{
  "date": "2024-03-15",
  "present": false,
  "courseId": 1,
  "studentId": 1
}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "date": "2024-03-15",
  "present": false,
  "courseId": 1,
  "studentId": 1
}
```

---

#### **GET** `/api/attendances/student/{studentId}/course/{courseId}/percentage`
Calcula el porcentaje de asistencia de un estudiante en un curso.

**Response 200 OK:**
```json
{
  "percentage": 85.5
}
```

---

## 📊 8. Excel (`/api/excel`)

### ❌ Estado: **NO IMPLEMENTADO** (solo comentarios en el código)

---

#### **GET** `/api/excel/courses/{courseId}/grades`
Genera y descarga un archivo Excel con la planilla de notas del curso.

**Response 200 OK:**
- **Content-Type:** `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- **Content-Disposition:** `attachment; filename="notas_curso_{courseId}.xlsx"`
- **Body:** Archivo binario Excel

---

## 📋 Resumen de Estados REAL

| Módulo | Estado | Endpoints Funcionales | Notas |
|--------|--------|----------------------|-------|
| `/api/auth/*` | ❌ **NO IMPLEMENTADO** | 0/4 | Solo comentarios en código |
| `/api/professors/*` | ❌ **NO IMPLEMENTADO** | 0/3 | Solo comentarios en código |
| `/api/courses/*` | ❌ **NO IMPLEMENTADO** | 0/6 | Solo comentarios en código |
| `/api/students/*` | ❌ **NO IMPLEMENTADO** | 0/4 | Solo comentarios en código |
| `/api/evaluations/*` | ✅ **IMPLEMENTADO** | 3/3 | ✅ Funcional con validaciones |
| `/api/grades/*` | ❌ **NO IMPLEMENTADO** | 0/4 | Solo comentarios en código |
| `/api/attendances/*` | ❌ **NO IMPLEMENTADO** | 0/5 | Solo comentarios en código |
| `/api/excel/*` | ❌ **NO IMPLEMENTADO** | 0/1 | Solo comentarios en código |

### 📊 Estadísticas:
- **Total de endpoints planificados:** ~30
- **Endpoints realmente implementados:** 3 (10%)
- **Endpoints solo comentarios:** ~27 (90%)

---

## 🔍 Notas Importantes

### Formatos de Fecha
- Todas las fechas deben estar en formato **ISO 8601**: `YYYY-MM-DD`
- Ejemplo: `"2024-03-15"`

### Códigos de Estado HTTP
- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado exitosamente
- `204 No Content`: Operación exitosa sin contenido
- `400 Bad Request`: Error en la solicitud (validación fallida)
- `401 Unauthorized`: No autenticado o token inválido
- `403 Forbidden`: No autorizado
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error interno del servidor

### Validaciones
- Los campos marcados como obligatorios deben estar presentes y no vacíos
- Las validaciones se aplican tanto en el frontend como en el backend
- El backend retorna errores estructurados cuando falla la validación

### Autenticación
- Actualmente Spring Security está **deshabilitado** para desarrollo
- En producción, todos los endpoints (excepto `/api/auth/register` y `/api/auth/login`) requerirán un token JWT en el header:
  ```
  Authorization: Bearer {token}
  ```

---

## 🧪 Cómo Probar los Endpoints

### Con cURL:
```bash
# Crear evaluación (ejemplo funcional)
curl -X POST http://localhost:8080/api/evaluations \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Parcial 1",
    "date": "2024-03-15",
    "tipo": "examen",
    "courseId": 1
  }'
```

### Con Postman:
1. Crear una nueva request
2. Seleccionar el método HTTP (GET, POST, PUT, DELETE)
3. Ingresar la URL completa
4. Para POST/PUT: Agregar header `Content-Type: application/json`
5. En Body → raw → JSON, pegar el JSON de ejemplo

---

¡Listo! Esta es la documentación completa de todos los endpoints. 🚀

