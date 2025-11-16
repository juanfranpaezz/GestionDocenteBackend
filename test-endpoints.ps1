# Script de Pruebas de Endpoints - Gestin Docente Backend
# Este script prueba todos los endpoints implementados de forma sistemtica

$baseUrl = "http://localhost:8080"
$results = @()

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [int]$ExpectedStatus = 200,
        [string]$Description = ""
    )
    
    Write-Host "`n" -ForegroundColor Cyan
    Write-Host "PRUEBA: $Name" -ForegroundColor Yellow
    Write-Host "Mtodo: $Method | URL: $Url" -ForegroundColor Gray
    if ($Description) { Write-Host "Descripcin: $Description" -ForegroundColor Gray }
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            ErrorAction = "Stop"
            TimeoutSec = 10
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
            $params.ContentType = "application/json"
        }
        
        $response = Invoke-WebRequest @params
        $statusCode = $response.StatusCode
        $content = $response.Content | ConvertFrom-Json
        
        $success = ($statusCode -eq $ExpectedStatus)
        
        if ($success) {
            Write-Host "    Status: $statusCode (Esperado: $ExpectedStatus)" -ForegroundColor Green
            Write-Host "    Respuesta recibida correctamente" -ForegroundColor Green
            if ($content) {
                $contentPreview = ($content | ConvertTo-Json -Depth 2 -Compress).Substring(0, [Math]::Min(100, ($content | ConvertTo-Json -Depth 2 -Compress).Length))
                Write-Host "    Preview: $contentPreview..." -ForegroundColor Gray
            }
        } else {
            Write-Host "     Status: $statusCode (Esperado: $ExpectedStatus)" -ForegroundColor Yellow
        }
        
        $results += [PSCustomObject]@{
            Test = $Name
            Method = $Method
            Url = $Url
            Status = $statusCode
            ExpectedStatus = $ExpectedStatus
            Success = $success
            Response = $content
            Error = $null
        }
        
        return $content
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        $errorMessage = $_.Exception.Message
        
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            try {
                $errorContent = $responseBody | ConvertFrom-Json
                $errorMessage = $errorContent.error
            } catch {
                $errorMessage = $responseBody
            }
        }
        
        $success = ($statusCode -eq $ExpectedStatus)
        
        if ($success) {
            Write-Host "    Status: $statusCode (Esperado para error)" -ForegroundColor Green
            Write-Host "    Error manejado correctamente: $errorMessage" -ForegroundColor Green
        } else {
            Write-Host "    Status: $statusCode (Esperado: $ExpectedStatus)" -ForegroundColor Red
            Write-Host "    Error: $errorMessage" -ForegroundColor Red
        }
        
        $results += [PSCustomObject]@{
            Test = $Name
            Method = $Method
            Url = $Url
            Status = $statusCode
            ExpectedStatus = $ExpectedStatus
            Success = $success
            Response = $null
            Error = $errorMessage
        }
        
        return $null
    }
}

Write-Host "`n" -ForegroundColor Cyan
Write-Host "          PRUEBAS SISTEMTICAS DE ENDPOINTS - GESTIN DOCENTE            " -ForegroundColor Cyan
Write-Host "" -ForegroundColor Cyan

# Variables globales para IDs creados
$global:professorId = $null
$global:courseId = $null
$global:evaluationId = $null
$global:studentId = $null
$global:gradeId = $null

# ============================================================================
# SECCIN 1: AUTENTICACIN
# ============================================================================
Write-Host "`n" -ForegroundColor Magenta
Write-Host "SECCIN 1: AUTENTICACIN" -ForegroundColor Magenta
Write-Host "" -ForegroundColor Magenta

# 1.1 Registrar profesor - Caso exitoso
$professor1 = Test-Endpoint `
    -Name "1.1 Registrar Profesor (Caso Exitoso)" `
    -Method "POST" `
    -Url "$baseUrl/api/auth/register" `
    -Body @{
        name = "Juan"
        lastname = "Prez"
        email = "juan.perez.test@example.com"
        password = "password123"
        cel = "1234567890"
    } `
    -ExpectedStatus 201 `
    -Description "Registrar un nuevo profesor con todos los campos"

if ($professor1) { $global:professorId = $professor1.id }

