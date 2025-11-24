package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ProfessorDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.ProfessorService;
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
    public ResponseEntity<?> searchProfessorsByLastname(
            @RequestParam String lastname) {
        try {
            List<ProfessorDTO> professors = professorService.searchProfessorsByLastname(lastname);
            return ResponseEntity.ok(professors);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
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

