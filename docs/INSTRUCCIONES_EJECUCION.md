# 🚀 Instrucciones para Ejecutar el Proyecto

## ✅ Configuración Realizada

He configurado el proyecto para que pueda ejecutarse. Los cambios realizados fueron:

1. **Base de Datos H2 (en memoria)**: Configurada para desarrollo, no requiere instalación de PostgreSQL
2. **Spring Security**: Deshabilitado temporalmente para desarrollo
3. **Controladores**: Los servicios están comentados temporalmente (porque son solo interfaces sin implementaciones)

## 📋 Requisitos Previos

- **Java 21** instalado
- **Maven** instalado (o usar el wrapper `mvnw` incluido)
- Un IDE (IntelliJ IDEA, Eclipse, VS Code) o terminal

## 🏃 Cómo Ejecutar

### Opción 1: Desde el IDE (Recomendado)

1. Abre el proyecto en tu IDE (IntelliJ IDEA, Eclipse, etc.)
2. Busca la clase `GestionDocenteBackendApplication.java`
3. Haz clic derecho → **Run** o presiona `Shift + F10` (IntelliJ) / `Ctrl + F11` (Eclipse)

### Opción 2: Desde la Terminal

**Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### Opción 3: Compilar y Ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar
java -jar target/Gestion-Docente-Backend-0.0.1-SNAPSHOT.jar
```

## 🌐 Verificar que Funciona

Una vez que el servidor arranque, deberías ver algo como:

```
Started GestionDocenteBackendApplication in X.XXX seconds
```

### Endpoints Disponibles

Aunque los controladores están vacíos, puedes verificar que el servidor responde:

- **Servidor**: http://localhost:8080
- **H2 Console** (Base de datos): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:gestiondocente`
  - Usuario: `sa`
  - Contraseña: (vacía)

### Probar con Postman o cURL

Puedes probar que los endpoints existen (aunque no hagan nada todavía):

```bash
# Probar que el servidor responde
curl http://localhost:8080/api/courses

# Debería retornar algo (aunque sea vacío o error 404)
```

## 📊 Ver la Base de Datos

1. Abre tu navegador
2. Ve a: http://localhost:8080/h2-console
3. Ingresa:
   - **JDBC URL**: `jdbc:h2:mem:gestiondocente`
   - **Usuario**: `sa`
   - **Contraseña**: (déjala vacía)
4. Haz clic en **Connect**
5. Puedes ejecutar queries SQL como:
   ```sql
   SELECT * FROM professors;
   SELECT * FROM courses;
   ```

## ⚠️ Estado Actual del Proyecto

### ✅ Lo que está listo:
- ✅ Estructura MVC completa
- ✅ Entidades (Models) con relaciones JPA
- ✅ DTOs
- ✅ Repositorios (interfaces)
- ✅ Servicios (interfaces)
- ✅ Controladores (estructura básica)
- ✅ Base de datos configurada
- ✅ El servidor puede arrancar sin errores

### ⚠️ Lo que falta implementar:
- ⚠️ **Implementaciones de los servicios** (actualmente son solo interfaces)
- ⚠️ **Endpoints en los controladores** (actualmente están vacíos)
- ⚠️ **Autenticación JWT** (Spring Security está deshabilitado)
- ⚠️ **Validaciones** de datos
- ⚠️ **Manejo de errores** personalizado

## 🔧 Próximos Pasos

Para que el proyecto funcione completamente, necesitas:

1. **Implementar los servicios**: Crear clases que implementen las interfaces de servicio
2. **Implementar los endpoints**: Agregar los métodos en los controladores
3. **Configurar Spring Security**: Habilitar autenticación JWT
4. **Agregar validaciones**: Usar `@Valid` y `@NotNull` en los DTOs

## 🐛 Solución de Problemas

### Error: "Port 8080 already in use"
- Cambia el puerto en `application.properties`:
  ```properties
  server.port=8081
  ```

### Error: "Cannot find symbol" o errores de compilación
- Ejecuta: `mvn clean install` para descargar dependencias

### Error: "No bean found" para los servicios
- Es normal, los servicios están comentados. Descoméntalos cuando implementes las clases de servicio.

### La base de datos está vacía
- Es normal, H2 en memoria se reinicia cada vez que arrancas el servidor
- Para datos persistentes, cambia a PostgreSQL o usa H2 en archivo

## 📝 Notas Importantes

- **H2 en memoria**: Los datos se pierden cuando detienes el servidor
- **Spring Security deshabilitado**: Todos los endpoints son accesibles sin autenticación (solo para desarrollo)
- **Servicios comentados**: Los controladores no tienen lógica todavía

## 🎯 Ejemplo de Próxima Implementación

Cuando implementes un servicio, por ejemplo `CourseServiceImpl`:

```java
@Service
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public List<CourseDTO> getCoursesByProfessor(Long professorId) {
        // Implementación aquí
    }
}
```

Luego descomenta el `@Autowired` en el controlador correspondiente.

---

¡Listo! El proyecto debería ejecutarse sin problemas. 🎉

