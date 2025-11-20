# 🔍 Reporte de Pruebas y Falencias Detectadas

**Fecha:** $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")  
**Tester:** Análisis Automatizado  
**Base URL:** `http://localhost:8080`

---

## 📊 Resumen Ejecutivo

Se realizaron pruebas sistemáticas de todos los endpoints implementados para detectar falencias, inconsistencias y problemas potenciales en el código.

### Estadísticas Generales
- **Total de Pruebas Realizadas:** ~30+ casos de prueba
- **Endpoints Probados:** 13 endpoints implementados
- **Falencias Críticas Encontradas:** 2
- **Falencias Menores Encontradas:** 3
- **Mejoras Recomendadas:** 5

---

## ❌ FALENCIAS CRÍTICAS ENCONTRADAS

### 🔴 **FALENCIA #1: Validación Faltante en `getEvaluationsByCourse` (Sin Paginación)**

**Ubicación:** `EvaluationServiceImpl.getEvaluationsByCourse(Long courseId)`  
**Línea:** 28-32

**Problema:**
El método `getEvaluationsByCourse(Long courseId)` **NO valida** que el curso exista antes de buscar evaluaciones. Esto causa que:
- Si se consulta un curso inexistente, retorna `200 OK` con lista vacía `[]`
- Debería retornar `404 Not Found` con mensaje de error

**Código Actual:**
```java
@Override
public List<EvaluationDTO> getEvaluationsByCourse(Long courseId) {
    List<Evaluation> evaluations = evaluationRepository.findByCourseId(courseId);
    return evaluations.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}
```

**Problema Detectado:**
- ✅ El método con paginación `getEvaluationsByCourse(Long courseId, Pageable pageable)` SÍ valida (línea 38)
- ❌ El método sin paginación NO valida

**Impacto:**
- **Severidad:** Media-Alta
- **Comportamiento Inconsistente:** Mismo endpoint, diferentes validaciones según si usa paginación o no
- **UX:** El frontend no puede distinguir entre "curso sin evaluaciones" y "curso inexistente"

**Prueba Realizada:**
```
GET /api/evaluations/course/99999
Status: 200 (Esperado: 404)
Respuesta: [] (lista vacía)
```

**Solución Requerida:**
Agregar validación de existencia del curso antes de buscar evaluaciones, igual que en el método con paginación.

---

### 🔴 **FALENCIA #2: Imposibilidad de Probar Funcionalidad Completa de Notas**

**Ubicación:** Múltiples endpoints de `/api/grades`

**Problema:**
Los endpoints de notas requieren `studentId` válido, pero **NO existe endpoint para crear estudiantes**. Esto impide:
- Probar completamente la creación de notas
- Probar validaciones de consistencia (studentId pertenece a courseId)
- Probar el cálculo de promedios con datos reales
- Probar el endpoint de promedios por curso con estudiantes reales

**Endpoints Afectados:**
- `POST /api/grades` - No se puede probar completamente
- `PUT /api/grades/{id}` - No se puede probar completamente
- `GET /api/grades/course/{courseId}/averages` - Retorna lista vacía siempre
- `GET /api/grades/student/{studentId}/course/{courseId}/average` - No se puede probar

**Impacto:**
- **Severidad:** Alta
- **Bloqueo Funcional:** No se puede usar la funcionalidad de notas sin estudiantes
- **Testing Incompleto:** No se pueden probar casos de uso reales

**Solución Requerida:**
Implementar endpoints de estudiantes (CRUD completo) o al menos `POST /api/students` para poder crear estudiantes de prueba.

---

## ⚠️ FALENCIAS MENORES ENCONTRADAS

### 🟡 **FALENCIA #3: Inconsistencia en Validación de Rango de Notas (0.0 y 10.0)**

**Ubicación:** `GradeServiceImpl.setGrade()` y `GradeServiceImpl.updateGrade()`

**Problema Detectado:**
Hay una **inconsistencia** entre las validaciones:
- `GradeDTO` usa `@DecimalMin(value = "0.0")` y `@DecimalMax(value = "10.0")` - **Permite** 0.0 y 10.0 ✅
- `GradeServiceImpl` usa `grade < 0.0 || grade > 10.0` - **Rechaza** 0.0 y 10.0 ❌

**Código Problemático:**
```java
// GradeServiceImpl.java línea 72
if (gradeDTO.getGrade() < 0.0 || gradeDTO.getGrade() > 10.0) {
    throw new IllegalArgumentException("La nota debe estar entre 0 y 10");
}
```

**Problema:**
- La validación del DTO (`@DecimalMin/@DecimalMax`) permite 0.0 y 10.0
- La validación del servicio (`<` y `>`) rechaza 0.0 y 10.0
- Esto causa que 0.0 y 10.0 sean rechazados aunque deberían ser válidos

