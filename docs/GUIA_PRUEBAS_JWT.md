# 🔐 Guía de Pruebas - Autenticación JWT

Esta guía te ayudará a probar la implementación de JWT paso a paso.

---

## 📋 Prerequisitos

1. Asegúrate de que el proyecto compile correctamente:
   ```bash
   mvn clean compile
   ```

2. Inicia el servidor:
   ```bash
   mvn spring-boot:run
   ```

3. El servidor debería estar corriendo en `http://localhost:8080`

---

## 🧪 Pruebas Paso a Paso

### 1. Registro de Profesor

**Endpoint:** `POST http://localhost:8080/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "password": "password123",
  "cel": "123456789",
  "photoUrl": "https://example.com/photo.jpg"
}
```

**Respuesta esperada (201 Created):**
```json
{
  "id": 1,
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "cel": "123456789",
  "photoUrl": "https://example.com/photo.jpg"
}
```

**Nota:** El campo `password` NO debe aparecer en la respuesta.

---

### 2. Login con Email + Contraseña

**Endpoint:** `POST http://localhost:8080/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "email": "juan.perez@example.com",
  "password": "password123"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJwcm9mZXNzb3JJZCI6MSwiZW1haWwiOiJqdWFuLnBlcmV6QGV4YW1wbGUuY29tIiwicm9sZSI6IlBST0ZFU1NPUiIsInN1YmplY3QiOiJqdWFuLnBlcmV6QGV4YW1wbGUuY29tIiwiaWF0IjoxNzM0NTY3ODkwLCJleHAiOjE3MzQ1NzE0OTB9.xxxxx",
  "expiresIn": 3600,
  "professor": {
    "id": 1,
    "name": "Juan",
    "lastname": "Pérez",
    "email": "juan.perez@example.com",
    "cel": "123456789",
    "photoUrl": "https://example.com/photo.jpg"
  }
}
```

**✅ Verificaciones:**
- El token debe ser una cadena larga (JWT)
- `expiresIn` debe ser `3600` (1 hora en segundos)
- Los datos del profesor deben coincidir con los del registro

**⚠️ Prueba con credenciales incorrectas:**
```json
{
  "email": "juan.perez@example.com",
  "password": "passwordIncorrecta"
}
```

**Respuesta esperada (401 Unauthorized):**
```json
{
  "error": "Credenciales inválidas"
}
```

---

### 3. Acceso a Endpoints Protegidos con Token

**Endpoint:** `GET http://localhost:8080/api/auth/me`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Ejemplo:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJwcm9mZXNzb3JJZCI6MSwiZW1haWwiOiJqdWFuLnBlcmV6QGV4YW1wbGUuY29tIiwicm9sZSI6IlBST0ZFU1NPUiIsInN1YmplY3QiOiJqdWFuLnBlcmV6QGV4YW1wbGUuY29tIiwiaWF0IjoxNzM0NTY3ODkwLCJleHAiOjE3MzQ1NzE0OTB9.xxxxx
```

**Respuesta esperada (200 OK):**
```json
{
  "id": 1,
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan.perez@example.com",
  "cel": "123456789",
  "photoUrl": "https://example.com/photo.jpg"
}
```

**Otros endpoints protegidos para probar:**
- `GET http://localhost:8080/api/courses` - Lista de cursos
- `GET http://localhost:8080/api/grades/course/1` - Notas de un curso
- `POST http://localhost:8080/api/courses` - Crear curso (requiere body)

---

### 4. Comportamiento sin Token

**Endpoint:** `GET http://localhost:8080/api/auth/me`

**Headers:**
```
Content-Type: application/json
```
(Sin header Authorization)

**Respuesta esperada (401 Unauthorized):**
```json
{
  "error": "No autorizado",
  "message": "Debe iniciar sesión para acceder a este recurso",
  "status": 401
}
```

---

### 5. Comportamiento con Token Inválido

**Endpoint:** `GET http://localhost:8080/api/auth/me`

**Headers:**
```
Authorization: Bearer token_invalido_o_alterado
Content-Type: application/json
```

