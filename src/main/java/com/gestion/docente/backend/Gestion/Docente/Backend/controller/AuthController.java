package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.LoginRequest;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.LoginResponse;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ProfessorDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.RegisterRequest;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private ProfessorService professorService;
    
    /**
     * POST /api/auth/register
     * Registra un nuevo profesor en el sistema.
     * Valida que el email no esté registrado y encripta la contraseña.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            ProfessorDTO createdProfessor = professorService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfessor);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al registrar el profesor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/auth/login
     * Inicia sesión con email y contraseña.
     * Retorna un token JWT y los datos del profesor.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = professorService.login(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al iniciar sesión: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/auth/logout
     * Cierra sesión (endpoint contractual para el frontend).
     * 
     * Requiere autenticación JWT.
     * 
     * Nota: Como usamos JWT stateless, este endpoint no invalida tokens en el backend.
     * El frontend debe eliminar el token del almacenamiento local.
     * Sin embargo, requiere autenticación para que solo usuarios autenticados puedan hacer logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Sesión cerrada exitosamente");
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/auth/me
     * Obtiene los datos del profesor autenticado actualmente.
     * Requiere autenticación JWT.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentProfessor() {
        try {
            ProfessorDTO professor = professorService.getCurrentProfessor();
            return ResponseEntity.ok(professor);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener el profesor actual: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

