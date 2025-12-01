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
    
    @PostMapping("/{id}/archive")
    public ResponseEntity<?> archiveCourse(@PathVariable Long id) {
        try {
            CourseDTO archived = courseService.archiveCourse(id);
            return ResponseEntity.ok(archived);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{id}/unarchive")
    public ResponseEntity<?> unarchiveCourse(@PathVariable Long id) {
        try {
            CourseDTO unarchived = courseService.unarchiveCourse(id);
            return ResponseEntity.ok(unarchived);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/archived")
    public ResponseEntity<?> getArchivedCourses() {
        try {
            List<CourseDTO> courses = courseService.getArchivedCourses();
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener cursos archivados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<?> duplicateCourse(
            @PathVariable Long id,
            @RequestBody com.gestion.docente.backend.Gestion.Docente.Backend.dto.DuplicateCourseDTO options) {
        try {
            CourseDTO duplicated = courseService.duplicateCourse(id, options);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Curso duplicado exitosamente");
            response.put("newCourseId", duplicated.getId());
            response.put("newCourseName", duplicated.getName());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping(params = {"search"})
    public ResponseEntity<?> searchCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean archived) {
        try {
            List<CourseDTO> courses = courseService.searchCourses(search, archived);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al buscar cursos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/courses/{id}/send-personalized-message
     * Envía un mensaje personalizado a todos los estudiantes del curso.
     * El curso debe pertenecer al profesor autenticado (obtenido del JWT).
     */
    @PostMapping("/{id}/send-personalized-message")
    public ResponseEntity<?> sendPersonalizedMessage(
            @PathVariable Long id,
            @Valid @RequestBody com.gestion.docente.backend.Gestion.Docente.Backend.dto.SendPersonalizedMessageDTO messageDTO) {
        try {
            courseService.sendPersonalizedMessageToAllStudents(id, messageDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Mensajes enviados exitosamente a todos los estudiantes del curso");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al enviar los mensajes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

