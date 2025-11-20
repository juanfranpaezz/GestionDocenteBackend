# 📮 Guía Paso a Paso - Postman (Súper Detallada)

## 🎯 Objetivo
Crear un profesor, luego un curso, y finalmente una evaluación usando Postman.

---

## ✅ PASO 1: Crear un Profesor

### 1.1. Abrir Postman
- Abre la aplicación Postman en tu computadora
- Si no la tienes, descárgala de: https://www.postman.com/downloads/

### 1.2. Crear una Nueva Request
1. Haz clic en el botón **"New"** (arriba a la izquierda)
2. Selecciona **"HTTP Request"**
3. O simplemente presiona `Ctrl + N` (Windows) o `Cmd + N` (Mac)

### 1.3. Configurar el Método HTTP
1. En la parte superior izquierda, verás un dropdown que dice **"GET"**
2. Haz clic en ese dropdown
3. Selecciona **"POST"**

### 1.4. Ingresar la URL
1. En el campo de texto que dice **"Enter request URL"**
2. Escribe: `http://localhost:8080/api/auth/register`
3. Presiona `Enter`

### 1.5. Configurar los Headers
1. Haz clic en la pestaña **"Headers"** (debajo de la URL)
2. En la primera fila, en la columna **"Key"**, escribe: `Content-Type`
3. En la misma fila, en la columna **"Value"**, escribe: `application/json`
4. Postman puede autocompletar esto, déjalo que lo haga

### 1.6. Configurar el Body
1. Haz clic en la pestaña **"Body"** (al lado de "Headers")
2. Selecciona la opción **"raw"** (botones de radio)
3. En el dropdown que aparece a la derecha (que probablemente dice "Text"), cámbialo a **"JSON"**

### 1.7. Escribir el JSON
En el área de texto grande, copia y pega esto:

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

### 1.8. Enviar la Petición
1. Haz clic en el botón azul **"Send"** (arriba a la derecha)
2. O presiona `Ctrl + Enter` (Windows) o `Cmd + Enter` (Mac)

### 1.9. Ver la Respuesta
Abajo verás la respuesta. Deberías ver algo como:

**Status:** `201 Created`

**Body (JSON):**
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

⚠️ **IMPORTANTE:** Anota el `"id"` que te retorna (probablemente será `1`). Lo necesitarás para el siguiente paso.

### 1.10. Si hay Error
Si recibes un error `400 Bad Request` con este mensaje:
```json
{
  "error": "El email juan.perez@example.com ya está registrado"
}
```
Significa que ya creaste un profesor con ese email. Cambia el email en el JSON y vuelve a intentar.

---

## ✅ PASO 2: Crear un Curso

### 2.1. Crear Nueva Request
1. Haz clic en **"New"** otra vez
2. Selecciona **"HTTP Request"**

### 2.2. Configurar el Método
1. Cambia el método a **"POST"**

### 2.3. Ingresar la URL
1. Escribe: `http://localhost:8080/api/courses`

### 2.4. Configurar Headers
1. Pestaña **"Headers"**
2. Key: `Content-Type`
3. Value: `application/json`

### 2.5. Configurar Body
1. Pestaña **"Body"**
2. Selecciona **"raw"**
3. Cambia a **"JSON"**

### 2.6. Escribir el JSON
Copia y pega esto (usa el `professorId` que obtuviste en el Paso 1):

```json
{
  "name": "4toC",
  "school": "EES69",
  "description": "Curso de prueba para evaluaciones",
  "professorId": 1
}
```

⚠️ **IMPORTANTE:** Si el `id` del profesor que creaste fue diferente a `1`, cambia el `"professorId": 1` por el ID correcto.

### 2.7. Enviar la Petición
1. Haz clic en **"Send"**

### 2.8. Ver la Respuesta
Deberías ver:

**Status:** `201 Created`

**Body (JSON):**
```json
{
  "id": 1,
  "name": "4toC",
  "school": "EES69",
  "description": "Curso de prueba para evaluaciones",
  "professorId": 1
}
```

⚠️ **IMPORTANTE:** Anota el `"id"` del curso (probablemente será `1`). Lo necesitarás para el siguiente paso.

### 2.9. Si hay Error
Si recibes:
```json
{
  "error": "El profesor con ID 999 no existe"
}
```
Significa que el `professorId` que pusiste no existe. Verifica que sea el ID correcto del profesor que creaste.

---

## ✅ PASO 3: Verificar que el Curso se Creó (Opcional pero Recomendado)

### 3.1. Crear Nueva Request
1. **"New"** → **"HTTP Request"**

### 3.2. Configurar el Método
1. Deja el método en **"GET"** (es el predeterminado)

### 3.3. Ingresar la URL
1. Escribe: `http://localhost:8080/api/courses`

### 3.4. No Necesitas Headers ni Body
- Para GET, no necesitas configurar nada más

### 3.5. Enviar la Petición
1. Haz clic en **"Send"**

### 3.6. Ver la Respuesta
Deberías ver:

**Status:** `200 OK`

**Body (JSON):**
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

Esto confirma que el curso se creó correctamente.

---

## ✅ PASO 4: Crear una Evaluación

### 4.1. Crear Nueva Request
1. **"New"** → **"HTTP Request"**

### 4.2. Configurar el Método
1. Cambia a **"POST"**

### 4.3. Ingresar la URL
1. Escribe: `http://localhost:8080/api/evaluations`

