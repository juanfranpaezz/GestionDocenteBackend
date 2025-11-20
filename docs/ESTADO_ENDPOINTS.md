# 📊 Estado de Endpoints - Gestión Docente Backend

**Última actualización:** 15 de Noviembre, 2025  
**Base URL:** `http://localhost:8080`

> 📌 **Este documento se actualiza cada vez que se implementa un nuevo endpoint.**  
> ✅ = Implementado y funcionando | ❌ = No implementado | ⏸️ = Pendiente

---

## 🔐 Autenticación (`/api/auth`)

| Endpoint | Método | Estado | Notas |
|----------|--------|--------|-------|
| `/api/auth/register` | POST | ✅ **FUNCIONANDO** | Registra profesor, encripta contraseña, valida email duplicado |
| `/api/auth/login` | POST | ❌ No implementado | Requiere JWT |
| `/api/auth/logout` | POST | ❌ No implementado | Requiere JWT |
| `/api/auth/me` | GET | ❌ No implementado | Requiere autenticación |

**Total:** 1/4 implementados (25%)

---

## 👨‍🏫 Profesores (`/api/professors`)

| Endpoint | Método | Estado | Notas |
|----------|--------|--------|-------|
| `/api/professors/{id}` | GET | ❌ No implementado | Solo comentarios en código |
| `/api/professors/{id}` | PUT | ❌ No implementado | Solo comentarios en código |
| `/api/professors/email-exists?email={email}` | GET | ❌ No implementado | Solo comentarios en código |

**Total:** 0/3 implementados (0%)

---

## 📚 Cursos (`/api/courses`)

| Endpoint | Método | Estado | Notas |
|----------|--------|--------|-------|
| `/api/courses` | GET | ✅ **FUNCIONANDO** | Obtiene todos los cursos |
| `/api/courses/{id}` | GET | ✅ **FUNCIONANDO** | Obtiene curso por ID, maneja 404 |
| `/api/courses` | POST | ✅ **FUNCIONANDO** | Crea curso, valida profesor existe |
| `/api/courses/professor/{professorId}` | GET | ✅ **FUNCIONANDO** | Obtiene cursos por profesor |
| `/api/courses/{id}` | PUT | ❌ No implementado | Solo comentarios en código |
| `/api/courses/{id}` | DELETE | ❌ No implementado | Servicio tiene método pero no está en controller |

**Total:** 4/6 implementados (67%)

---

## 👨‍🎓 Estudiantes (`/api/students`)

| Endpoint | Método | Estado | Notas |
|----------|--------|--------|-------|
| `/api/students/course/{courseId}` | GET | ❌ No implementado | Solo comentarios en código |
| `/api/students` | POST | ❌ No implementado | Solo comentarios en código |
| `/api/students/{id}` | PUT | ❌ No implementado | Solo comentarios en código |
| `/api/students/{id}` | DELETE | ❌ No implementado | Solo comentarios en código |

**Total:** 0/4 implementados (0%)

---

## 📝 Evaluaciones (`/api/evaluations`)

| Endpoint | Método | Estado | Notas |
|----------|--------|--------|-------|
| `/api/evaluations/course/{courseId}` | GET | ✅ **FUNCIONANDO** | Lista evaluaciones de un curso |
| `/api/evaluations` | POST | ✅ **FUNCIONANDO** | Crea evaluación, valida curso existe, valida campos |
| `/api/evaluations/{id}` | DELETE | ✅ **FUNCIONANDO** | Elimina evaluación, maneja 404 |

**Total:** 3/3 implementados (100%) ✅ **MÓDULO COMPLETO**

---

## 📊 Notas (`/api/grades`)

| Endpoint | Método | Estado | Notas |
|----------|--------|--------|-------|
| `/api/grades/course/{courseId}` | GET | ✅ **FUNCIONANDO** | Obtiene todas las notas de un curso |
| `/api/grades/evaluation/{evaluationId}` | GET | ✅ **FUNCIONANDO** | Obtiene todas las notas de una evaluación |
| `/api/grades` | POST | ✅ **FUNCIONANDO** | Crea o actualiza nota (si ya existe para estudiante+evaluación) |
| `/api/grades/{id}` | PUT | ✅ **FUNCIONANDO** | Actualiza una nota existente por ID |
| `/api/grades/student/{studentId}/course/{courseId}/average` | GET | ✅ **FUNCIONANDO** | Calcula promedio de notas de un estudiante |

