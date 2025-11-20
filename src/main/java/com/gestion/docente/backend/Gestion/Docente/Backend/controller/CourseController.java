package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CourseDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * GET /api/courses
     * Obtiene todos los cursos del profesor autenticado (obtenido del JWT).
     * Soporta paginación con parámetros opcionales: ?page=0&size=10&sort=name,asc
     */
    @GetMapping
    public ResponseEntity<?> getAllCourses(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            @RequestParam(required = false) Boolean paginated) {
        try {
            // Si se solicita paginación explícitamente o si se proporcionan parámetros de paginación (page, size)
            if (Boolean.TRUE.equals(paginated) || 
                (pageable.getPageNumber() > 0 || pageable.getPageSize() != 20)) {
                Page<CourseDTO> coursesPage = courseService.getAllCourses(pageable);
                return ResponseEntity.ok(coursesPage);
            } else {
                // Retornar lista completa para compatibilidad hacia atrás
                List<CourseDTO> courses = courseService.getAllCourses();
                return ResponseEntity.ok(courses);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener los cursos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /api/courses/{id}
     * Obtiene un curso por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            CourseDTO course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener el curso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/courses
     * Crea un nuevo curso para el profesor autenticado (obtenido del JWT).
     * El professorId NO es necesario en el body, se obtiene automáticamente del token JWT.
     * Valida que todos los campos obligatorios estén presentes.
     */
    @PostMapping
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        try {
            CourseDTO createdCourse = courseService.createCourse(courseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear el curso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * PUT /api/courses/{id}
     * Actualiza un curso existente.
     * El curso debe pertenecer al profesor autenticado (obtenido del JWT).
     * Valida que todos los campos obligatorios estén presentes.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO) {
        try {
            CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
            return ResponseEntity.ok(updatedCourse);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar el curso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * DELETE /api/courses/{id}
     * Elimina un curso existente.
     * El curso debe pertenecer al profesor autenticado (obtenido del JWT).
     * Las relaciones (estudiantes, evaluaciones, notas) se eliminan en cascada.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar el curso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

