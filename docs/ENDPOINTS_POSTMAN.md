# 🚀 Endpoints para Postman - Gestión Docente Backend

**Base URL:** `http://localhost:8080`  
**Servidor:** Ejecutándose en puerto 8080

---

## 📋 Endpoints Disponibles

### ✅ **1. Registrar un Profesor**

**Endpoint:** `POST /api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "password": "password123",
  "cel": "1234567890",
  "photoUrl": "https://example.com/foto.jpg"
}
```

**Campos obligatorios:**
- `name` (String, no puede estar vacío)
- `lastname` (String, no puede estar vacío)
- `email` (String, formato de email válido, no puede estar vacío)
- `password` (String, no puede estar vacío)

**Campos opcionales:**
- `cel` (String)
- `photoUrl` (String)

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "cel": "1234567890",
  "photoUrl": "https://example.com/foto.jpg"
}
```

**Respuesta de error (400 Bad Request):**
```json
{
  "error": "El email juan.perez@example.com ya está registrado"
}
```

---

### ✅ **2. Obtener Todos los Cursos**

**Endpoint:** `GET /api/courses`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Matemáticas I",
    "school": "Facultad de Ciencias",
    "description": "Curso de matemáticas básicas",
    "professorId": 1
  },
  {
    "id": 2,
    "name": "Programación I",
    "school": "Facultad de Ingeniería",
    "description": "Introducción a la programación",
    "professorId": 1
  }
]
```

**Respuesta vacía (200 OK):**
```json
[]
```

---

### ✅ **3. Obtener un Curso por ID**

**Endpoint:** `GET /api/courses/{id}`

**Ejemplo:** `GET /api/courses/1`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "name": "Matemáticas I",
  "school": "Facultad de Ciencias",
  "description": "Curso de matemáticas básicas",
  "professorId": 1
}
```

**Respuesta de error (404 Not Found):**
```json
{
  "error": "El curso con ID 1 no existe"
}
```

---

### ✅ **4. Crear un Curso**

**Endpoint:** `POST /api/courses`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "name": "Matemáticas I",
  "school": "Facultad de Ciencias",
  "description": "Curso de matemáticas básicas",
  "professorId": 1
}
```

**Campos obligatorios:**
- `name` (String, no puede estar vacío)
- `school` (String, no puede estar vacío)
- `professorId` (Long, debe existir en la base de datos)

**Campos opcionales:**
- `description` (String)

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "name": "Matemáticas I",
  "school": "Facultad de Ciencias",
  "description": "Curso de matemáticas básicas",
  "professorId": 1
}
```

**Respuesta de error (400 Bad Request):**
```json
{
  "error": "El profesor con ID 1 no existe"
}
```

**Respuesta de validación (400 Bad Request):**
```json
{
  "error": "Error de validación",
  "campos": {
    "name": "El nombre del curso es obligatorio",
    "professorId": "El ID del profesor es obligatorio"
  },
  "mensaje": "Por favor, complete todos los campos obligatorios"
}
```

---

### ✅ **5. Obtener Cursos por Profesor**

**Endpoint:** `GET /api/courses/professor/{professorId}`

**Ejemplo:** `GET /api/courses/professor/1`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Matemáticas I",
    "school": "Facultad de Ciencias",
    "description": "Curso de matemáticas básicas",
    "professorId": 1
  },
  {
    "id": 2,
    "name": "Programación I",
    "school": "Facultad de Ingeniería",
    "description": "Introducción a la programación",
    "professorId": 1
  }
]
```

**Respuesta vacía (200 OK):**
```json
[]
```

**Respuesta de error (404 Not Found):**
```json
{
  "error": "El profesor con ID 1 no existe"
}
```

---

### ✅ **6. Obtener Evaluaciones de un Curso**

**Endpoint:** `GET /api/evaluations/course/{courseId}`

**Ejemplo:** `GET /api/evaluations/course/1`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Examen Parcial",
    "date": "2025-11-20",
    "tipo": "examen",
    "courseId": 1
  },
  {
    "id": 2,
    "nombre": "Trabajo Práctico 1",
    "date": "2025-11-25",
    "tipo": "práctica",
    "courseId": 1
  }
]
```

**Respuesta vacía (200 OK):**
```json
[]
```

---

### ✅ **7. Crear una Evaluación**

**Endpoint:** `POST /api/evaluations`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nombre": "Examen Parcial",
  "date": "2025-11-20",
  "tipo": "examen",
  "courseId": 1
}
```

