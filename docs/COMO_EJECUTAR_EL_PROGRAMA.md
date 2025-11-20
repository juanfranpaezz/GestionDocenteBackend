# 🚀 Cómo Ejecutar el Programa - Guía Rápida

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

## ⚠️ Solución de Problemas

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

## 🎯 Método Más Rápido (Recomendado)

**Si usas IntelliJ IDEA:**
1. Abre el proyecto
2. Busca `GestionDocenteBackendApplication.java`
3. Haz clic en el icono ▶️ verde a la izquierda
4. ¡Listo!

**Si usas VS Code:**
1. Abre el proyecto
2. Busca `GestionDocenteBackendApplication.java`
3. Presiona `F5`
4. ¡Listo!

**Si prefieres Terminal:**
1. Abre PowerShell en la carpeta del proyecto
2. Escribe: `.\mvnw.cmd spring-boot:run`
3. Presiona Enter
4. ¡Listo!

---

## 📝 Nota Importante

El programa seguirá corriendo hasta que lo detengas. Mientras esté corriendo, puedes hacer peticiones con Postman.

Para verificar que está corriendo, abre tu navegador y ve a:
- `http://localhost:8080` (debería dar 404, pero significa que el servidor está activo)
- `http://localhost:8080/h2-console` (consola de la base de datos)

---

¡Eso es todo! Elige el método que prefieras y ejecuta el programa. 🚀

