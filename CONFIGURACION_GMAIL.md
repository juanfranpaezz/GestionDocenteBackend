# Configuración de Gmail para Verificación de Email

Este documento explica cómo configurar Gmail para enviar emails de verificación de cuenta.

## Requisitos Previos

1. Una cuenta de Gmail
2. Verificación en dos pasos activada en tu cuenta de Google

## Pasos para Configurar

### 1. Activar Verificación en Dos Pasos

1. Ve a [https://myaccount.google.com/security](https://myaccount.google.com/security)
2. En la sección "Acceso a Google", busca "Verificación en dos pasos"
3. Actívala si aún no está activada

### 2. Generar una Contraseña de Aplicación

1. Una vez activada la verificación en dos pasos, ve a [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
2. Selecciona "Aplicación" y elige "Correo"
3. Selecciona "Dispositivo" y elige "Otro (nombre personalizado)"
4. Escribe "Gestión Docente Backend" o cualquier nombre que prefieras
5. Haz clic en "Generar"
6. **IMPORTANTE**: Copia la contraseña de 16 caracteres que se genera (se muestra solo una vez)

### 3. Configurar Variables de Entorno

Tienes dos opciones para configurar las credenciales:

#### Opción A: Variables de Entorno del Sistema

En Windows (PowerShell):
```powershell
$env:GMAIL_USERNAME="tu-email@gmail.com"
$env:GMAIL_APP_PASSWORD="xxxx xxxx xxxx xxxx"  # La contraseña de aplicación generada
$env:APP_BASE_URL="http://localhost:4200"      # URL base del frontend
```

En Linux/Mac:
```bash
export GMAIL_USERNAME="tu-email@gmail.com"
export GMAIL_APP_PASSWORD="xxxx xxxx xxxx xxxx"
export APP_BASE_URL="http://localhost:4200"
```

#### Opción B: Editar application.properties

Edita el archivo `src/main/resources/application.properties` y reemplaza los valores vacíos:

```properties
spring.mail.username=tu-email@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx
app.base-url=http://localhost:4200
```

**⚠️ ADVERTENCIA**: No subas este archivo con credenciales reales a un repositorio público. Usa variables de entorno en producción.

### 4. Verificar la Configuración

1. Inicia el backend
2. Intenta registrar un nuevo usuario
3. Revisa la bandeja de entrada del email registrado
4. Deberías recibir un email con un enlace de verificación

## Solución de Problemas

### Error: "Authentication failed"

- Verifica que la verificación en dos pasos esté activada
- Asegúrate de usar la **Contraseña de aplicación** (16 caracteres), NO tu contraseña normal de Gmail
- Verifica que no haya espacios extra en la contraseña

### Error: "Connection timeout"

- Verifica tu conexión a internet
- Asegúrate de que el puerto 587 no esté bloqueado por un firewall
- Verifica que `spring.mail.host=smtp.gmail.com` esté correcto

### No se reciben emails

- Revisa la carpeta de spam
- Verifica que el email de destino sea válido
- Revisa los logs del backend para ver si hay errores al enviar el email

## Notas Importantes

- La contraseña de aplicación es diferente de tu contraseña de Gmail
- Cada contraseña de aplicación es única y solo se muestra una vez
- Si pierdes la contraseña, genera una nueva
- En producción, usa variables de entorno o un servicio de gestión de secretos (como AWS Secrets Manager, Azure Key Vault, etc.)

## Producción

Para producción, considera:

1. Usar un servicio de email profesional (SendGrid, Mailgun, AWS SES, etc.)
2. Configurar variables de entorno en tu servidor
3. Usar un servicio de gestión de secretos
4. Configurar SPF, DKIM y DMARC para mejorar la deliverabilidad
5. Usar un dominio personalizado para los emails