**Campos obligatorios:**
- `nombre` (String, no puede estar vacío)
- `date` (String, formato: "YYYY-MM-DD", no puede ser null)
- `tipo` (String, no puede estar vacío - ej: "examen", "práctica", "tarea")
- `courseId` (Long, debe existir en la base de datos)

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "nombre": "Examen Parcial",
  "date": "2025-11-20",
  "tipo": "examen",
  "courseId": 1
}
```

**Respuesta de error (400 Bad Request):**
```json
{
  "error": "El curso con ID 1 no existe"
}
```

**Respuesta de validación (400 Bad Request):**
```json
{
  "error": "Error de validación",
  "campos": {
    "nombre": "El nombre de la evaluación es obligatorio",
    "date": "La fecha es obligatoria",
    "tipo": "El tipo de evaluación es obligatorio",
    "courseId": "El ID del curso es obligatorio"
  },
  "mensaje": "Por favor, complete todos los campos obligatorios"
}
```

---

### ✅ **8. Eliminar una Evaluación**

**Endpoint:** `DELETE /api/evaluations/{id}`

**Ejemplo:** `DELETE /api/evaluations/1`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa (204 No Content):**
```
(Sin contenido)
```

**Respuesta de error (404 Not Found):**
```json
{
  "error": "La evaluación con ID 1 no existe"
}
```

---

### ✅ **9. Obtener Notas de un Curso**

**Endpoint:** `GET /api/grades/course/{courseId}`

**Ejemplo:** `GET /api/grades/course/1`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "grade": 8.5,
    "courseId": 1,
    "studentId": 1,
    "evaluationId": 1
  },
  {
    "id": 2,
    "grade": 7.0,
    "courseId": 1,
    "studentId": 2,
    "evaluationId": 1
  }
]
```

---

### ✅ **10. Obtener Notas de una Evaluación**

**Endpoint:** `GET /api/grades/evaluation/{evaluationId}`

**Ejemplo:** `GET /api/grades/evaluation/1`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "grade": 8.5,
    "courseId": 1,
    "studentId": 1,
    "evaluationId": 1
  },
  {
    "id": 2,
    "grade": 7.0,
    "courseId": 1,
    "studentId": 2,
    "evaluationId": 1
  }
]
```

---

### ✅ **11. Crear o Actualizar una Nota**

**Endpoint:** `POST /api/grades`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "grade": 8.5,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

**Campos obligatorios:**
- `grade` (Double, entre 0.0 y 10.0, no puede ser null)
- `courseId` (Long, debe existir en la base de datos)
- `studentId` (Long, debe existir en la base de datos)
- `evaluationId` (Long, debe existir en la base de datos)

**Comportamiento:**
- Si ya existe una nota para el mismo `studentId` y `evaluationId`, la **actualiza**
- Si no existe, **crea** una nueva nota

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "grade": 8.5,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

**Respuesta de error (400 Bad Request):**
```json
{
  "error": "El estudiante con ID 1 no existe"
}
```

**Respuesta de validación (400 Bad Request):**
```json
{
  "error": "Error de validación",
  "campos": {
    "grade": "La nota no puede ser mayor a 10",
    "studentId": "El ID del estudiante es obligatorio"
  },
  "mensaje": "Por favor, complete todos los campos obligatorios"
}
```

---

### ✅ **12. Actualizar una Nota por ID**

**Endpoint:** `PUT /api/grades/{id}`

**Ejemplo:** `PUT /api/grades/1`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "grade": 9.0,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

**Campos:**
- Todos los campos son opcionales (solo actualiza los que envíes)
- `grade` debe estar entre 0.0 y 10.0 si se envía

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "grade": 9.0,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

**Respuesta de error (400 Bad Request):**
```json
{
  "error": "La nota debe estar entre 0 y 10"
}
```

**Respuesta de error (404 Not Found):**
```json
{
  "error": "La nota con ID 1 no existe"
}
```

---

### ✅ **13. Calcular Promedio de un Estudiante**

**Endpoint:** `GET /api/grades/student/{studentId}/course/{courseId}/average`

**Ejemplo:** `GET /api/grades/student/1/course/1/average`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa (200 OK):**
```json
{
  "average": 7.5,
  "studentId": 1,
  "courseId": 1
}
```

**Respuesta sin notas (200 OK):**
```json
{
  "average": null,
  "message": "El estudiante no tiene notas registradas en este curso",
  "studentId": 1,
  "courseId": 1
}
```

---

## 📝 Orden Recomendado para Probar

### **Paso 1: Crear un Profesor**
```
POST /api/auth/register
```
Necesitas crear un profesor primero porque los cursos requieren un `professorId` válido.

### **Paso 2: Crear un Curso**
```
POST /api/courses
```
Usa el `id` del profesor que creaste en el Paso 1.

### **Paso 3: Crear una Evaluación**
```
POST /api/evaluations
```
Usa el `id` del curso que creaste en el Paso 2.

### **Paso 4: Consultar los Datos**
```
GET /api/courses
GET /api/courses/1
GET /api/courses/professor/1
GET /api/evaluations/course/1
```

### **Paso 5: Crear una Nota**
```
POST /api/grades
```
Usa el `id` del estudiante, curso y evaluación que creaste anteriormente.

### **Paso 6: Consultar las Notas**
```
GET /api/grades/evaluation/1
GET /api/grades/course/1
GET /api/grades/student/1/course/1/average
```

### **Paso 7: Actualizar una Nota (Opcional)**
```
PUT /api/grades/1
```

### **Paso 8: Eliminar una Evaluación (Opcional)**
```
DELETE /api/evaluations/1
```

---

## 🎯 Ejemplo Completo de Flujo

### 1. Crear Profesor
**POST** `http://localhost:8080/api/auth/register`
```json
{
  "name": "María",
  "lastname": "González",
  "email": "maria.gonzalez@example.com",
  "password": "password123"
}
```
**Respuesta:** `{"id": 1, ...}` ← **Guarda este ID**

