package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EvaluationTypeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EvaluationTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-types")
@CrossOrigin(origins = "http://localhost:4200")
public class EvaluationTypeController {
    
    @Autowired
    private EvaluationTypeService evaluationTypeService;
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EvaluationTypeDTO>> getEvaluationTypesByCourse(@PathVariable Long courseId) {
        try {
            List<EvaluationTypeDTO> types = evaluationTypeService.getEvaluationTypesByCourse(courseId);
            return ResponseEntity.ok(types);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createEvaluationType(@Valid @RequestBody EvaluationTypeDTO evaluationTypeDTO) {
        try {
            EvaluationTypeDTO created = evaluationTypeService.createEvaluationType(evaluationTypeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvaluationType(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationTypeDTO evaluationTypeDTO) {
        try {
            EvaluationTypeDTO updated = evaluationTypeService.updateEvaluationType(id, evaluationTypeDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvaluationType(@PathVariable Long id) {
        try {
            evaluationTypeService.deleteEvaluationType(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // Clase interna para respuestas de error
    private static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
}

