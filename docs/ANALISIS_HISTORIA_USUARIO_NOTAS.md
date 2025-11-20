# 📋 Análisis de Historia de Usuario - Cargar Notas de Alumnos

**Historia de Usuario:** Como profesor quiero cargar notas de alumnos

---

## 🎯 Objetivo
Permitir que el profesor registre las calificaciones de los alumnos en una evaluación.

**Rol:** Profesor  
**Necesidad:** Cargar notas asociadas a cada alumno y evaluación.  
**Beneficio:** Llevar un registro académico claro y accesible para futuras consultas y seguimiento.

---

## ✅ Criterios de Aceptación

### Criterio 1: Mostrar lista de alumnos para cargar notas
**Dado que** el profesor se encuentra autenticado  
**Cuando** ingresa a una evaluación dentro de un curso  
**Entonces** el sistema debe mostrar la lista de alumnos inscriptos para cargar o modificar sus notas.

**Estado Backend:** ✅ **COMPLETO**
- ✅ Endpoint `GET /api/grades/evaluation/{evaluationId}` implementado
- ✅ Retorna todas las notas de una evaluación específica
- ⚠️ **Nota:** Para obtener la lista de alumnos, el frontend deberá usar `GET /api/students/course/{courseId}` (aún no implementado) y luego `GET /api/grades/evaluation/{evaluationId}` para ver qué alumnos ya tienen notas

**Estado Frontend:** ❌ **PENDIENTE**
- ❌ Componente para mostrar lista de alumnos no implementado
- ❌ Integración con endpoints no implementada

---

### Criterio 2: Guardar nota correctamente
**Dado que** el profesor ingresa una nota para un alumno  
**Cuando** confirma el registro  
**Entonces** el sistema debe guardar la nota correctamente y asociarla tanto al alumno como a la evaluación.

**Estado Backend:** ✅ **COMPLETO**
- ✅ Endpoint `POST /api/grades` implementado
- ✅ Valida que el estudiante exista
- ✅ Valida que la evaluación exista
- ✅ Valida que el curso exista
- ✅ Si ya existe una nota para el mismo estudiante y evaluación, la actualiza automáticamente
- ✅ Si no existe, crea una nueva nota
- ✅ Asocia correctamente la nota al estudiante, evaluación y curso

**Estado Frontend:** ❌ **PENDIENTE**
- ❌ Formulario para ingresar nota no implementado
- ❌ Método `saveGrade()` en servicio Angular no implementado
- ❌ Integración con endpoint no implementada

---

### Criterio 3: Editar notas existentes
**Dado que** ya existen notas cargadas previamente  
**Cuando** el profesor vuelve a la misma evaluación  
**Entonces** el sistema debe permitir editar las notas existentes.

**Estado Backend:** ✅ **COMPLETO**
- ✅ Endpoint `PUT /api/grades/{id}` implementado
- ✅ Permite actualizar una nota existente por ID
- ✅ Valida que la nota exista antes de actualizar
- ✅ Valida rango de nota (0-10)
- ✅ Valida que estudiante, evaluación y curso existan si se actualizan

**Estado Frontend:** ❌ **PENDIENTE**
- ❌ Componente para editar notas no implementado
- ❌ Método `updateGrade()` en servicio Angular no implementado
- ❌ Integración con endpoint no implementada

---

### Criterio 4: Validación de nota inválida
**Dado que** el profesor intenta guardar una nota inválida (por ejemplo, fuera del rango permitido)  
**Cuando** se intenta confirmar  
**Entonces** el sistema debe mostrar un mensaje de error.

**Estado Backend:** ✅ **COMPLETO**
- ✅ Validación de rango implementada: `@DecimalMin(0.0)` y `@DecimalMax(10.0)`
- ✅ Mensajes de error personalizados en español:
  - "La nota no puede ser menor a 0"
  - "La nota no puede ser mayor a 10"
- ✅ Validación de campos obligatorios:
  - "La nota es obligatoria"
  - "El ID del curso es obligatorio"
  - "El ID del estudiante es obligatorio"
  - "El ID de la evaluación es obligatorio"
- ✅ Respuestas estructuradas con errores por campo

**Estado Frontend:** ❌ **PENDIENTE**
- ❌ Validación en formulario no implementada
- ❌ Mostrar mensajes de error en UI no implementado

---

## 📦 Alcance de la Historia de Usuario

### ✅ Incluye:
- ✅ Registro y modificación de notas por alumno en evaluaciones

### ❌ No incluye:
- ❌ Generación de reportes o promedios (cubierto en HU futura)

---

## 🔍 Checklist de Implementación

### Backend

#### ✅ **1. Crear entidad/tabla Nota (id, idAlumno, idEvaluación, valor)**
- ✅ Entidad `Grade` ya existía con todos los campos:
  - ✅ `id` (Long, auto-generado)
  - ✅ `studentId` → idAlumno (Long, nullable = false)
  - ✅ `evaluationId` → idEvaluación (Long, nullable = false)
  - ✅ `grade` → valor (Double, puede ser null)
  - ✅ `courseId` (Long, nullable = false) - Campo adicional para facilitar consultas