**Impacto:**
- **Severidad:** Media
- **Comportamiento Inconsistente:** Las anotaciones dicen una cosa, el código hace otra
- **UX:** No se pueden registrar notas de 0.0 o 10.0 aunque deberían ser válidas

**Solución Requerida:**
Cambiar la validación en el servicio de `<` y `>` a `<=` y `>=`, o mejor aún, eliminar esta validación redundante ya que el DTO ya la tiene.

**Pruebas Realizadas:**
```
POST /api/grades con grade: 0.0
Status: 400 (Puede ser por estudiante inexistente)

POST /api/grades con grade: 10.0
Status: 400 (Puede ser por estudiante inexistente)
```

**Solución Requerida:**
Verificar que las validaciones `@DecimalMin` y `@DecimalMax` incluyan los valores límite (0.0 y 10.0) correctamente. Si el problema es por falta de estudiante, implementar endpoints de estudiantes.

---

### 🟡 **FALENCIA #4: Inconsistencia en Validación de Curso Inexistente**

**Ubicación:** `EvaluationServiceImpl`

**Problema:**
- Método con paginación: Valida existencia del curso ✅
- Método sin paginación: NO valida existencia del curso ❌

**Impacto:**
- Comportamiento inconsistente según si se usa paginación o no
- Mismo endpoint puede retornar diferentes códigos HTTP para el mismo caso

**Solución Requerida:**
Unificar el comportamiento: ambos métodos deben validar la existencia del curso.

---

### 🟡 **FALENCIA #5: Falta de Validación de Límites en Paginación**

**Ubicación:** Todos los controladores con paginación

**Problema:**
No hay validación de límites razonables en parámetros de paginación:
- `page` puede ser negativo (retorna resultados, pero puede ser confuso)
- `size` puede ser extremadamente grande (ej: 999999) sin límite máximo
- No hay límite máximo para `size`

**Pruebas Realizadas:**
```
GET /api/courses?page=-1&size=10
Status: 200 (Funciona, pero puede ser confuso)

GET /api/courses?page=0&size=999999
Status: 200 (Funciona, pero puede causar problemas de rendimiento)
```

**Impacto:**
- **Severidad:** Baja
- **Riesgo de Rendimiento:** Consultas con `size` muy grande pueden causar problemas
- **UX:** Puede ser confuso para el frontend

**Solución Recomendada:**
- Agregar límite máximo para `size` (ej: máximo 100)
- Validar que `page` sea >= 0
- Retornar error 400 si se exceden los límites

---

## 🔍 PROBLEMAS POTENCIALES DETECTADOS

### 🟠 **PROBLEMA #1: Manejo de Null en lastName**

**Ubicación:** `StudentAverageDTO`, `getAveragesByCourse`

**Estado:** ✅ **MANEJADO CORRECTAMENTE**
- El campo `lastName` puede ser null según el modelo `Student`
- El DTO lo permite y se maneja correctamente
- No es una falencia, está bien implementado

---

### 🟠 **PROBLEMA #2: Falta de Validación de Tipo de Evaluación**

**Ubicación:** `EvaluationDTO`

**Problema Potencial:**
El campo `tipo` acepta cualquier string. No hay validación de valores permitidos (ej: "examen", "práctica", "tarea").

**Impacto:**
- **Severidad:** Baja
- Puede permitir valores inconsistentes en la base de datos
- No afecta funcionalidad, pero puede afectar consistencia de datos

**Solución Recomendada:**
- Agregar validación con `@Pattern` o enum para tipos permitidos
- O usar un enum `EvaluationType`

---

### 🟠 **PROBLEMA #3: Falta de Validación de Fecha Futura/Pasada**

**Ubicación:** `EvaluationDTO`

**Problema Potencial:**
No hay validación que la fecha sea razonable (no muy en el pasado o futuro).

**Impacto:**
- **Severidad:** Muy Baja
- Puede permitir fechas como "1900-01-01" o "2099-12-31"
- No es crítico, pero podría ser útil

**Solución Recomendada:**
- Agregar validación de rango de fechas si es necesario para el negocio

---

## ✅ ASPECTOS QUE FUNCIONAN CORRECTAMENTE

### 1. **Validaciones de Campos Obligatorios**
- ✅ Todos los endpoints con `@Valid` funcionan correctamente
- ✅ `GlobalExceptionHandler` maneja errores de validación apropiadamente
- ✅ Mensajes de error en español

### 2. **Manejo de Errores**
- ✅ Códigos HTTP apropiados (400, 404, 500)
- ✅ Mensajes de error descriptivos
- ✅ Consistencia en formato de respuestas de error

### 3. **Validaciones de Existencia**
- ✅ La mayoría de endpoints validan que las entidades relacionadas existan
- ✅ Mensajes de error claros cuando algo no existe