### 2. Crear Curso
**POST** `http://localhost:8080/api/courses`
```json
{
  "name": "Álgebra Lineal",
  "school": "Facultad de Ciencias Exactas",
  "description": "Curso de álgebra lineal para ingeniería",
  "professorId": 1
}
```
**Respuesta:** `{"id": 1, ...}` ← **Guarda este ID**

### 3. Crear Evaluación
**POST** `http://localhost:8080/api/evaluations`
```json
{
  "nombre": "Primer Parcial",
  "date": "2025-12-01",
  "tipo": "examen",
  "courseId": 1
}
```
**Respuesta:** `{"id": 1, ...}`

### 4. Consultar Evaluaciones del Curso
**GET** `http://localhost:8080/api/evaluations/course/1`
**Respuesta:** `[{...}]` ← Lista de evaluaciones

---

## ⚠️ Errores Comunes

### Error: "El profesor con ID X no existe"
- **Causa:** Intentaste crear un curso con un `professorId` que no existe
- **Solución:** Primero crea el profesor con `POST /api/auth/register`

### Error: "El curso con ID X no existe"
- **Causa:** Intentaste crear una evaluación con un `courseId` que no existe
- **Solución:** Primero crea el curso con `POST /api/courses`

### Error: "El email X ya está registrado"
- **Causa:** Intentaste registrar un profesor con un email que ya existe
- **Solución:** Usa un email diferente o elimina el profesor existente (aún no implementado)

### Error: Validación de campos
- **Causa:** Faltan campos obligatorios o tienen formato incorrecto
- **Solución:** Revisa el JSON y asegúrate de incluir todos los campos obligatorios

---

## 📌 Notas Importantes

1. **Base de datos H2 en memoria:** Los datos se pierden cuando reinicias el servidor
2. **Sin autenticación:** Por ahora no necesitas tokens JWT (está deshabilitado para desarrollo)
3. **Formato de fecha:** Usa formato `YYYY-MM-DD` (ej: "2025-11-20")
4. **IDs:** Los IDs se generan automáticamente, no los incluyas al crear nuevos recursos

---

## 🔗 Ver la Base de Datos

Si quieres ver los datos directamente en la base de datos:

1. Abre tu navegador
2. Ve a: `http://localhost:8080/h2-console`
3. Ingresa:
   - **JDBC URL:** `jdbc:h2:mem:gestiondocente`
   - **Usuario:** `sa`
   - **Contraseña:** (déjala vacía)
4. Haz clic en **Connect**
5. Ejecuta queries como:
   ```sql
   SELECT * FROM professors;
   SELECT * FROM courses;
   SELECT * FROM evaluations;
   ```

---

¡Listo para probar! 🚀