# 1.2 Registrar profesor - Email duplicado (debe fallar)
Test-Endpoint `
    -Name "1.2 Registrar Profesor (Email Duplicado)" `
    -Method "POST" `
    -Url "$baseUrl/api/auth/register" `
    -Body @{
        name = "Otro"
        lastname = "Usuario"
        email = "juan.perez.test@example.com"
        password = "password123"
    } `
    -ExpectedStatus 400 `
    -Description "Intentar registrar con email ya existente"

# 1.3 Registrar profesor - Campos faltantes (debe fallar)
Test-Endpoint `
    -Name "1.3 Registrar Profesor (Campos Faltantes)" `
    -Method "POST" `
    -Url "$baseUrl/api/auth/register" `
    -Body @{
        name = "Test"
        # Falta lastname, email, password
    } `
    -ExpectedStatus 400 `
    -Description "Intentar registrar sin campos obligatorios"

# 1.4 Registrar profesor - Email invlido (debe fallar)
Test-Endpoint `
    -Name "1.4 Registrar Profesor (Email Invlido)" `
    -Method "POST" `
    -Url "$baseUrl/api/auth/register" `
    -Body @{
        name = "Test"
        lastname = "User"
        email = "email-invalido"
        password = "password123"
    } `
    -ExpectedStatus 400 `
    -Description "Intentar registrar con formato de email invlido"

# ============================================================================
# SECCIN 2: CURSOS
# ============================================================================
Write-Host "`n" -ForegroundColor Magenta
Write-Host "SECCIN 2: CURSOS" -ForegroundColor Magenta
Write-Host "" -ForegroundColor Magenta

# 2.1 Obtener todos los cursos (debe estar vaco inicialmente)
$courses = Test-Endpoint `
    -Name "2.1 Obtener Todos los Cursos" `
    -Method "GET" `
    -Url "$baseUrl/api/courses" `
    -ExpectedStatus 200 `
    -Description "Obtener lista de todos los cursos"

# 2.2 Crear curso - Caso exitoso
if ($global:professorId) {
    $course1 = Test-Endpoint `
        -Name "2.2 Crear Curso (Caso Exitoso)" `
        -Method "POST" `
        -Url "$baseUrl/api/courses" `
        -Body @{
            name = "Matemticas I"
            school = "EES69"
            description = "Curso de matemticas bsicas"
            professorId = $global:professorId
        } `
        -ExpectedStatus 201 `
        -Description "Crear un nuevo curso"
    
    if ($course1) { $global:courseId = $course1.id }
}

# 2.3 Crear curso - Profesor inexistente (debe fallar)
Test-Endpoint `
    -Name "2.3 Crear Curso (Profesor Inexistente)" `
    -Method "POST" `
    -Url "$baseUrl/api/courses" `
    -Body @{
        name = "Curso Test"
        school = "Test"
        professorId = 99999
    } `
    -ExpectedStatus 400 `
    -Description "Intentar crear curso con profesor que no existe"

# 2.4 Crear curso - Campos faltantes (debe fallar)
Test-Endpoint `
    -Name "2.4 Crear Curso (Campos Faltantes)" `
    -Method "POST" `
    -Url "$baseUrl/api/courses" `
    -Body @{
        name = "Curso Test"
        # Falta school y professorId
    } `
    -ExpectedStatus 400 `
    -Description "Intentar crear curso sin campos obligatorios"

# 2.5 Obtener curso por ID
if ($global:courseId) {
    Test-Endpoint `
        -Name "2.5 Obtener Curso por ID" `
        -Method "GET" `
        -Url "$baseUrl/api/courses/$($global:courseId)" `
        -ExpectedStatus 200 `
        -Description "Obtener un curso especfico por su ID"
}

# 2.6 Obtener curso por ID inexistente (debe fallar)
Test-Endpoint `
    -Name "2.6 Obtener Curso (ID Inexistente)" `
    -Method "GET" `
    -Url "$baseUrl/api/courses/99999" `
    -ExpectedStatus 404 `
    -Description "Intentar obtener curso que no existe"

# 2.7 Obtener cursos por profesor
if ($global:professorId) {
    Test-Endpoint `
        -Name "2.7 Obtener Cursos por Profesor" `
        -Method "GET" `
        -Url "$baseUrl/api/courses/professor/$($global:professorId)" `
        -ExpectedStatus 200 `
        -Description "Obtener todos los cursos de un profesor"
}