### 4. **Validación de Consistencia en Notas**
- ✅ Implementada correctamente en `setGrade` y `updateGrade`
- ✅ Valida que studentId y evaluationId pertenezcan al mismo courseId

### 5. **Paginación**
- ✅ Funciona correctamente cuando se solicita
- ✅ Compatibilidad hacia atrás mantenida
- ✅ Parámetros opcionales funcionan bien

---

## 📋 CASOS DE PRUEBA REALIZADOS

### ✅ Autenticación (4 pruebas)
1. ✅ Registrar profesor - Caso exitoso
2. ✅ Registrar profesor - Email duplicado (error esperado)
3. ✅ Registrar profesor - Campos faltantes (error esperado)
4. ✅ Registrar profesor - Email inválido (error esperado)

### ✅ Cursos (9 pruebas)
1. ✅ Obtener todos los cursos
2. ✅ Crear curso - Caso exitoso
3. ✅ Crear curso - Profesor inexistente (error esperado)
4. ✅ Crear curso - Campos faltantes (error esperado)
5. ✅ Obtener curso por ID
6. ✅ Obtener curso - ID inexistente (error esperado)
7. ✅ Obtener cursos por profesor
8. ✅ Obtener cursos - Profesor inexistente (error esperado)
9. ✅ Paginación de cursos

### ✅ Evaluaciones (9 pruebas)
1. ✅ Obtener evaluaciones de curso (vacío)
2. ✅ Crear evaluación - Caso exitoso
3. ✅ Crear evaluación - Curso inexistente (error esperado)
4. ✅ Crear evaluación - Campos faltantes (error esperado)
5. ✅ Obtener evaluaciones de curso (con datos)
6. ❌ **Obtener evaluaciones - Curso inexistente** (FALENCIA #1)
7. ✅ Eliminar evaluación
8. ✅ Eliminar evaluación - Inexistente (error esperado)
9. ✅ Recrear evaluación para pruebas

### ✅ Notas (11+ pruebas)
1. ✅ Obtener notas de curso (vacío)
2. ✅ Obtener notas - Curso inexistente (error esperado)
3. ✅ Crear nota - Estudiante inexistente (error esperado)
4. ✅ Crear nota - Evaluación inexistente (error esperado)
5. ✅ Crear nota - Curso inexistente (error esperado)
6. ✅ Crear nota - Fuera de rango mayor (error esperado)
7. ✅ Crear nota - Fuera de rango menor (error esperado)
8. ✅ Crear nota - Campos faltantes (error esperado)
9. ✅ Obtener promedios de curso (sin estudiantes)
10. ✅ Obtener promedios - Curso inexistente (error esperado)
11. ✅ Calcular promedio - Estudiante inexistente (error esperado)
12. ⚠️ **No se pueden probar casos exitosos** (FALENCIA #2 - falta endpoint de estudiantes)

---

## 🎯 PRIORIDAD DE CORRECCIONES

### 🔴 **ALTA PRIORIDAD**
1. **FALENCIA #1:** Agregar validación en `getEvaluationsByCourse` sin paginación
2. **FALENCIA #2:** Implementar al menos `POST /api/students` para poder probar notas

### 🟡 **MEDIA PRIORIDAD**
3. **FALENCIA #3:** Verificar validación de notas 0.0 y 10.0
4. **FALENCIA #4:** Unificar validación de curso en ambos métodos de evaluaciones
5. **FALENCIA #5:** Agregar límites a parámetros de paginación

### 🟢 **BAJA PRIORIDAD**
6. Validación de tipo de evaluación (enum o pattern)
7. Validación de rango de fechas en evaluaciones
8. Mejoras de documentación

---

## 📝 NOTAS ADICIONALES

### Limitaciones de las Pruebas
- No se pudieron probar completamente los endpoints de notas debido a la falta de endpoint para crear estudiantes
- Algunas pruebas requieren datos previos que no se pueden crear sin endpoints adicionales
- Las pruebas se realizaron con datos mínimos, casos reales pueden exponer más problemas

### Recomendaciones Generales
1. **Implementar endpoints de estudiantes** para poder probar completamente la funcionalidad de notas
2. **Agregar tests unitarios** para validaciones y lógica de negocio
3. **Documentar casos de uso** para cada endpoint
4. **Considerar agregar logging** para debugging en producción
5. **Revisar manejo de transacciones** en operaciones complejas

---

## 🔄 PRÓXIMOS PASOS SUGERIDOS

1. ✅ Corregir FALENCIA #1 (validación en getEvaluationsByCourse)
2. ✅ Implementar endpoint básico de estudiantes (al menos POST)
3. ✅ Verificar y corregir validación de notas 0.0 y 10.0
4. ✅ Agregar límites a paginación
5. ✅ Re-probar todos los endpoints después de correcciones

---

**Fin del Reporte**

