package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeScaleDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.GradeScaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grade-scales")
@CrossOrigin(origins = "http://localhost:4200")
public class GradeScaleController {
    
    @Autowired
    private GradeScaleService gradeScaleService;
    
    @GetMapping
    public ResponseEntity<List<GradeScaleDTO>> getGradeScales(@RequestParam(required = false) Boolean global) {
        try {
            List<GradeScaleDTO> scales = gradeScaleService.getGradeScales(global);
            return ResponseEntity.ok(scales);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createGradeScale(@Valid @RequestBody GradeScaleDTO gradeScaleDTO) {
        try {
            GradeScaleDTO created = gradeScaleService.createGradeScale(gradeScaleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGradeScale(
            @PathVariable Long id,
            @Valid @RequestBody GradeScaleDTO gradeScaleDTO) {
        try {
            GradeScaleDTO updated = gradeScaleService.updateGradeScale(id, gradeScaleDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGradeScale(@PathVariable Long id) {
        try {
            gradeScaleService.deleteGradeScale(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/evaluations/{evaluationId}/available")
    public ResponseEntity<List<GradeScaleDTO>> getAvailableGradeScales(@PathVariable Long evaluationId) {
        try {
            List<GradeScaleDTO> scales = gradeScaleService.getAvailableGradeScalesForEvaluation(evaluationId);
            return ResponseEntity.ok(scales);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