# 2.8 Obtener cursos por profesor inexistente (debe fallar)
Test-Endpoint `
    -Name "2.8 Obtener Cursos (Profesor Inexistente)" `
    -Method "GET" `
    -Url "$baseUrl/api/courses/professor/99999" `
    -ExpectedStatus 404 `
    -Description "Intentar obtener cursos de profesor que no existe"

# 2.9 Paginacin de cursos
$paginationUrl = $baseUrl + '/api/courses?page=0&size=10&sort=name,asc&paginated=true'
Test-Endpoint `
    -Name "2.9 Obtener Cursos con Paginacin" `
    -Method "GET" `
    -Url $paginationUrl `
    -ExpectedStatus 200 `
    -Description "Obtener cursos con parmetros de paginacin"

# ============================================================================
# SECCIN 3: EVALUACIONES
# ============================================================================
Write-Host "`n" -ForegroundColor Magenta
Write-Host "SECCIN 3: EVALUACIONES" -ForegroundColor Magenta
Write-Host "" -ForegroundColor Magenta

# 3.1 Obtener evaluaciones de un curso (debe estar vaco)
if ($global:courseId) {
    Test-Endpoint `
        -Name "3.1 Obtener Evaluaciones de Curso (Vaco)" `
        -Method "GET" `
        -Url "$baseUrl/api/evaluations/course/$($global:courseId)" `
        -ExpectedStatus 200 `
        -Description "Obtener evaluaciones de un curso sin evaluaciones"
}

# 3.2 Crear evaluacin - Caso exitoso
if ($global:courseId) {
    $evaluation1 = Test-Endpoint `
        -Name "3.2 Crear Evaluacin (Caso Exitoso)" `
        -Method "POST" `
        -Url "$baseUrl/api/evaluations" `
        -Body @{
            nombre = "Examen Parcial"
            date = "2025-11-20"
            tipo = "examen"
            courseId = $global:courseId
        } `
        -ExpectedStatus 201 `
        -Description "Crear una nueva evaluacin"
    
    if ($evaluation1) { $global:evaluationId = $evaluation1.id }
}

# 3.3 Crear evaluacin - Curso inexistente (debe fallar)
Test-Endpoint `
    -Name "3.3 Crear Evaluacin (Curso Inexistente)" `
    -Method "POST" `
    -Url "$baseUrl/api/evaluations" `
    -Body @{
        nombre = "Test"
        date = "2025-11-20"
        tipo = "examen"
        courseId = 99999
    } `
    -ExpectedStatus 400 `
    -Description "Intentar crear evaluacin con curso que no existe"

# 3.4 Crear evaluacin - Campos faltantes (debe fallar)
Test-Endpoint `
    -Name "3.4 Crear Evaluacin (Campos Faltantes)" `
    -Method "POST" `
    -Url "$baseUrl/api/evaluations" `
    -Body @{
        nombre = "Test"
        # Falta date, tipo, courseId
    } `
    -ExpectedStatus 400 `
    -Description "Intentar crear evaluacin sin campos obligatorios"

# 3.5 Obtener evaluaciones de un curso (con datos)
if ($global:courseId) {
    Test-Endpoint `
        -Name "3.5 Obtener Evaluaciones de Curso (Con Datos)" `
        -Method "GET" `
        -Url "$baseUrl/api/evaluations/course/$($global:courseId)" `
        -ExpectedStatus 200 `
        -Description "Obtener evaluaciones de un curso con evaluaciones"
}

# 3.6 Obtener evaluaciones - Curso inexistente (debe fallar)
Test-Endpoint `
    -Name "3.6 Obtener Evaluaciones (Curso Inexistente)" `
    -Method "GET" `
    -Url "$baseUrl/api/evaluations/course/99999" `
    -ExpectedStatus 404 `
    -Description "Intentar obtener evaluaciones de curso que no existe"

# 3.7 Eliminar evaluacin
if ($global:evaluationId) {
    Test-Endpoint `
        -Name "3.7 Eliminar Evaluacin" `
        -Method "DELETE" `
        -Url "$baseUrl/api/evaluations/$($global:evaluationId)" `
        -ExpectedStatus 204 `
        -Description "Eliminar una evaluacin existente"
}

