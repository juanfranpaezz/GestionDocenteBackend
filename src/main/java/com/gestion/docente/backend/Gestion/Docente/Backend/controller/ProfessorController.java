package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CreateProfessorByAdminRequest;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ProfessorDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {
    
    @Autowired
    private ProfessorService professorService;
    
    /**
     * POST /api/professors
     * Crea un nuevo administrador (solo admins).
     * No envía email de verificación, el usuario queda verificado automáticamente.
     * 
     * IMPORTANTE: Solo se pueden crear ADMINS desde este endpoint por seguridad.
     * Los profesores deben auto-registrarse en /api/auth/register con verificación de email.
     */
    @PostMapping
    public ResponseEntity<?> createProfessor(@Valid @RequestBody CreateProfessorByAdminRequest request) {
        try {
            ProfessorDTO createdProfessor = professorService.createByAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfessor);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear el profesor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ProfessorDTO>> getAllProfessors() {
        List<ProfessorDTO> professors = professorService.getAllProfessors();
        return ResponseEntity.ok(professors);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorDTO> getProfessorById(@PathVariable Long id) {
        ProfessorDTO professor = professorService.getProfessorById(id);
        return ResponseEntity.ok(professor);
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchProfessors(
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String query) {
        try {
            List<ProfessorDTO> professors;
            // Si se proporciona 'query', usar búsqueda general (nombre, apellido, email)
            if (query != null && !query.trim().isEmpty()) {
                professors = professorService.searchProfessors(query.trim());
            } else if (lastname != null && !lastname.trim().isEmpty()) {
                // Mantener compatibilidad con búsqueda por apellido
                professors = professorService.searchProfessorsByLastname(lastname.trim());
            } else {
                // Si no hay parámetros, retornar todos los profesores
                professors = professorService.getAllProfessors();
            }
            return ResponseEntity.ok(professors);
        } catch (IllegalStateException e) {
            // Error de autorización (no es admin o no está autenticado)
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            // Si el mensaje indica que no hay usuario autenticado, retornar 401
            if (e.getMessage() != null && e.getMessage().contains("No hay un usuario autenticado")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al buscar profesores: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorDTO> updateProfessor(
            @PathVariable Long id,
            @RequestBody ProfessorDTO professorDTO) {
        ProfessorDTO updated = professorService.updateProfessor(id, professorDTO);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfessor(@PathVariable Long id) {
        try {
            professorService.deleteProfessor(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar profesor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        boolean exists = professorService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}

