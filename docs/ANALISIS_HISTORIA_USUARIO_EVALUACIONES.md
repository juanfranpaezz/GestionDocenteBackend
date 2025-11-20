# 📋 Análisis de Historia de Usuario - Crear Evaluaciones

**Historia de Usuario:** Permitir que el profesor cree evaluaciones asociadas a un curso

---

## 🎯 Objetivo
Permitir que el profesor cree evaluaciones asociadas a un curso.

**Rol:** Profesor  
**Necesidad:** Registrar trabajos prácticos, exámenes u otras instancias evaluativas.  
**Beneficio:** Organizar y gestionar el proceso de calificación de los alumnos.

---

## ✅ Criterios de Aceptación

### Criterio 1: Crear evaluación con campos requeridos
**Dado que** el profesor se encuentra autenticado  
**Cuando** accede a la sección de evaluaciones de un curso  
**Entonces** el sistema debe permitir crear una nueva evaluación ingresando nombre, fecha y tipo (por ejemplo: examen, práctica, tarea).

**Estado Backend:** ✅ **COMPLETO**
- ✅ Endpoint `POST /api/evaluations` implementado
- ✅ Campos: `nombre`, `date`, `tipo` disponibles
- ⚠️ **Nota:** Autenticación aún no implementada (JWT pendiente), pero el endpoint funciona sin autenticación para desarrollo

**Estado Frontend:** ❌ **PENDIENTE**
- ❌ Componente de formulario no implementado
- ❌ Método `createEvaluation()` en servicio Angular no implementado

---

### Criterio 2: Guardar y mostrar en lista
**Dado que** el profesor completa los datos requeridos  
**Cuando** confirma la creación  
**Entonces** la evaluación debe guardarse y quedar disponible en la lista de evaluaciones del curso.

**Estado Backend:** ✅ **COMPLETO**
- ✅ La evaluación se guarda en la base de datos
- ✅ Endpoint `GET /api/evaluations/course/{courseId}` implementado para listar evaluaciones
- ✅ La evaluación queda disponible inmediatamente después de crearse

**Estado Frontend:** ❌ **PENDIENTE**
- ❌ Actualización de lista con signals no implementada
- ❌ Conexión del formulario con el servicio no implementada

---

### Criterio 3: Validación de campos obligatorios
**Dado que** falta completar algún campo obligatorio  
**Cuando** se intenta crear la evaluación  
**Entonces** el sistema debe mostrar un mensaje indicando los campos faltantes.

**Estado Backend:** ✅ **COMPLETO**
- ✅ Validaciones con `@NotBlank` y `@NotNull` implementadas
- ✅ Mensajes de error personalizados en español
- ✅ Respuesta estructurada con errores por campo:
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

**Estado Frontend:** ❌ **PENDIENTE**
- ❌ Manejo de errores de validación en el formulario no implementado
- ❌ Mostrar mensajes de error en la UI no implementado

---

## 📦 Alcance de la Historia de Usuario

### ✅ Incluye:
- ✅ Creación y registro de evaluaciones pertenecientes a un curso

### ❌ No incluye:
- ❌ Carga de notas (cubierto en HU-5.2)

---

## 🔍 Checklist de Implementación

### Backend

#### ✅ **1. Crear entidad/tabla Evaluación en la base de datos**
- ✅ Entidad `Evaluation` creada con campos:
  - ✅ `id` (Long, auto-generado)
  - ✅ `idCurso` → `courseId` (Long, nullable = false)
  - ✅ `nombre` (String, nullable = false)
  - ✅ `fecha` → `date` (LocalDate, nullable = false)
  - ✅ `tipo` (String, nullable = false) ← **Agregado según requisito**
- ✅ Tabla `evaluations` creada automáticamente por JPA
- ✅ Relación `@ManyToOne` con `Course` configurada

**Estado:** ✅ **COMPLETO**

---

#### ✅ **2. Crear endpoint en backend para crear evaluaciones**
- ✅ Endpoint `POST /api/evaluations` implementado
- ✅ Ubicación: `EvaluationController.createEvaluation()`
- ✅ Retorna `201 Created` con la evaluación creada
- ✅ Maneja errores con códigos HTTP apropiados

**Estado:** ✅ **COMPLETO**

---

#### ✅ **3. Validar campos obligatorios en el backend**
- ✅ Validaciones implementadas en `EvaluationDTO`:
  - ✅ `nombre`: `@NotBlank(message = "El nombre de la evaluación es obligatorio")`
  - ✅ `date`: `@NotNull(message = "La fecha es obligatoria")`
  - ✅ `tipo`: `@NotBlank(message = "El tipo de evaluación es obligatorio")`
  - ✅ `courseId`: `@NotNull(message = "El ID del curso es obligatorio")`