# 3.8 Eliminar evaluacin inexistente (debe fallar)
Test-Endpoint `
    -Name "3.8 Eliminar Evaluacin (Inexistente)" `
    -Method "DELETE" `
    -Url "$baseUrl/api/evaluations/99999" `
    -ExpectedStatus 404 `
    -Description "Intentar eliminar evaluacin que no existe"

# Recrear evaluacin para pruebas de notas
if ($global:courseId) {
    $evaluation2 = Test-Endpoint `
        -Name "3.9 Recrear Evaluacin para Pruebas" `
        -Method "POST" `
        -Url "$baseUrl/api/evaluations" `
        -Body @{
            nombre = "Examen Final"
            date = "2025-12-15"
            tipo = "examen"
            courseId = $global:courseId
        } `
        -ExpectedStatus 201 `
        -Description "Recrear evaluacin para pruebas de notas"
    
    if ($evaluation2) { $global:evaluationId = $evaluation2.id }
}

# ============================================================================
# SECCIN 4: NOTAS
# ============================================================================
Write-Host "`n" -ForegroundColor Magenta
Write-Host "SECCIN 4: NOTAS" -ForegroundColor Magenta
Write-Host "" -ForegroundColor Magenta

# NOTA: Para probar notas necesitamos estudiantes, pero el endpoint de estudiantes
# no est implementado. Vamos a probar los casos de error primero.

