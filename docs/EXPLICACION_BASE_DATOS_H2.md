# 💾 Explicación: Base de Datos H2 en Memoria

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

## ✅ Resumen

1. ✅ **Los repositorios SÍ guardan en la base de datos**
2. ✅ **H2 se crea automáticamente** cuando arranca la app
3. ✅ **Las tablas se crean automáticamente** por Hibernate
4. ✅ **Puedes ver los datos** en `http://localhost:8080/h2-console`
5. ⚠️ **Los datos se pierden** cuando detienes el servidor (es en memoria)

---

## 🧪 Prueba Rápida

1. **Arranca el servidor**
2. **Crea un profesor** con Postman (Paso 1 de la guía)
3. **Abre H2 Console:** `http://localhost:8080/h2-console`
4. **Ejecuta:** `SELECT * FROM professors;`
5. **Deberías ver el profesor que creaste** ✅

¡Así de simple! 🚀

