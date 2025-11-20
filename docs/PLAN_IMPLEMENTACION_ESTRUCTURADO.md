# 📋 Plan de Implementación Estructurado - Endpoints Necesarios

## 🔍 FASE 1: ANÁLISIS DE DEPENDENCIAS

### 1.1. Análisis de la Cadena de Dependencias

```
Professor (raíz)
    ↓ (professorId)
Course
    ↓ (courseId)
Evaluation ✅ (ya implementado)
```

### 1.2. Dependencias Identificadas

**Evaluation:**
- ✅ Requiere: `courseId` (obligatorio)
- ✅ Validación: El curso debe existir en la BD
- ✅ Implementado en: `EvaluationServiceImpl.addEvaluation()`

**Course:**
- ❌ Requiere: `professorId` (obligatorio)
- ❌ Validación: El profesor debe existir en la BD
- ❌ NO implementado

**Professor:**
- ✅ No tiene dependencias (entidad raíz)
- ❌ NO implementado

### 1.3. Endpoints Mínimos Necesarios

Para poder usar los endpoints de Evaluations, necesitamos:

#### **PRIORIDAD ALTA (Críticos):**
1. ✅ `POST /api/auth/register` - Crear profesor (necesario para crear cursos)
2. ✅ `POST /api/courses` - Crear curso (necesario para crear evaluaciones)
3. ✅ `GET /api/courses` - Listar cursos (para obtener IDs)

#### **PRIORIDAD MEDIA (Recomendados):**
4. ✅ `GET /api/courses/{id}` - Obtener curso por ID (útil para validar)
5. ✅ `GET /api/courses/professor/{professorId}` - Listar cursos de un profesor

#### **PRIORIDAD BAJA (Opcionales por ahora):**
6. ⏸️ `PUT /api/courses/{id}` - Actualizar curso
7. ⏸️ `DELETE /api/courses/{id}` - Eliminar curso

---

## 🎯 FASE 2: PLAN DE IMPLEMENTACIÓN DETALLADO

### 2.1. Orden de Implementación

```
PASO 1: ProfessorService + AuthController
    ↓
PASO 2: CourseService + CourseController
    ↓
PASO 3: Validación y Pruebas
```

---

## 📝 FASE 3: DETALLE DE IMPLEMENTACIÓN

### PASO 1: Implementar ProfessorService y AuthController

#### 3.1.1. Objetivo
Crear la funcionalidad para registrar profesores, que es la base para todo lo demás.

#### 3.1.2. Componentes a Crear/Modificar

**A. ProfessorServiceImpl**
- Ubicación: `service/impl/ProfessorServiceImpl.java`
- Dependencias:
  - `ProfessorRepository` (ya existe)
  - `PasswordEncoder` (para encriptar contraseñas)
  - `EmailService` (opcional por ahora, puede ser mock)

**B. AuthController**
- Ubicación: `controller/AuthController.java` (ya existe, solo comentarios)
- Endpoints a implementar:
  - `POST /api/auth/register`

#### 3.1.3. Lógica de Negocio Detallada

**POST /api/auth/register:**
```
1. Recibir RegisterRequest
2. Validar que el email no exista (usar ProfessorRepository.existsByEmail())
3. Si existe → Error 400 "El email ya está registrado"
4. Si no existe:
   a. Crear nueva entidad Professor
   b. Encriptar contraseña con BCrypt
   c. Guardar en BD (ProfessorRepository.save())
   d. Convertir a ProfessorDTO (sin password)
   e. Retornar 201 Created con ProfessorDTO
```

**Validaciones:**
- Email único (validar con `existsByEmail()`)
- Campos obligatorios: name, lastname, email, password
- Email válido (puede usar `@Email` de validación)

#### 3.1.4. Estructura de Código

```java
@Service
@Transactional
public class ProfessorServiceImpl implements ProfessorService {
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Necesita configuración
    
    @Override
    public ProfessorDTO register(RegisterRequest request) {
        // 1. Validar email único
        // 2. Crear entidad
        // 3. Encriptar password
        // 4. Guardar
        // 5. Convertir a DTO
        // 6. Retornar
    }
}
```

