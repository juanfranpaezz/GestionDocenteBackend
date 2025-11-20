package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    /**
     * GET /api/students/course/{courseId}
     * Obtiene todos los estudiantes de un curso.
     * El curso debe pertenecer al profesor autenticado (obtenido del JWT).
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getStudentsByCourse(@PathVariable Long courseId) {
        try {
            List<StudentDTO> students = studentService.getStudentsByCourse(courseId);
            return ResponseEntity.ok(students);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener los estudiantes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/students
     * Agrega un nuevo estudiante a un curso.
     * El curso debe pertenecer al profesor autenticado (obtenido del JWT).
     * Valida que todos los campos obligatorios estén presentes.
     */
    @PostMapping
    public ResponseEntity<?> addStudentToCourse(@Valid @RequestBody StudentDTO studentDTO) {
        try {
            StudentDTO createdStudent = studentService.addStudentToCourse(studentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al agregar el estudiante: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * PUT /api/students/{id}
     * Actualiza un estudiante existente.
     * El curso del estudiante debe pertenecer al profesor autenticado (obtenido del JWT).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO studentDTO) {
        try {
            StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
            return ResponseEntity.ok(updatedStudent);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar el estudiante: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * DELETE /api/students/{id}
     * Elimina un estudiante.
     * El curso del estudiante debe pertenecer al profesor autenticado (obtenido del JWT).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeStudent(@PathVariable Long id) {
        try {
            studentService.removeStudent(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar el estudiante: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
