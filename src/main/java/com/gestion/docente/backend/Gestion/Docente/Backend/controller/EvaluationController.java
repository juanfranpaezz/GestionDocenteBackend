package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EvaluationDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EvaluationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    
    @Autowired
    private EvaluationService evaluationService;
    
    /**
     * GET /api/evaluations/course/{courseId}
     * Obtiene todas las evaluaciones de un curso.
     * Soporta paginación con parámetros opcionales: ?page=0&size=10&sort=date,desc
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getEvaluationsByCourse(
            @PathVariable Long courseId,
            @PageableDefault(size = 20, sort = "date", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Boolean paginated) {
        try {
            // Si se solicita paginación explícitamente o si se proporcionan parámetros de paginación (page, size)
            if (Boolean.TRUE.equals(paginated) || 
                (pageable.getPageNumber() > 0 || pageable.getPageSize() != 20)) {
                Page<EvaluationDTO> evaluationsPage = evaluationService.getEvaluationsByCourse(courseId, pageable);
                return ResponseEntity.ok(evaluationsPage);
            } else {
                // Retornar lista completa para compatibilidad hacia atrás
                List<EvaluationDTO> evaluations = evaluationService.getEvaluationsByCourse(courseId);
                return ResponseEntity.ok(evaluations);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener las evaluaciones: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/evaluations
     * Crea una nueva evaluación
     * Valida que todos los campos obligatorios estén presentes
     */
    @PostMapping
    public ResponseEntity<?> createEvaluation(@Valid @RequestBody EvaluationDTO evaluationDTO) {
        try {
            EvaluationDTO createdEvaluation = evaluationService.addEvaluation(evaluationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvaluation);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear la evaluación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * DELETE /api/evaluations/{id}
     * Elimina una evaluación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvaluation(@PathVariable Long id) {
        try {
            evaluationService.deleteEvaluation(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar la evaluación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/evaluations/{id}/send-grades
     * Envía las notas de una evaluación por email a todos los alumnos
     */
    @PostMapping("/{id}/send-grades")
    public ResponseEntity<?> sendGradesByEmail(@PathVariable Long id) {
        try {
            evaluationService.sendGradesByEmail(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notas enviadas por email exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al enviar las notas por email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