#### 3.1.5. Configuración Necesaria

**SecurityConfig (para PasswordEncoder):**
- Crear clase `SecurityConfig.java`
- Configurar `BCryptPasswordEncoder` como bean
- Deshabilitar seguridad para endpoints públicos (por ahora)

---

### PASO 2: Implementar CourseService y CourseController

#### 3.2.1. Objetivo
Crear la funcionalidad para gestionar cursos, que es necesaria para crear evaluaciones.

#### 3.2.2. Componentes a Crear/Modificar

**A. CourseServiceImpl**
- Ubicación: `service/impl/CourseServiceImpl.java`
- Dependencias:
  - `CourseRepository` (ya existe)
  - `ProfessorRepository` (para validar que el profesor existe)

**B. CourseController**
- Ubicación: `controller/CourseController.java` (ya existe, solo comentarios)
- Endpoints a implementar:
  - `POST /api/courses` (CRÍTICO)
  - `GET /api/courses` (CRÍTICO)
  - `GET /api/courses/{id}` (RECOMENDADO)
  - `GET /api/courses/professor/{professorId}` (RECOMENDADO)

#### 3.2.3. Lógica de Negocio Detallada

**POST /api/courses:**
```
1. Recibir CourseDTO
2. Validar campos obligatorios (name, school, professorId)
3. Validar que el profesor exista (ProfessorRepository.existsById())
4. Si no existe → Error 400 "El profesor con ID X no existe"
5. Si existe:
   a. Crear nueva entidad Course
   b. Asignar professorId
   c. Guardar en BD (CourseRepository.save())
   d. Convertir a CourseDTO
   e. Retornar 201 Created con CourseDTO
```

**GET /api/courses:**
```
1. Por ahora, retornar todos los cursos (sin filtro de profesor)
   (Más adelante se filtrará por profesor autenticado)
2. Obtener todos (CourseRepository.findAll())
3. Convertir lista a List<CourseDTO>
4. Retornar 200 OK
```

**GET /api/courses/{id}:**
```
1. Buscar curso por ID (CourseRepository.findById())
2. Si no existe → Error 404 "Curso no encontrado"
3. Si existe:
   a. Convertir a CourseDTO
   b. Retornar 200 OK
```

**GET /api/courses/professor/{professorId}:**
```
1. Validar que el profesor exista
2. Buscar cursos por profesor (CourseRepository.findByProfessorId())
3. Convertir lista a List<CourseDTO>
4. Retornar 200 OK
```

#### 3.2.4. Estructura de Código

```java
@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        // 1. Validar profesor existe
        // 2. Crear entidad
        // 3. Guardar
        // 4. Convertir a DTO
        // 5. Retornar
    }
    
    @Override
    public List<CourseDTO> getCoursesByProfessor(Long professorId) {
        // 1. Buscar cursos
        // 2. Convertir a DTOs
        // 3. Retornar
    }
    
    // Métodos auxiliares de conversión
    private CourseDTO convertToDTO(Course course) { ... }
    private Course convertToEntity(CourseDTO dto) { ... }
}
```

---

### PASO 3: Validaciones y Manejo de Errores

#### 3.3.1. Validaciones en DTOs

**RegisterRequest:**
- `@NotBlank` para name, lastname, email, password
- `@Email` para email

**CourseDTO:**
- `@NotBlank` para name, school
- `@NotNull` para professorId

#### 3.3.2. Manejo de Errores

- Usar `GlobalExceptionHandler` (ya existe)
- Agregar validaciones de negocio en servicios
- Retornar códigos HTTP apropiados:
  - 400: Validación fallida o regla de negocio
  - 404: Recurso no encontrado
  - 201: Recurso creado
  - 200: Operación exitosa

---

## 🔧 FASE 4: CONFIGURACIÓN TÉCNICA

### 4.1. Dependencias Necesarias

