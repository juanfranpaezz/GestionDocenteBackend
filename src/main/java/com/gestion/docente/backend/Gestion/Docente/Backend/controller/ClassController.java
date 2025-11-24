package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ClassDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    @GetMapping
    public ResponseEntity<List<ClassDTO>> getAllClasses() {
        List<ClassDTO> classes = classService.getAllClasses();
        return ResponseEntity.ok(classes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClassDTO> getClassById(@PathVariable Long id) {
        ClassDTO classDTO = classService.getClassById(id);
        return ResponseEntity.ok(classDTO);
    }
    
    @PostMapping
    public ResponseEntity<ClassDTO> createClass(@RequestBody ClassDTO classDTO) {
        ClassDTO created = classService.createClass(classDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClassDTO> updateClass(
            @PathVariable Long id,
            @RequestBody ClassDTO classDTO) {
        ClassDTO updated = classService.updateClass(id, classDTO);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}

