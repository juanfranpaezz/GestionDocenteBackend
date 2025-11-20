# 🚀 Endpoints para Postman - Gestión Docente Backend (Nuevos)

**Base URL:** `http://localhost:8080`  
**Servidor:** Ejecutándose en puerto 8080

> ⚠️ **Nota:** Esta lista contiene solo los endpoints que NO estaban en la lista anterior.  
> Los siguientes endpoints ya fueron documentados:
> - POST /api/auth/register
> - GET /api/courses
> - GET /api/courses/{id}
> - POST /api/courses
> - GET /api/courses/professor/{professorId}
> - GET /api/evaluations/course/{courseId}
> - POST /api/evaluations

---

## 📝 **1. Eliminar una Evaluación**

**Endpoint:** `DELETE /api/evaluations/{id}`

**Ejemplo:** `DELETE /api/evaluations/1`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa (204 No Content):**
```
(Respuesta vacía)
```

**Respuesta de error (404 Not Found):**
```json
{
  "error": "La evaluación con ID 1 no existe"
}
```

**Respuesta de error (500 Internal Server Error):**
```json
{
  "error": "Error al eliminar la evaluación: [mensaje de error]"
}
```

---

## 📊 **2. Obtener Notas de un Curso**

**Endpoint:** `GET /api/grades/course/{courseId}`

**Ejemplo:** `GET /api/grades/course/1`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Parámetros opcionales de paginación:**
- `?page=0` - Número de página (0-indexed)
- `?size=20` - Tamaño de página (default: 20)
- `?sort=id,asc` - Ordenamiento
- `?paginated=true` - Forzar paginación

**Ejemplos:**
```
GET /api/grades/course/1
GET /api/grades/course/1?page=0&size=10
GET /api/grades/course/1?paginated=true&sort=id,desc
```

**Respuesta exitosa - Lista completa (200 OK):**
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

**Respuesta exitosa - Con paginación (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "grade": 8.5,
      "courseId": 1,
      "studentId": 1,
      "evaluationId": 1
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 2,
  "first": true,
  "empty": false
}
```

**Respuesta de error (404 Not Found):**
```json
{
  "error": "El curso con ID 1 no existe"
}
```

**Respuesta vacía (200 OK):**
```json
[]
```

---

## 📊 **3. Obtener Notas de una Evaluación**

**Endpoint:** `GET /api/grades/evaluation/{evaluationId}`

**Ejemplo:** `GET /api/grades/evaluation/1`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Parámetros opcionales de paginación:**
- `?page=0` - Número de página (0-indexed)
- `?size=20` - Tamaño de página (default: 20)
- `?sort=id,asc` - Ordenamiento
- `?paginated=true` - Forzar paginación

**Ejemplos:**
```
GET /api/grades/evaluation/1
GET /api/grades/evaluation/1?page=0&size=10
GET /api/grades/evaluation/1?paginated=true&sort=id,desc
```

**Respuesta exitosa - Lista completa (200 OK):**
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

**Respuesta exitosa - Con paginación (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "grade": 8.5,
      "courseId": 1,
      "studentId": 1,
      "evaluationId": 1
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true
}
```

**Respuesta de error (404 Not Found):**
```json
{
  "error": "La evaluación con ID 1 no existe"
}
```

---

## 📊 **4. Crear o Actualizar una Nota**

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
- `grade` (Double, entre 0.0 y 10.0)
- `courseId` (Long, no puede ser null)
- `studentId` (Long, no puede ser null)
- `evaluationId` (Long, no puede ser null)

**Características especiales:**
- ✅ Si ya existe una nota para el mismo `studentId` y `evaluationId`, la actualiza automáticamente
- ✅ Valida que el `studentId` pertenezca al `courseId`
- ✅ Valida que el `evaluationId` pertenezca al `courseId`
- ✅ Valida que la nota esté entre 0 y 10

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

**Respuesta de error - Validación (400 Bad Request):**
```json
{
  "error": "La nota debe estar entre 0 y 10"
}
```

**Respuesta de error - Consistencia (400 Bad Request):**
```json
{
  "error": "El estudiante con ID 1 no pertenece al curso con ID 1"
}
```

```json
{
  "error": "La evaluación con ID 1 no pertenece al curso con ID 1"
}
```

**Respuesta de error - No existe (400 Bad Request):**
```json
{
  "error": "El estudiante con ID 999 no existe"
}
```

```json
{
  "error": "La evaluación con ID 999 no existe"
}
```

```json
{
  "error": "El curso con ID 999 no existe"
}
```

---

## 📊 **5. Actualizar una Nota por ID**

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

**Campos opcionales (puedes actualizar solo algunos):**
- `grade` (Double, entre 0.0 y 10.0) - Opcional
- `courseId` (Long) - Opcional
- `studentId` (Long) - Opcional
- `evaluationId` (Long) - Opcional

**Ejemplo - Actualizar solo la nota:**
```json
{
  "grade": 9.5
}
```

**Ejemplo - Actualizar nota y estudiante:**
```json
{
  "grade": 8.0,
  "studentId": 2
}
```