### 4.4. Configurar Headers
1. Pestaña **"Headers"**
2. Key: `Content-Type`
3. Value: `application/json`

### 4.5. Configurar Body
1. Pestaña **"Body"**
2. Selecciona **"raw"**
3. Cambia a **"JSON"**

### 4.6. Escribir el JSON
Copia y pega esto (usa el `courseId` que obtuviste en el Paso 2):

```json
{
  "nombre": "Parcial 1",
  "date": "2024-03-15",
  "tipo": "examen",
  "courseId": 1
}
```

⚠️ **IMPORTANTE:** Si el `id` del curso que creaste fue diferente a `1`, cambia el `"courseId": 1` por el ID correcto.

**Notas sobre los campos:**
- `"nombre"`: Puede ser cualquier nombre (ej: "Parcial 1", "TP 1", "Examen Final")
- `"date"`: Formato `YYYY-MM-DD` (año-mes-día)
- `"tipo"`: Puede ser "examen", "práctica", "tarea", etc.

### 4.7. Enviar la Petición
1. Haz clic en **"Send"**

### 4.8. Ver la Respuesta
Deberías ver:

**Status:** `201 Created`

**Body (JSON):**
```json
{
  "id": 1,
  "nombre": "Parcial 1",
  "date": "2024-03-15",
  "tipo": "examen",
  "courseId": 1
}
```

¡Felicidades! Has creado exitosamente: Profesor → Curso → Evaluación 🎉

### 4.9. Si hay Error
Si recibes:
```json
{
  "error": "El curso con ID 999 no existe"
}
```
Verifica que el `courseId` sea correcto.

Si recibes un error de validación:
```json
{
  "error": "Error de validación",
  "campos": {
    "nombre": "El nombre de la evaluación es obligatorio"
  }
}
```
Revisa que todos los campos obligatorios estén presentes.

---

## ✅ PASO 5: Verificar que la Evaluación se Creó (Opcional)

### 5.1. Crear Nueva Request
1. **"New"** → **"HTTP Request"**

### 5.2. Configurar el Método
1. Deja en **"GET"**

### 5.3. Ingresar la URL
1. Escribe: `http://localhost:8080/api/evaluations/course/1`

⚠️ **IMPORTANTE:** Cambia el `1` por el `courseId` que usaste en el Paso 4.

### 5.4. Enviar la Petición
1. Haz clic en **"Send"**

### 5.5. Ver la Respuesta
Deberías ver:

**Status:** `200 OK`

**Body (JSON):**
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

Esto confirma que la evaluación se creó y está asociada al curso.

---

## 📋 Resumen de URLs y Métodos

| Paso | Método | URL | Descripción |
|------|--------|-----|-------------|
| 1 | POST | `http://localhost:8080/api/auth/register` | Crear profesor |
| 2 | POST | `http://localhost:8080/api/courses` | Crear curso |
| 3 | GET | `http://localhost:8080/api/courses` | Listar cursos |
| 4 | POST | `http://localhost:8080/api/evaluations` | Crear evaluación |
| 5 | GET | `http://localhost:8080/api/evaluations/course/1` | Ver evaluaciones |

---

## 🎯 Tips y Trucos

### Guardar Requests en Postman
1. Haz clic en **"Save"** (arriba a la derecha)
2. Dale un nombre (ej: "Crear Profesor")
3. Puedes crear una carpeta "Gestión Docente" para organizarlas

### Usar Variables en Postman
Puedes crear variables para no tener que cambiar los IDs manualmente:
1. Ve a **"Environments"** (icono de ojo arriba a la derecha)
2. Crea un nuevo environment
3. Agrega variables: `professorId`, `courseId`
4. En las URLs usa: `{{professorId}}`, `{{courseId}}`

### Ver el Código cURL
Si quieres ver el comando cURL equivalente:
1. Haz clic en **"Code"** (debajo de "Send")
2. Selecciona **"cURL"**
3. Copia el comando

---

## ⚠️ Errores Comunes

### Error: "Could not get any response"
- **Causa:** El servidor no está corriendo
- **Solución:** Ejecuta `.\mvnw.cmd spring-boot:run` en la terminal

### Error: "Connection refused"
- **Causa:** El servidor no está en el puerto 8080
- **Solución:** Verifica que el servidor esté corriendo y en el puerto correcto

### Error: "400 Bad Request" con validación
- **Causa:** Faltan campos obligatorios o tienen formato incorrecto
- **Solución:** Revisa el JSON y asegúrate de que todos los campos obligatorios estén presentes

### Error: "404 Not Found"
- **Causa:** La URL está mal escrita
- **Solución:** Verifica que la URL sea exactamente: `http://localhost:8080/api/...`

---

## ✅ Checklist de Verificación

Antes de empezar, verifica:
- [ ] El servidor está corriendo (deberías ver logs en la consola)
- [ ] Postman está instalado y abierto
- [ ] Tienes conexión a internet (para descargar Postman si no lo tienes)

Durante las pruebas:
- [ ] Guardas los IDs que te retornan
- [ ] Usas los IDs correctos en los siguientes pasos
- [ ] Verificas que el Status Code sea el esperado (201 para crear, 200 para obtener)

---

¡Listo! Sigue estos pasos y deberías poder crear todo sin problemas. 🚀

Si tienes algún error, revisa la sección "Errores Comunes" o comparte el mensaje de error que recibes.