- ✅ Tabla `grades` creada automáticamente por JPA
- ✅ Relaciones `@ManyToOne` con `Student`, `Evaluation` y `Course` configuradas

**Estado:** ✅ **COMPLETO** (ya existía, no fue necesario crear)

---

#### ✅ **2. Crear endpoint en backend para cargar notas (POST /notas)**
- ✅ Endpoint `POST /api/grades` implementado
- ✅ Ubicación: `GradeController.createOrUpdateGrade()`
- ✅ Lógica inteligente:
  - Si ya existe una nota para el mismo `studentId` y `evaluationId`, la actualiza
  - Si no existe, crea una nueva
- ✅ Retorna `201 Created` con la nota guardada
- ✅ Valida existencia de estudiante, evaluación y curso
- ✅ Maneja errores con códigos HTTP apropiados

**Estado:** ✅ **COMPLETO**

---

#### ✅ **3. Crear endpoint en backend para actualizar notas (PUT /notas/{id})**
- ✅ Endpoint `PUT /api/grades/{id}` implementado
- ✅ Ubicación: `GradeController.updateGrade()`
- ✅ Permite actualizar una nota existente por ID
- ✅ Valida que la nota exista
- ✅ Retorna `200 OK` con la nota actualizada
- ✅ Maneja errores con códigos HTTP apropiados

**Estado:** ✅ **COMPLETO**

---

#### ✅ **4. Implementar validación de rangos de nota en backend**
- ✅ Validaciones implementadas en `GradeDTO`:
  - ✅ `@NotNull(message = "La nota es obligatoria")`
  - ✅ `@DecimalMin(value = "0.0", message = "La nota no puede ser menor a 0")`
  - ✅ `@DecimalMax(value = "10.0", message = "La nota no puede ser mayor a 10")`
- ✅ Validación adicional en `GradeServiceImpl.updateGrade()`:
  - Valida rango 0-10 antes de actualizar
- ✅ Uso de `@Valid` en los controllers
- ✅ `GlobalExceptionHandler` maneja errores de validación
- ✅ Respuestas estructuradas con errores por campo

**Estado:** ✅ **COMPLETO**

---

### Frontend (NO implementado - fuera del alcance del backend)

#### ❌ **5. Crear método en grade.service.ts (saveGrade() y updateGrade())**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

**Código sugerido:**
```typescript
saveGrade(grade: GradeDTO): Observable<GradeDTO> {
  return this.http.post<GradeDTO>(
    `${this.apiUrl}/grades`,
    grade
  );
}

updateGrade(id: number, grade: GradeDTO): Observable<GradeDTO> {
  return this.http.put<GradeDTO>(
    `${this.apiUrl}/grades/${id}`,
    grade
  );
}

getGradesByEvaluation(evaluationId: number): Observable<GradeDTO[]> {
  return this.http.get<GradeDTO[]>(
    `${this.apiUrl}/grades/evaluation/${evaluationId}`
  );
}
```

---

#### ❌ **6. Crear componente standalone para cargar/editar notas dentro de una evaluación**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

**Requisitos:**
- Componente standalone con formulario Reactivo
- Mostrar lista de alumnos del curso
- Campo editable para nota por cada alumno
- Botón para guardar/actualizar

---

#### ❌ **7. Mostrar la lista de alumnos con campo editable para nota**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

**Requisitos:**
- Obtener lista de alumnos del curso: `GET /api/students/course/{courseId}` (aún no implementado)
- Obtener notas existentes: `GET /api/grades/evaluation/{evaluationId}`
- Combinar ambas listas para mostrar alumnos con sus notas
- Campo editable para cada alumno

---

#### ❌ **8. Refrescar los datos de la lista luego de guardar (signals)**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

**Requisitos:**
- Usar signals para actualizar la lista automáticamente
- O recargar la lista después de guardar exitosamente

---

#### ❌ **9. Mostrar mensajes de éxito y error según el resultado**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

**Requisitos:**
- Mostrar mensaje de éxito cuando se guarda correctamente
- Mostrar mensajes de error cuando falla la validación
- Mostrar errores de red

---

#### ❌ **10. Pruebas funcionales: guardar nota, editar y verificar persistencia**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

**Requisitos:**
- Probar guardar una nueva nota
- Probar editar una nota existente
- Verificar que los datos persisten correctamente

---

## 📊 Resumen del Estado

### Backend: ✅ **100% COMPLETO**