**Características:**
- ✅ Valida consistencia si actualizas `courseId`, `studentId` o `evaluationId`
- ✅ Valida rango de nota (0-10) si proporcionas `grade`
- ✅ Solo actualiza los campos que proporcionas

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

**Respuesta de error - No existe (400 Bad Request):**
```json
{
  "error": "La nota con ID 999 no existe"
}
```

**Respuesta de error - Validación (400 Bad Request):**
```json
{
  "error": "La nota debe estar entre 0 y 10"
}
```

**Respuesta de error - Consistencia (400 Bad Request):**
```json
{
  "error": "El estudiante con ID 2 no pertenece al curso con ID 1"
}
```

---

## 📊 **6. Calcular Promedio de Notas de un Estudiante**

**Endpoint:** `GET /api/grades/student/{studentId}/course/{courseId}/average`

**Ejemplo:** `GET /api/grades/student/1/course/1/average`

**Headers:** (No requiere headers especiales)

**Body:** (No requiere body)

**Respuesta exitosa - Con notas (200 OK):**
```json
{
  "average": 8.25,
  "studentId": 1,
  "courseId": 1
}
```

**Respuesta exitosa - Sin notas (200 OK):**
```json
{
  "average": null,
  "message": "El estudiante no tiene notas registradas en este curso",
  "studentId": 1,
  "courseId": 1
}
```

**Nota:** El promedio se calcula solo con las notas que tienen valor (no null). Si un estudiante tiene notas null, esas no se incluyen en el cálculo.

**Respuesta de error (404 Not Found):**
```json
{
  "error": "El estudiante con ID 999 no existe"
}
```

```json
{
  "error": "El curso con ID 999 no existe"
}
```

---

## 📋 Resumen de Endpoints Nuevos

| # | Método | Endpoint | Descripción |
|---|--------|----------|-------------|
| 1 | `DELETE` | `/api/evaluations/{id}` | Elimina una evaluación |
| 2 | `GET` | `/api/grades/course/{courseId}` | Obtiene notas de un curso (con paginación) |
| 3 | `GET` | `/api/grades/evaluation/{evaluationId}` | Obtiene notas de una evaluación (con paginación) |
| 4 | `POST` | `/api/grades` | Crea o actualiza una nota |
| 5 | `PUT` | `/api/grades/{id}` | Actualiza una nota por ID |
| 6 | `GET` | `/api/grades/student/{studentId}/course/{courseId}/average` | Calcula promedio de estudiante |

---

## 🔍 Validaciones Importantes

### Para Notas (POST/PUT):
1. **Rango de nota:** Debe estar entre 0.0 y 10.0
2. **Consistencia de estudiante:** El `studentId` debe pertenecer al `courseId` especificado
3. **Consistencia de evaluación:** El `evaluationId` debe pertenecer al `courseId` especificado
4. **Existencia:** Todos los IDs (courseId, studentId, evaluationId) deben existir en la base de datos

### Ejemplo de Error de Consistencia:
Si intentas crear una nota con:
- `courseId: 1`
- `studentId: 2` (pero el estudiante 2 pertenece al curso 3)
- `evaluationId: 1`

Obtendrás:
```json
{
  "error": "El estudiante con ID 2 no pertenece al curso con ID 1"
}
```

---

## 💡 Tips para Probar en Postman

1. **Orden sugerido de pruebas:**
   - Primero crea un profesor (ya documentado)
   - Luego crea un curso (ya documentado)
   - Crea una evaluación (ya documentado)
   - Crea estudiantes (pendiente de implementar)
   - Crea notas con `POST /api/grades`
   - Prueba obtener notas con `GET /api/grades/course/{courseId}`
   - Prueba calcular promedio con `GET /api/grades/student/{studentId}/course/{courseId}/average`
   - Actualiza notas con `PUT /api/grades/{id}`
   - Elimina evaluación con `DELETE /api/evaluations/{id}`

2. **Para probar paginación:**
   - Primero crea varias notas (al menos 25 para ver el efecto)
   - Luego prueba: `GET /api/grades/course/1?page=0&size=10`
   - Cambia `page=1` para ver la segunda página
   - Prueba diferentes `size` (5, 10, 20, 50)

3. **Para probar validaciones:**
   - Intenta crear una nota con `grade: 11` → Debe dar error
   - Intenta crear una nota con `grade: -1` → Debe dar error
   - Intenta crear una nota con `studentId` que no pertenece al `courseId` → Debe dar error de consistencia

---

## 🎯 Ejemplo Completo de Flujo

### Paso 1: Crear una nota
```
POST /api/grades
Body:
{
  "grade": 8.5,
  "courseId": 1,
  "studentId": 1,
  "evaluationId": 1
}
```

### Paso 2: Obtener todas las notas del curso
```
GET /api/grades/course/1
```

### Paso 3: Calcular promedio del estudiante
```
GET /api/grades/student/1/course/1/average
```

### Paso 4: Actualizar la nota
```
PUT /api/grades/1
Body:
{
  "grade": 9.0
}
```

### Paso 5: Verificar el nuevo promedio
```
GET /api/grades/student/1/course/1/average
```

---

¡Listo para probar! 🚀