**Respuesta esperada (401 Unauthorized):**
```json
{
  "error": "No autorizado",
  "message": "Debe iniciar sesión para acceder a este recurso",
  "status": 401
}
```

**Pruebas adicionales:**
- Token expirado (esperar 1 hora o modificar el token manualmente)
- Token con formato incorrecto (sin "Bearer " al inicio)
- Token vacío

---

### 6. Logout (Endpoint Contractual)

**Endpoint:** `POST http://localhost:8080/api/auth/logout`

**Headers:**
```
Content-Type: application/json
```

**Respuesta esperada (200 OK):**
```json
{
  "message": "Sesión cerrada exitosamente"
}
```

**Nota:** Este endpoint es público y no requiere autenticación. Es solo contractual para el frontend.

---

## 🛠️ Usando Postman

### Configuración de Variables

1. Crea una variable de entorno en Postman:
   - Variable: `baseUrl`
   - Valor: `http://localhost:8080`

2. Crea una variable para el token:
   - Variable: `jwtToken`
   - Valor: (se llenará automáticamente después del login)

### Flujo de Pruebas en Postman

1. **Registro:**
   - Método: `POST`
   - URL: `{{baseUrl}}/api/auth/register`
   - Body: JSON con datos del profesor

2. **Login:**
   - Método: `POST`
   - URL: `{{baseUrl}}/api/auth/login`
   - Body: JSON con email y password
   - En "Tests" tab, agrega:
     ```javascript
     if (pm.response.code === 200) {
         var jsonData = pm.response.json();
         pm.environment.set("jwtToken", jsonData.token);
     }
     ```

3. **Endpoints Protegidos:**
   - Método: `GET`
   - URL: `{{baseUrl}}/api/auth/me`
   - Headers: 
     - Key: `Authorization`
     - Value: `Bearer {{jwtToken}}`

---

## 🧪 Usando cURL

### 1. Registro
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan",
    "lastname": "Pérez",
    "email": "juan.perez@example.com",
    "password": "password123",
    "cel": "123456789"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.perez@example.com",
    "password": "password123"
  }'
```

Guarda el token de la respuesta.

### 3. Endpoint Protegido
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <TU_TOKEN_AQUI>" \
  -H "Content-Type: application/json"
```

### 4. Sin Token (debe fallar)
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Content-Type: application/json"
```

---

## ✅ Checklist de Pruebas

- [ ] Registro de profesor funciona
- [ ] Login con credenciales correctas retorna token
- [ ] Login con credenciales incorrectas retorna 401
- [ ] Endpoint `/api/auth/me` funciona con token válido
- [ ] Endpoint `/api/auth/me` retorna 401 sin token
- [ ] Endpoint `/api/auth/me` retorna 401 con token inválido
- [ ] Otros endpoints protegidos requieren token
- [ ] Endpoints públicos (`/register`, `/login`, `/logout`) no requieren token
- [ ] El token expira después de 1 hora (opcional, requiere esperar)

---

## 🐛 Troubleshooting

### Error: "Missing mandatory Classpath entries"
- **Solución:** Ejecuta `mvn clean install` para descargar las dependencias

### Error: "JWT_SECRET not found"
- **Solución:** El valor por defecto en `application.properties` debería funcionar. Si no, configura la variable de entorno `JWT_SECRET`

### Error: "No autorizado" incluso con token válido
- **Verifica:**
  - El header `Authorization` tiene el formato: `Bearer <token>` (con espacio)
  - El token no ha expirado
  - El profesor existe en la base de datos

### Token funciona pero el profesor no se encuentra
- **Verifica:** Que el `professorId` en el token coincida con un profesor existente en la BD

---

## 📝 Notas Importantes

1. **Expiración del Token:** Los tokens expiran después de 1 hora (3600 segundos)
2. **Stateless:** No hay sesiones en el servidor. Cada request debe incluir el token
3. **Seguridad:** En producción, configura `JWT_SECRET` como variable de entorno
4. **HTTPS:** En producción, siempre usa HTTPS para proteger los tokens en tránsito

---

¡Listo para probar! 🚀

