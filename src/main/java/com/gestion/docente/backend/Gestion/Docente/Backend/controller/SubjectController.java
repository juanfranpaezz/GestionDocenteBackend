package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.SubjectDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubjectController {
    
    @Autowired
    private SubjectService subjectService;
    
    /**
     * GET /api/courses/{courseId}/subjects
     * Obtiene todas las materias de un curso.
     */
    @GetMapping("/courses/{courseId}/subjects")
    public ResponseEntity<?> getSubjectsByCourse(@PathVariable Long courseId) {
        try {
            List<SubjectDTO> subjects = subjectService.getSubjectsByCourse(courseId);
            return ResponseEntity.ok(subjects);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener las materias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /api/courses/{courseId}/subjects/default
     * Obtiene la materia default (sin nombre o primera materia) de un curso.
     */
    @GetMapping("/courses/{courseId}/subjects/default")
    public ResponseEntity<?> getDefaultSubject(@PathVariable Long courseId) {
        try {
            SubjectDTO defaultSubject = subjectService.getDefaultSubject(courseId);
            return ResponseEntity.ok(defaultSubject);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener la materia default: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/courses/{courseId}/subjects
     * Crea una nueva materia para un curso.
     */
    @PostMapping("/courses/{courseId}/subjects")
    public ResponseEntity<?> createSubject(
            @PathVariable Long courseId,
            @Valid @RequestBody SubjectDTO subjectDTO) {
        try {
            // Asignar el courseId del path al DTO
            subjectDTO.setCourseId(courseId);
            
            SubjectDTO createdSubject = subjectService.createSubject(subjectDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear la materia: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * PUT /api/subjects/{id}
     * Actualiza una materia existente.
     */
    @PutMapping("/subjects/{id}")
    public ResponseEntity<?> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectDTO subjectDTO) {
        try {
            SubjectDTO updatedSubject = subjectService.updateSubject(id, subjectDTO);
            return ResponseEntity.ok(updatedSubject);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar la materia: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * DELETE /api/subjects/{id}
     * Elimina una materia.
     */
    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteSubject(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Materia eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar la materia: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