# 4.1 Obtener notas de un curso (vaco)
if ($global:courseId) {
    Test-Endpoint `
        -Name "4.1 Obtener Notas de Curso (Vaco)" `
        -Method "GET" `
        -Url "$baseUrl/api/grades/course/$($global:courseId)" `
        -ExpectedStatus 200 `
        -Description "Obtener notas de un curso sin notas"
}

# 4.2 Obtener notas - Curso inexistente (debe fallar)
Test-Endpoint `
    -Name "4.2 Obtener Notas (Curso Inexistente)" `
    -Method "GET" `
    -Url "$baseUrl/api/grades/course/99999" `
    -ExpectedStatus 404 `
    -Description "Intentar obtener notas de curso que no existe"

# 4.3 Crear nota - Estudiante inexistente (debe fallar)
if ($global:courseId -and $global:evaluationId) {
    Test-Endpoint `
        -Name "4.3 Crear Nota (Estudiante Inexistente)" `
        -Method "POST" `
        -Url "$baseUrl/api/grades" `
        -Body @{
            grade = 8.5
            courseId = $global:courseId
            studentId = 99999
            evaluationId = $global:evaluationId
        } `
        -ExpectedStatus 400 `
        -Description "Intentar crear nota con estudiante que no existe"
}

# 4.4 Crear nota - Evaluacin inexistente (debe fallar)
if ($global:courseId) {
    Test-Endpoint `
        -Name "4.4 Crear Nota (Evaluacin Inexistente)" `
        -Method "POST" `
        -Url "$baseUrl/api/grades" `
        -Body @{
            grade = 8.5
            courseId = $global:courseId
            studentId = 1
            evaluationId = 99999
        } `
        -ExpectedStatus 400 `
        -Description "Intentar crear nota con evaluacin que no existe"
}

# 4.5 Crear nota - Curso inexistente (debe fallar)
if ($global:evaluationId) {
    Test-Endpoint `
        -Name "4.5 Crear Nota (Curso Inexistente)" `
        -Method "POST" `
        -Url "$baseUrl/api/grades" `
        -Body @{
            grade = 8.5
            courseId = 99999
            studentId = 1
            evaluationId = $global:evaluationId
        } `
        -ExpectedStatus 400 `
        -Description "Intentar crear nota con curso que no existe"
}

# 4.6 Crear nota - Nota fuera de rango (debe fallar)
if ($global:courseId -and $global:evaluationId) {
    Test-Endpoint `
        -Name "4.6 Crear Nota (Fuera de Rango - Mayor)" `
        -Method "POST" `
        -Url "$baseUrl/api/grades" `
        -Body @{
            grade = 11.0
            courseId = $global:courseId
            studentId = 1
            evaluationId = $global:evaluationId
        } `
        -ExpectedStatus 400 `
        -Description "Intentar crear nota mayor a 10"
    
    Test-Endpoint `
        -Name "4.7 Crear Nota (Fuera de Rango - Menor)" `
        -Method "POST" `
        -Url "$baseUrl/api/grades" `
        -Body @{
            grade = -1.0
            courseId = $global:courseId
            studentId = 1
            evaluationId = $global:evaluationId
        } `
        -ExpectedStatus 400 `
        -Description "Intentar crear nota menor a 0"
}

# 4.8 Crear nota - Campos faltantes (debe fallar)
Test-Endpoint `
    -Name "4.8 Crear Nota (Campos Faltantes)" `
    -Method "POST" `
    -Url "$baseUrl/api/grades" `
    -Body @{
        grade = 8.5
        # Falta courseId, studentId, evaluationId
    } `
    -ExpectedStatus 400 `
    -Description "Intentar crear nota sin campos obligatorios"

# 4.9 Obtener promedios de curso (sin estudiantes)
if ($global:courseId) {
    Test-Endpoint `
        -Name "4.9 Obtener Promedios de Curso (Sin Estudiantes)" `
        -Method "GET" `
        -Url "$baseUrl/api/grades/course/$($global:courseId)/averages" `
        -ExpectedStatus 200 `
        -Description "Obtener promedios cuando no hay estudiantes en el curso"
}

# 4.10 Obtener promedios - Curso inexistente (debe fallar)
Test-Endpoint `
    -Name "4.10 Obtener Promedios (Curso Inexistente)" `
    -Method "GET" `
    -Url "$baseUrl/api/grades/course/99999/averages" `
    -ExpectedStatus 404 `
    -Description "Intentar obtener promedios de curso que no existe"

# 4.11 Calcular promedio individual - Estudiante inexistente (debe fallar)
if ($global:courseId) {
    Test-Endpoint `
        -Name "4.11 Calcular Promedio (Estudiante Inexistente)" `
        -Method "GET" `
        -Url "$baseUrl/api/grades/student/99999/course/$($global:courseId)/average" `
        -ExpectedStatus 404 `
        -Description "Intentar calcular promedio de estudiante que no existe"
}

# ============================================================================
# RESUMEN DE PRUEBAS
# ============================================================================
Write-Host "`n" -ForegroundColor Cyan
Write-Host "                    RESUMEN DE PRUEBAS REALIZADAS                          " -ForegroundColor Cyan
Write-Host "" -ForegroundColor Cyan

$totalTests = $results.Count
$successfulTests = ($results | Where-Object { $_.Success -eq $true }).Count
$failedTests = ($results | Where-Object { $_.Success -eq $false }).Count

Write-Host "`nTotal de Pruebas: $totalTests" -ForegroundColor White
Write-Host " Exitosas: $successfulTests" -ForegroundColor Green
Write-Host " Fallidas: $failedTests" -ForegroundColor Red
Write-Host " Porcentaje de xito: $([math]::Round(($successfulTests / $totalTests) * 100, 2))%" -ForegroundColor Yellow

Write-Host "`n" -ForegroundColor Cyan
Write-Host "PRUEBAS FALLIDAS:" -ForegroundColor Red
Write-Host "" -ForegroundColor Cyan

$failed = $results | Where-Object { $_.Success -eq $false }
if ($failed) {
    $failed | ForEach-Object {
        Write-Host "`n $($_.Test)" -ForegroundColor Red
        Write-Host "   Mtodo: $($_.Method) | URL: $($_.Url)" -ForegroundColor Gray
        Write-Host "   Status: $($_.Status) (Esperado: $($_.ExpectedStatus))" -ForegroundColor Gray
        if ($_.Error) {
            Write-Host "   Error: $($_.Error)" -ForegroundColor Yellow
        }
    }
} else {
    Write-Host "   Todas las pruebas pasaron exitosamente!" -ForegroundColor Green
}

# Exportar resultados a JSON
$results | ConvertTo-Json -Depth 5 | Out-File -FilePath "test-results.json" -Encoding UTF8
Write-Host ""
Write-Host "Resultados exportados a: test-results.json" -ForegroundColor Cyan
Write-Host ""
Write-Host "Pruebas completadas" -ForegroundColor Green

