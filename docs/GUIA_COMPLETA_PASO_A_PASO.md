# 📚 Guía Completa Paso a Paso - Gestión Docente Backend

## 📋 Índice

1. [Cómo Ejecutar el Programa](#cómo-ejecutar-el-programa)
2. [Solución de Error de Lombok](#solución-de-error-de-lombok)
3. [Pruebas con Postman - Paso a Paso](#pruebas-con-postman---paso-a-paso)
4. [Explicación de la Base de Datos H2](#explicación-de-la-base-de-datos-h2)

---

# 🚀 Cómo Ejecutar el Programa

## 🎯 Opción 1: Desde tu IDE (IntelliJ IDEA / VS Code / Eclipse)

### IntelliJ IDEA (Recomendado)

1. **Abre el proyecto** en IntelliJ IDEA
2. **Busca el archivo:** `GestionDocenteBackendApplication.java`
   - Está en: `src/main/java/com/gestion/docente/backend/Gestion/Docente/Backend/GestionDocenteBackendApplication.java`
3. **Haz clic derecho** sobre el archivo
4. **Selecciona:** `Run 'GestionDocenteBackendApplication.main()'`
   - O simplemente presiona `Shift + F10`

**O también puedes:**
- Buscar el archivo en el explorador de proyectos
- Verás un pequeño icono de ▶️ (play) verde a la izquierda del nombre de la clase
- Haz clic en ese icono ▶️
- Selecciona `Run 'GestionDocenteBackendApplication'`

---

### VS Code

1. **Abre el proyecto** en VS Code
2. **Busca el archivo:** `GestionDocenteBackendApplication.java`
3. **Haz clic en el botón "Run"** que aparece arriba del método `main()`
   - O presiona `F5`
   - O usa `Ctrl + F5` para ejecutar sin debug

**Si no ves el botón Run:**
1. Instala la extensión "Extension Pack for Java" de Microsoft
2. Reinicia VS Code
3. Espera a que se descarguen las dependencias (verás notificaciones)

---

### Eclipse

1. **Abre el proyecto** en Eclipse
2. **Busca el archivo:** `GestionDocenteBackendApplication.java`
3. **Haz clic derecho** sobre el archivo
4. **Selecciona:** `Run As` → `Java Application`
   - O presiona `Ctrl + F11`

---

## 🖥️ Opción 2: Desde la Terminal/Consola

### Windows (PowerShell o CMD)

1. **Abre PowerShell o CMD**
   - Presiona `Win + R`, escribe `powershell` o `cmd`, presiona Enter

2. **Navega a la carpeta del proyecto:**
   ```powershell
   cd "C:\Users\pezfr\OneDrive\Escritorio\Gestión Docente Backend"
   ```

3. **Ejecuta el programa:**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

**O si tienes Maven instalado:**
```powershell
mvn spring-boot:run
```

---

### Linux / Mac

1. **Abre la Terminal**

2. **Navega a la carpeta del proyecto:**
   ```bash
   cd "/ruta/a/tu/proyecto/Gestión Docente Backend"
   ```

3. **Ejecuta el programa:**
   ```bash
   ./mvnw spring-boot:run
   ```

**O si tienes Maven instalado:**
```bash
mvn spring-boot:run
```

---

## ✅ Cómo Saber que Está Funcionando

Cuando el programa arranque correctamente, verás en la consola algo como:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.7)

2024-XX-XX XX:XX:XX.XXX  INFO ... : Starting GestionDocenteBackendApplication
2024-XX-XX XX:XX:XX.XXX  INFO ... : Started GestionDocenteBackendApplication in X.XXX seconds
```

**La línea importante es:** `Started GestionDocenteBackendApplication in X.XXX seconds`

---

## 🛑 Cómo Detener el Programa

### Desde el IDE:
- Haz clic en el botón **🛑 (Stop)** en la barra de herramientas
- O presiona `Ctrl + F2` (IntelliJ) / `Ctrl + C` (VS Code)

### Desde la Terminal:
- Presiona `Ctrl + C` en la terminal donde está corriendo

---

## ⚠️ Solución de Problemas - Ejecución

### Error: "No se encuentra mvnw.cmd"
- **Causa:** Estás en la carpeta incorrecta
- **Solución:** Asegúrate de estar en la carpeta raíz del proyecto (donde está el archivo `pom.xml`)

### Error: "Puerto 8080 ya está en uso"
- **Causa:** Ya hay otro programa usando el puerto 8080
- **Solución 1:** Detén el otro programa
- **Solución 2:** Cambia el puerto en `application.properties`:
  ```properties
  server.port=8081
  ```

### Error: "Java no se reconoce como comando"
- **Causa:** Java no está instalado o no está en el PATH
- **Solución:** Instala Java 21 y asegúrate de que esté en el PATH

### El IDE no encuentra la clase main
- **Causa:** El proyecto no está configurado correctamente
- **Solución:** 
  - En IntelliJ: File → Project Structure → Project → SDK: Java 21
  - En VS Code: Instala "Extension Pack for Java"
  - En Eclipse: Project → Properties → Java Build Path → Libraries → Add Library → JRE System Library

---

# 🔧 Solución de Error de Lombok

## 🔍 El Problema

Si ves este error:
```
java: cannot find symbol
  symbol:   method getProfessorId()
  location: variable courseDTO of type CourseDTO
```

**Causa:** IntelliJ IDEA no está procesando las anotaciones de Lombok (`@Data`), por lo que no genera automáticamente los métodos `getProfessorId()` y `setProfessorId()`.

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

# 📮 Pruebas con Postman - Paso a Paso

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

## 🎯 Tips y Trucos de Postman

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

## ⚠️ Errores Comunes en Postman

### Error: "Could not get any response"
- **Causa:** El servidor no está corriendo
- **Solución:** Ejecuta el programa primero (ver sección "Cómo Ejecutar el Programa")

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

# 💾 Explicación de la Base de Datos H2

## 🔍 ¿Por qué no veo configuración de base de datos?

**Respuesta corta:** Estamos usando **H2 en memoria**, que se crea automáticamente cuando arranca la aplicación. No necesitas crear la base de datos manualmente.

---

## 📚 ¿Qué es H2 en Memoria?

H2 es una base de datos **embebida** (viene incluida en la aplicación) que puede funcionar de dos formas:

1. **En memoria (in-memory):** Los datos se guardan en la RAM
2. **En archivo:** Los datos se guardan en un archivo `.db`

Actualmente estamos usando la opción **en memoria**.

---

## ⚙️ Configuración Actual

En `application.properties` tenemos:

```properties
# Configuración de Base de Datos H2 (en memoria - para desarrollo)
spring.datasource.url=jdbc:h2:mem:gestiondocente
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Configuración JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### ¿Qué significa cada línea?

- **`jdbc:h2:mem:gestiondocente`**: 
  - `h2` = Base de datos H2
  - `mem` = **En memoria** (RAM)
  - `gestiondocente` = Nombre de la base de datos
  
- **`spring.jpa.hibernate.ddl-auto=update`**: 
  - Hibernate **crea automáticamente** las tablas cuando arranca la aplicación
  - Si las tablas no existen, las crea
  - Si existen, las actualiza según las entidades
  
- **`spring.jpa.show-sql=true`**: 
  - Muestra en la consola los SQL que se ejecutan (útil para debug)

---

## 🗄️ ¿Los Repositorios meten datos en la base de datos?

**SÍ**, los repositorios **SÍ guardan datos en la base de datos**.

### Cómo funciona:

1. **Cuando arrancas la aplicación:**
   - Hibernate lee las entidades (`@Entity`)
   - Crea automáticamente las tablas en H2
   - No necesitas crear las tablas manualmente

2. **Cuando llamas a un endpoint POST:**
   ```
   POST /api/auth/register
   ↓
   AuthController recibe la petición
   ↓
   ProfessorServiceImpl.register()
   ↓
   professorRepository.save(professor)  ← AQUÍ SE GUARDA EN LA BD
   ↓
   Hibernate ejecuta: INSERT INTO professors (...)
   ↓
   Datos guardados en H2 (en memoria)
   ```

3. **Cuando llamas a un endpoint GET:**
   ```
   GET /api/courses
   ↓
   CourseController recibe la petición
   ↓
   CourseServiceImpl.getAllCourses()
   ↓
   courseRepository.findAll()  ← AQUÍ SE LEEN DE LA BD
   ↓
   Hibernate ejecuta: SELECT * FROM courses
   ↓
   Retorna los datos
   ```

---

## 👀 ¿Cómo ver los datos en la base de datos?

### Opción 1: H2 Console (Recomendado)

1. **Abre tu navegador**
2. **Ve a:** `http://localhost:8080/h2-console`
3. **Ingresa estos datos:**
   - **JDBC URL:** `jdbc:h2:mem:gestiondocente`
   - **Usuario:** `sa`
   - **Contraseña:** (déjala vacía)
4. **Haz clic en "Connect"**

5. **Ejecuta queries SQL:**
   ```sql
   -- Ver todos los profesores
   SELECT * FROM professors;
   
   -- Ver todos los cursos
   SELECT * FROM courses;
   
   -- Ver todas las evaluaciones
   SELECT * FROM evaluations;
   
   -- Ver estructura de una tabla
   DESCRIBE professors;
   ```

### Opción 2: Ver en la Consola

Con `spring.jpa.show-sql=true`, verás en la consola del servidor los SQL que se ejecutan:

```
Hibernate: insert into professors (cel, email, lastname, name, password, photo_url, id) values (?, ?, ?, ?, ?, ?, ?)
Hibernate: insert into courses (description, name, professor_id, school, id) values (?, ?, ?, ?, ?)
```

---

## ⚠️ Importante: Datos en Memoria

### ¿Qué significa "en memoria"?

- Los datos se guardan en la **RAM** (memoria de la computadora)
- **NO se guardan en disco**
- Cuando **detienes el servidor**, **todos los datos se pierden**
- Cada vez que reinicias, la base de datos está vacía

### ¿Por qué usar H2 en memoria?

✅ **Ventajas:**
- No necesitas instalar PostgreSQL, MySQL, etc.
- Perfecto para desarrollo y pruebas
- Muy rápido
- Se crea automáticamente

❌ **Desventajas:**
- Los datos se pierden al reiniciar
- No es para producción

---

## 🔄 Cambiar a Base de Datos Persistente (Opcional)

Si quieres que los datos **persistan** (se guarden en disco), puedes cambiar a H2 en archivo:

### En `application.properties`:

```properties
# Cambiar de "mem" a "file"
spring.datasource.url=jdbc:h2:file:./data/gestiondocente
```

Esto creará un archivo `gestiondocente.mv.db` en la carpeta `data/` del proyecto.

**O usar PostgreSQL (producción):**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestiondocente
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

---

## 📊 Tablas que se Crean Automáticamente

Cuando arrancas la aplicación, Hibernate crea estas tablas:

1. **`professors`** - Profesores
2. **`courses`** - Cursos
3. **`students`** - Estudiantes
4. **`evaluations`** - Evaluaciones
5. **`grades`** - Notas
6. **`attendances`** - Asistencias

**Con sus relaciones y claves foráneas automáticamente.**

---

## ✅ Resumen de Base de Datos

1. ✅ **Los repositorios SÍ guardan en la base de datos**
2. ✅ **H2 se crea automáticamente** cuando arranca la app
3. ✅ **Las tablas se crean automáticamente** por Hibernate
4. ✅ **Puedes ver los datos** en `http://localhost:8080/h2-console`
5. ⚠️ **Los datos se pierden** cuando detienes el servidor (es en memoria)

---

## 🧪 Prueba Rápida de Base de Datos

1. **Arranca el servidor**
2. **Crea un profesor** con Postman (Paso 1 de la guía)
3. **Abre H2 Console:** `http://localhost:8080/h2-console`
4. **Ejecuta:** `SELECT * FROM professors;`
5. **Deberías ver el profesor que creaste** ✅

---

## 📝 Nota sobre Sincronización entre IDEs

**¿Se actualiza en tiempo real en la otra IDE si lo editas ahora?**

**Respuesta:** 
- **Sí**, si ambos IDEs están abiertos y tienen el proyecto abierto, verán los cambios cuando:
  - Guardes el archivo (`Ctrl + S`)
  - El IDE detecte el cambio (automático en la mayoría de casos)
  - Recompiles el proyecto

**Pero:** IntelliJ puede necesitar que hagas `File` → `Reload from Disk` si el archivo fue modificado externamente.

---

## ✅ Checklist Final

Antes de empezar a probar:
- [ ] El servidor está corriendo (deberías ver logs en la consola)
- [ ] Postman está instalado y abierto
- [ ] Lombok está configurado en IntelliJ (si usas IntelliJ)
- [ ] Tienes conexión a internet (para descargar Postman si no lo tienes)

Durante las pruebas:
- [ ] Guardas los IDs que te retornan
- [ ] Usas los IDs correctos en los siguientes pasos
- [ ] Verificas que el Status Code sea el esperado (201 para crear, 200 para obtener)

---

¡Listo! Con esta guía completa deberías poder ejecutar el programa y probar todos los endpoints sin problemas. 🚀