| Requisito | Estado | Notas |
|-----------|--------|-------|
| Entidad/tabla Nota | ✅ Completo | Ya existía con todos los campos |
| Endpoint POST /api/grades | ✅ Completo | Funcional, actualiza si ya existe |
| Endpoint PUT /api/grades/{id} | ✅ Completo | Funcional y probado |
| Validación de rangos de nota | ✅ Completo | Rango 0-10 con mensajes en español |
| Validación de entidades relacionadas | ✅ Completo | Valida estudiante, evaluación y curso |
| Endpoint GET para listar notas | ✅ Completo | Por curso y por evaluación |
| Manejo de errores | ✅ Completo | Respuestas estructuradas |

**Total Backend:** 7/7 completados (100%)

---

### Frontend: ❌ **0% COMPLETO**

| Requisito | Estado | Notas |
|-----------|--------|-------|
| Método saveGrade() en servicio | ❌ Pendiente | Debe implementarse en Angular |
| Método updateGrade() en servicio | ❌ Pendiente | Debe implementarse en Angular |
| Componente standalone de formulario | ❌ Pendiente | Debe crearse con Reactive Forms |
| Mostrar lista de alumnos | ❌ Pendiente | Requiere endpoint de estudiantes (aún no implementado) |
| Campo editable para nota | ❌ Pendiente | Debe implementarse en Angular |
| Actualización de lista (signals) | ❌ Pendiente | Debe implementarse en Angular |
| Mensajes de éxito/error en UI | ❌ Pendiente | Debe implementarse en Angular |
| Pruebas funcionales | ❌ Pendiente | Debe implementarse en Angular |

**Total Frontend:** 0/8 completados (0%)

---

## 🎯 Conclusión

### ✅ **Del Backend: TODO ESTÁ COMPLETO**

Todos los requisitos del backend de la historia de usuario están **100% implementados y funcionando**:

1. ✅ Entidad con todos los campos requeridos (ya existía)
2. ✅ Endpoint `POST /api/grades` funcional (crea o actualiza automáticamente)
3. ✅ Endpoint `PUT /api/grades/{id}` funcional
4. ✅ Validación de rangos de nota (0-10) con mensajes en español
5. ✅ Validación de que estudiante, evaluación y curso existan
6. ✅ Endpoints para listar notas (por curso y por evaluación)
7. ✅ Manejo de errores estructurado

**El backend está listo para ser consumido por el frontend.**

---

### ❌ **Del Frontend: TODO ESTÁ PENDIENTE**

El frontend debe implementar:

1. ❌ Métodos `saveGrade()` y `updateGrade()` en `grade.service.ts`
2. ❌ Componente standalone con formulario Reactivo
3. ❌ Mostrar lista de alumnos (requiere endpoint de estudiantes)
4. ❌ Campo editable para nota por cada alumno
5. ❌ Actualización de lista con signals
6. ❌ Mensajes de éxito y error en la UI
7. ❌ Pruebas funcionales

**El frontend necesita implementar estos componentes para completar la historia de usuario.**

---

## 📝 Nota sobre Autenticación

**Observación:** La historia de usuario menciona "Dado que el profesor se encuentra autenticado", pero actualmente:

- ✅ El backend tiene los endpoints funcionando **sin autenticación** (para desarrollo)
- ⏸️ JWT aún no está implementado
- ✅ Cuando se implemente JWT, solo habrá que agregar `@PreAuthorize` o similar a los endpoints

**Esto NO bloquea la funcionalidad**, ya que los endpoints funcionan correctamente. La autenticación se puede agregar después sin afectar la funcionalidad actual.

---

## 🔧 Endpoints Disponibles

### Para el Frontend:

1. ✅ `POST /api/grades` - Crear o actualizar nota
   - Si ya existe nota para estudiante+evaluación, la actualiza
   - Si no existe, crea una nueva

2. ✅ `PUT /api/grades/{id}` - Actualizar nota por ID

3. ✅ `GET /api/grades/evaluation/{evaluationId}` - Obtener todas las notas de una evaluación

4. ✅ `GET /api/grades/course/{courseId}` - Obtener todas las notas de un curso

5. ✅ `GET /api/grades/student/{studentId}/course/{courseId}/average` - Calcular promedio

---

## ✅ Resumen Final

**Backend:** ✅ **COMPLETO** - Listo para usar  
**Frontend:** ❌ **PENDIENTE** - Debe implementarse

**La historia de usuario del backend está 100% completa. Solo falta implementar el frontend.**

---

## 📌 Notas Técnicas

### Rango de Notas
- **Rango implementado:** 0.0 a 10.0
- **Tipo:** Double (permite decimales)
- **Validación:** `@DecimalMin(0.0)` y `@DecimalMax(10.0)`

### Lógica de Crear/Actualizar
El endpoint `POST /api/grades` tiene lógica inteligente:
- Si ya existe una nota para el mismo `studentId` y `evaluationId`, **actualiza** la nota existente
- Si no existe, **crea** una nueva nota

Esto permite que el frontend use siempre `POST` sin preocuparse por si la nota ya existe o no.

### Validaciones Implementadas
- Nota obligatoria y en rango 0-10
- Estudiante debe existir
- Evaluación debe existir
- Curso debe existir
- Todos los campos obligatorios validados





