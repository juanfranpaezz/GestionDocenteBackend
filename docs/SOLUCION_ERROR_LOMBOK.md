# 🔧 Solución: Error "cannot find symbol getProfessorId()"

## 🔍 El Problema

IntelliJ IDEA no está procesando las anotaciones de Lombok (`@Data`), por lo que no genera automáticamente los métodos `getProfessorId()` y `setProfessorId()`.

## ✅ Solución Rápida

### Paso 1: Habilitar Procesamiento de Anotaciones en IntelliJ

1. **Abre IntelliJ IDEA**
2. **Ve a:** `File` → `Settings` (o `Ctrl + Alt + S`)
3. **Navega a:** `Build, Execution, Deployment` → `Compiler` → `Annotation Processors`
4. **Marca la casilla:** ✅ `Enable annotation processing`
5. **Haz clic en:** `Apply` y luego `OK`

### Paso 2: Instalar Plugin de Lombok (si no lo tienes)

1. **Ve a:** `File` → `Settings` → `Plugins`
2. **Busca:** "Lombok"
3. **Si no está instalado:** Haz clic en `Install`
4. **Reinicia IntelliJ** si te lo pide

### Paso 3: Recompilar el Proyecto

1. **Ve a:** `File` → `Invalidate Caches / Restart...`
2. **Selecciona:** `Invalidate and Restart`
3. **Espera** a que IntelliJ reinicie y reindexe el proyecto

**O también puedes:**
- `Build` → `Rebuild Project` (o `Ctrl + Shift + F9`)

---

## 🔄 Alternativa: Verificar que Lombok Esté Funcionando

Si después de los pasos anteriores sigue sin funcionar:

1. **Abre** `CourseDTO.java`
2. **Coloca el cursor** sobre `@Data`
3. **Presiona** `Alt + Enter`
4. **Si aparece** "Add Lombok plugin" o similar, haz clic en eso

---

## ✅ Verificación

Después de configurar Lombok:

1. **Abre** `CourseDTO.java`
2. **Coloca el cursor** sobre `professorId`
3. **Presiona** `Ctrl + B` (o `Cmd + B` en Mac)
4. **Deberías ver** que IntelliJ reconoce el campo

O simplemente intenta ejecutar el programa de nuevo. Si Lombok está configurado, debería compilar sin problemas.

---

## 🎯 Respuesta a tu Pregunta

**¿Se actualiza en tiempo real en la otra IDE si lo editas ahora?**

**Respuesta:** 
- **Sí**, si ambos IDEs están abiertos y tienen el proyecto abierto, verán los cambios cuando:
  - Guardes el archivo (`Ctrl + S`)
  - El IDE detecte el cambio (automático en la mayoría de casos)
  - Recompiles el proyecto

**Pero:** IntelliJ puede necesitar que hagas `File` → `Reload from Disk` si el archivo fue modificado externamente.

---

## 🚀 Después de Configurar Lombok

Una vez que configures Lombok correctamente:

1. **Recompila el proyecto:** `Build` → `Rebuild Project`
2. **Ejecuta el programa:** Haz clic en el ▶️ verde en `GestionDocenteBackendApplication.java`
3. **Debería funcionar** sin errores

---

Si después de estos pasos sigue dando error, avísame y lo revisamos juntos.