- ✅ Uso de `@Valid` en el controller
- ✅ `GlobalExceptionHandler` maneja errores de validación
- ✅ Respuestas estructuradas con errores por campo

**Estado:** ✅ **COMPLETO**

---

### Frontend (NO implementado - fuera del alcance del backend)

#### ❌ **4. Crear método en servicio Angular evaluation.service.ts**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

**Código sugerido:**
```typescript
createEvaluation(evaluation: EvaluationDTO): Observable<EvaluationDTO> {
  return this.http.post<EvaluationDTO>(
    `${this.apiUrl}/evaluations`,
    evaluation
  );
}
```

---

#### ❌ **5. Crear componente standalone para formulario de creación de evaluación**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

**Requisitos:**
- Componente standalone con Reactive Forms
- Campos: `nombre`, `date`, `tipo`, `courseId` (hidden)

---

#### ❌ **6. Conectar formulario con servicio y endpoint**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

---

#### ❌ **7. Actualizar la lista de evaluaciones después de crear una nueva (signals)**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

---

#### ❌ **8. Mostrar mensajes de éxito y error en la UI**
**Estado:** ❌ **PENDIENTE** (debe hacerlo el frontend)

---

## 📊 Resumen del Estado

### Backend: ✅ **100% COMPLETO**

| Requisito | Estado | Notas |
|-----------|--------|-------|
| Entidad/tabla Evaluación | ✅ Completo | Todos los campos requeridos presentes |
| Endpoint POST /api/evaluations | ✅ Completo | Funcional y probado |
| Validación de campos obligatorios | ✅ Completo | Con mensajes en español |
| Validación de curso existente | ✅ Completo | Validación de negocio implementada |
| Manejo de errores | ✅ Completo | Respuestas estructuradas |
| Endpoint GET para listar | ✅ Completo | `GET /api/evaluations/course/{courseId}` |

**Total Backend:** 6/6 completados (100%)

---

### Frontend: ❌ **0% COMPLETO**

| Requisito | Estado | Notas |
|-----------|--------|-------|
| Método createEvaluation() en servicio | ❌ Pendiente | Debe implementarse en Angular |
| Componente standalone de formulario | ❌ Pendiente | Debe crearse con Reactive Forms |
| Conexión formulario-servicio | ❌ Pendiente | Debe conectarse al endpoint |
| Actualización de lista (signals) | ❌ Pendiente | Debe implementarse en Angular |
| Mensajes de éxito/error en UI | ❌ Pendiente | Debe implementarse en Angular |

**Total Frontend:** 0/5 completados (0%)

---

## 🎯 Conclusión

### ✅ **Del Backend: TODO ESTÁ COMPLETO**

Todos los requisitos del backend de la historia de usuario están **100% implementados y funcionando**:

1. ✅ Entidad con todos los campos requeridos (incluyendo `tipo`)
2. ✅ Endpoint `POST /api/evaluations` funcional
3. ✅ Validaciones de campos obligatorios con mensajes en español
4. ✅ Validación de que el curso existe
5. ✅ Manejo de errores estructurado
6. ✅ Endpoint para listar evaluaciones del curso

**El backend está listo para ser consumido por el frontend.**

---

### ❌ **Del Frontend: TODO ESTÁ PENDIENTE**

El frontend debe implementar:

1. ❌ Método `createEvaluation()` en `evaluation.service.ts`
2. ❌ Componente standalone con formulario Reactivo
3. ❌ Conexión del formulario con el servicio
4. ❌ Actualización de lista con signals
5. ❌ Mensajes de éxito y error en la UI

**El frontend necesita implementar estos componentes para completar la historia de usuario.**

---

## 📝 Nota sobre Autenticación

**Observación:** La historia de usuario menciona "Dado que el profesor se encuentra autenticado", pero actualmente:

- ✅ El backend tiene el endpoint funcionando **sin autenticación** (para desarrollo)
- ⏸️ JWT aún no está implementado
- ✅ Cuando se implemente JWT, solo habrá que agregar `@PreAuthorize` o similar al endpoint

**Esto NO bloquea la funcionalidad**, ya que el endpoint funciona correctamente. La autenticación se puede agregar después sin afectar la funcionalidad actual.

---

## ✅ Resumen Final

**Backend:** ✅ **COMPLETO** - Listo para usar  
**Frontend:** ❌ **PENDIENTE** - Debe implementarse

**La historia de usuario del backend está 100% completa. Solo falta implementar el frontend.**