**Ya incluidas:**
- ✅ Spring Boot Starter Web
- ✅ Spring Boot Starter Data JPA
- ✅ Spring Boot Starter Validation
- ✅ H2 Database

**A agregar:**
- ⚠️ Spring Boot Starter Security (ya está, pero deshabilitado)
- ⚠️ BCrypt (viene con Spring Security)

### 4.2. Configuración de Seguridad

**SecurityConfig.java:**
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Por ahora, permitir todo (desarrollo)
        http.csrf().disable()
            .authorizeHttpRequests()
            .anyRequest().permitAll();
        return http.build();
    }
}
```

---

## ✅ FASE 5: CHECKLIST DE IMPLEMENTACIÓN

### Paso 1: ProfessorService
- [ ] Crear `SecurityConfig.java` con PasswordEncoder
- [ ] Crear `ProfessorServiceImpl.java`
- [ ] Implementar método `register()`
- [ ] Agregar validaciones
- [ ] Implementar conversión DTO ↔ Entity
- [ ] Implementar `POST /api/auth/register` en AuthController
- [ ] Agregar manejo de errores
- [ ] Probar endpoint

### Paso 2: CourseService
- [ ] Crear `CourseServiceImpl.java`
- [ ] Implementar método `createCourse()`
- [ ] Implementar método `getCoursesByProfessor()`
- [ ] Implementar método `getCourseById()`
- [ ] Agregar validaciones
- [ ] Implementar conversión DTO ↔ Entity
- [ ] Implementar endpoints en CourseController:
  - [ ] `POST /api/courses`
  - [ ] `GET /api/courses`
  - [ ] `GET /api/courses/{id}`
  - [ ] `GET /api/courses/professor/{professorId}`
- [ ] Agregar manejo de errores
- [ ] Probar endpoints

### Paso 3: Integración y Pruebas
- [ ] Probar flujo completo:
  1. Crear profesor
  2. Crear curso
  3. Crear evaluación
- [ ] Verificar validaciones
- [ ] Verificar manejo de errores
- [ ] Documentar endpoints

---

## 📊 FASE 6: ESTRUCTURA DE ARCHIVOS A CREAR

```
src/main/java/.../backend/
├── config/
│   └── SecurityConfig.java                    [NUEVO]
├── controller/
│   ├── AuthController.java                    [MODIFICAR]
│   └── CourseController.java                  [MODIFICAR]
├── service/
│   └── impl/
│       ├── EvaluationServiceImpl.java         [YA EXISTE]
│       ├── ProfessorServiceImpl.java          [NUEVO]
│       └── CourseServiceImpl.java             [NUEVO]
└── dto/
    ├── RegisterRequest.java                   [YA EXISTE]
    ├── CourseDTO.java                         [YA EXISTE]
    └── ProfessorDTO.java                      [YA EXISTE]
```

---

## 🎯 FASE 7: ORDEN DE EJECUCIÓN

1. **Crear SecurityConfig** (base para encriptación)
2. **Crear ProfessorServiceImpl** (base para todo)
3. **Implementar POST /api/auth/register** (crear profesores)
4. **Crear CourseServiceImpl** (necesario para cursos)
5. **Implementar POST /api/courses** (crear cursos)
6. **Implementar GET /api/courses** (listar cursos)
7. **Implementar GET /api/courses/{id}** (obtener curso)
8. **Implementar GET /api/courses/professor/{professorId}** (listar por profesor)
9. **Probar flujo completo** (Profesor → Curso → Evaluación)

---

## 📝 NOTAS IMPORTANTES

1. **No implementar autenticación JWT todavía** - Solo registro básico
2. **No implementar EmailService todavía** - Puede ser mock o null
3. **Encriptar contraseñas** - Usar BCrypt siempre
4. **Validar dependencias** - Siempre verificar que existan antes de crear
5. **Manejar errores** - Usar GlobalExceptionHandler existente
6. **Conversión DTO ↔ Entity** - Métodos privados en servicios
7. **Transacciones** - Usar `@Transactional` en servicios

---

¿Procedo con la implementación siguiendo este plan paso a paso?