**Total:** 5/5 implementados (100%) ✅ **MÓDULO COMPLETO**

---

## ✅ Asistencias (`/api/attendances`)

| Endpoint | Método | Estado | Notas |
|----------|--------|--------|-------|
| `/api/attendances/course/{courseId}` | GET | ❌ No implementado | Solo comentarios en código |
| `/api/attendances/student/{studentId}` | GET | ❌ No implementado | Solo comentarios en código |
| `/api/attendances` | POST | ❌ No implementado | Solo comentarios en código |
| `/api/attendances/{id}` | PUT | ❌ No implementado | Solo comentarios en código |
| `/api/attendances/student/{studentId}/course/{courseId}/percentage` | GET | ❌ No implementado | Solo comentarios en código |

**Total:** 0/5 implementados (0%)

---

## 📊 Excel (`/api/excel`)

| Endpoint | Método | Estado | Notas |
|----------|--------|--------|-------|
| `/api/excel/courses/{courseId}/grades` | GET | ❌ No implementado | Solo comentarios en código |

**Total:** 0/1 implementado (0%)

---

## 📈 Resumen General

| Módulo | Implementados | Total | Porcentaje | Estado |
|--------|---------------|-------|------------|--------|
| **Autenticación** | 1 | 4 | 25% | ⏸️ Parcial |
| **Profesores** | 0 | 3 | 0% | ❌ Pendiente |
| **Cursos** | 4 | 6 | 67% | ✅ Mayormente completo |
| **Estudiantes** | 0 | 4 | 0% | ❌ Pendiente |
| **Evaluaciones** | 3 | 3 | 100% | ✅ **COMPLETO** |
| **Notas** | 5 | 5 | 100% | ✅ **COMPLETO** |
| **Asistencias** | 0 | 5 | 0% | ❌ Pendiente |
| **Excel** | 0 | 1 | 0% | ❌ Pendiente |
| **TOTAL** | **13** | **31** | **42%** | ⏸️ En desarrollo |

---

## ✅ Endpoints Funcionales (Lista Rápida)

### Para Probar en Postman:

1. ✅ `POST /api/auth/register` - Registrar profesor
2. ✅ `GET /api/courses` - Listar todos los cursos
3. ✅ `GET /api/courses/{id}` - Obtener curso por ID
4. ✅ `POST /api/courses` - Crear curso
5. ✅ `GET /api/courses/professor/{professorId}` - Cursos de un profesor
6. ✅ `GET /api/evaluations/course/{courseId}` - Evaluaciones de un curso
7. ✅ `POST /api/evaluations` - Crear evaluación
8. ✅ `DELETE /api/evaluations/{id}` - Eliminar evaluación
9. ✅ `GET /api/grades/course/{courseId}` - Notas de un curso
10. ✅ `GET /api/grades/evaluation/{evaluationId}` - Notas de una evaluación
11. ✅ `POST /api/grades` - Crear o actualizar nota
12. ✅ `PUT /api/grades/{id}` - Actualizar nota por ID
13. ✅ `GET /api/grades/student/{studentId}/course/{courseId}/average` - Promedio de estudiante

**Total de endpoints funcionales:** 13

---

## 📝 Notas de Implementación

### ✅ Lo que funciona bien:
- Validaciones con `@Valid` y mensajes en español
- Manejo de errores consistente (GlobalExceptionHandler)
- Validación de existencia de entidades relacionadas
- Respuestas HTTP correctas (201, 200, 404, 400)
- Encriptación de contraseñas con BCrypt

### ⏸️ Pendiente de implementar:
- JWT para autenticación
- Endpoints de actualización (PUT)
- Endpoints de eliminación (DELETE) en algunos módulos
- Módulos completos: Students, Grades, Attendances, Excel
- Filtrado por profesor autenticado (cuando se implemente JWT)

---

## 🔄 Cómo Actualizar Este Documento

Cuando implementes un nuevo endpoint:

1. Cambia el estado de ❌ a ✅
2. Actualiza el contador en la sección del módulo
3. Actualiza el resumen general
4. Agrega el endpoint a la lista de "Endpoints Funcionales"
5. Actualiza la fecha de "Última actualización"

---

**Última revisión:** Todos los endpoints marcados como ✅ fueron probados y funcionan correctamente.

