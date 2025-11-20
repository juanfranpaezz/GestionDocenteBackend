# 🧪 Guía Paso a Paso - Pruebas con Postman

## 📋 Configuración Inicial

### 1. Verificar que el servidor esté corriendo
- El servidor debe estar en: `http://localhost:8080`
- Si no está corriendo, ejecuta: `.\mvnw.cmd spring-boot:run`

### 2. Configurar Postman
- Base URL: `http://localhost:8080/api`
- Headers para POST/PUT: `Content-Type: application/json`

---

## 🔄 Flujo Completo de Pruebas

### **PASO 1: Crear un Profesor** ✅

**Endpoint:** `POST http://localhost:8080/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
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

**Respuesta Esperada (201 Created):**
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
⚠️ **Nota:** El `id` se genera automáticamente. **Guarda este ID** para el siguiente paso.

**Si el email ya existe, recibirás (400 Bad Request):**
```json
{
  "error": "El email juan.perez@example.com ya está registrado"
}
```

---

### **PASO 2: Verificar que el Profesor se creó** ✅

**Endpoint:** `GET http://localhost:8080/api/courses/professor/1`

**Headers:** (ninguno necesario)

**Respuesta Esperada (200 OK):**
```json
[]
```
(Array vacío porque aún no hay cursos, pero confirma que el profesor existe)

**Si el profesor no existe (404 Not Found):**
```json
{
  "error": "El profesor con ID 999 no existe"
}
```

---

### **PASO 3: Crear un Curso** ✅

**Endpoint:** `POST http://localhost:8080/api/courses`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "name": "4toC",
  "school": "EES69",
  "description": "Curso de prueba para evaluaciones",
  "professorId": 1
}
```
⚠️ **Importante:** Usa el `professorId` que obtuviste en el Paso 1 (probablemente `1`).

**Respuesta Esperada (201 Created):**
```json
{
  "id": 1,
  "name": "4toC",
  "school": "EES69",
  "description": "Curso de prueba para evaluaciones",
  "professorId": 1
}
```
⚠️ **Nota:** El `id` se genera automáticamente. **Guarda este ID** para el siguiente paso.

**Si el profesor no existe (400 Bad Request):**
```json
{
  "error": "El profesor con ID 999 no existe"
}
```

**Si faltan campos obligatorios (400 Bad Request):**
```json
{
  "error": "Error de validación",
  "campos": {
    "name": "El nombre del curso es obligatorio",
    "school": "La escuela es obligatoria"
  },
  "mensaje": "Por favor, complete todos los campos obligatorios"
}
```

---

### **PASO 4: Verificar que el Curso se creó** ✅

**Endpoint:** `GET http://localhost:8080/api/courses`

**Headers:** (ninguno necesario)

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "name": "4toC",
    "school": "EES69",
    "description": "Curso de prueba para evaluaciones",
    "professorId": 1
  }
]
```

**O también puedes usar:**
**Endpoint:** `GET http://localhost:8080/api/courses/1`

**Respuesta Esperada (200 OK):**
```json
{
  "id": 1,
  "name": "4toC",
  "school": "EES69",
  "description": "Curso de prueba para evaluaciones",
  "professorId": 1
}
```

---

### **PASO 5: Crear una Evaluación** ✅

**Endpoint:** `POST http://localhost:8080/api/evaluations`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "Parcial 1",
  "date": "2024-03-15",
  "tipo": "examen",
  "courseId": 1
}
```
⚠️ **Importante:** Usa el `courseId` que obtuviste en el Paso 3 (probablemente `1`).

**Respuesta Esperada (201 Created):**
```json
{
  "id": 1,
  "nombre": "Parcial 1",
  "date": "2024-03-15",
  "tipo": "examen",
  "courseId": 1
}
```

**Si el curso no existe (400 Bad Request):**
```json
{
  "error": "El curso con ID 999 no existe"
}
```

**Si faltan campos obligatorios (400 Bad Request):**
```json
{
  "error": "Error de validación",
  "campos": {
    "nombre": "El nombre de la evaluación es obligatorio",
    "tipo": "El tipo de evaluación es obligatorio",
    "date": "La fecha es obligatoria"
  },
  "mensaje": "Por favor, complete todos los campos obligatorios"
}
```

---

### **PASO 6: Verificar que la Evaluación se creó** ✅

**Endpoint:** `GET http://localhost:8080/api/evaluations/course/1`

**Headers:** (ninguno necesario)

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Parcial 1",
    "date": "2024-03-15",
    "tipo": "examen",
    "courseId": 1
  }
]
```

---

## 🧪 Pruebas Adicionales

### **Prueba de Validaciones - Email Duplicado**

**Endpoint:** `POST http://localhost:8080/api/auth/register`

**Body:**
```json
{
  "name": "Otro",
  "lastname": "Usuario",
  "email": "juan.perez@example.com",
  "password": "password123"
}
```

**Respuesta Esperada (400 Bad Request):**
```json
{
  "error": "El email juan.perez@example.com ya está registrado"
}
```

---

### **Prueba de Validaciones - Campos Faltantes**

**Endpoint:** `POST http://localhost:8080/api/courses`

**Body:**
```json
{
  "name": "",
  "school": "EES69"
}
```

**Respuesta Esperada (400 Bad Request):**
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

## 📊 Resumen de Endpoints para Probar

| # | Método | Endpoint | Descripción |
|---|--------|----------|-------------|
| 1 | POST | `/api/auth/register` | Crear profesor |
| 2 | GET | `/api/courses/professor/1` | Verificar profesor existe |
| 3 | POST | `/api/courses` | Crear curso |
| 4 | GET | `/api/courses` | Listar todos los cursos |
| 5 | GET | `/api/courses/1` | Obtener curso por ID |
| 6 | GET | `/api/courses/professor/1` | Listar cursos de un profesor |
| 7 | POST | `/api/evaluations` | Crear evaluación |
| 8 | GET | `/api/evaluations/course/1` | Listar evaluaciones de un curso |

---

## ⚠️ Notas Importantes

1. **IDs Generados:** Los IDs se generan automáticamente. El primer profesor será `id: 1`, el primer curso será `id: 1`, etc.

2. **Fechas:** Usa formato ISO 8601: `YYYY-MM-DD` (ej: `"2024-03-15"`)

3. **Tipos de Evaluación:** Puedes usar: `"examen"`, `"práctica"`, `"tarea"`, etc.

4. **Contraseñas:** Se encriptan automáticamente con BCrypt. No se retornan en las respuestas.

5. **Errores:** Todos los errores retornan un objeto JSON con el campo `"error"` y un mensaje descriptivo.

---

## 🎯 Orden Recomendado de Pruebas

1. ✅ Crear profesor (Paso 1)
2. ✅ Crear curso (Paso 3) - usando el `professorId` del paso 1
3. ✅ Crear evaluación (Paso 5) - usando el `courseId` del paso 3
4. ✅ Verificar todo con los GETs (Pasos 2, 4, 6)

¡Listo para probar! 🚀

