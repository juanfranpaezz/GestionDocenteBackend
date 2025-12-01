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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentAverageDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentGroupedAveragesDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.GradeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grades")
public class GradeController {
    
    @Autowired
    private GradeService gradeService;
    
    /**
     * GET /api/grades/course/{courseId}
     * Obtiene todas las notas de un curso.
     * Soporta paginación con parámetros opcionales: ?page=0&size=10&sort=id,asc
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getGradesByCourse(
            @PathVariable Long courseId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) Boolean paginated) {
        try {
            // Si se solicita paginación explícitamente o si se proporcionan parámetros de paginación (page, size)
            if (Boolean.TRUE.equals(paginated) || 
                (pageable.getPageNumber() > 0 || pageable.getPageSize() != 20)) {
                Page<GradeDTO> gradesPage = gradeService.getGradesByCourse(courseId, pageable);
                return ResponseEntity.ok(gradesPage);
            } else {
                // Retornar lista completa para compatibilidad hacia atrás
                List<GradeDTO> grades = gradeService.getGradesByCourse(courseId);
                return ResponseEntity.ok(grades);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener las notas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /api/grades/evaluation/{evaluationId}
     * Obtiene todas las notas de una evaluación específica.
     * Soporta paginación con parámetros opcionales: ?page=0&size=10&sort=id,asc
     */
    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<?> getGradesByEvaluation(
            @PathVariable Long evaluationId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) Boolean paginated) {
        try {
            // Si se solicita paginación explícitamente o si se proporcionan parámetros de paginación (page, size)
            if (Boolean.TRUE.equals(paginated) || 
                (pageable.getPageNumber() > 0 || pageable.getPageSize() != 20)) {
                Page<GradeDTO> gradesPage = gradeService.getGradesByEvaluation(evaluationId, pageable);
                return ResponseEntity.ok(gradesPage);
            } else {
                // Retornar lista completa para compatibilidad hacia atrás
                List<GradeDTO> grades = gradeService.getGradesByEvaluation(evaluationId);
                return ResponseEntity.ok(grades);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener las notas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/grades
     * Crea o actualiza una nota.
     * Si ya existe una nota para el mismo estudiante y evaluación, la actualiza.
     * Si no existe, crea una nueva.
     */
    @PostMapping
    public ResponseEntity<?> createOrUpdateGrade(@Valid @RequestBody GradeDTO gradeDTO) {
        try {
            GradeDTO savedGrade = gradeService.setGrade(gradeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGrade);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al guardar la nota: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * PUT /api/grades/{id}
     * Actualiza una nota existente por ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable Long id, @Valid @RequestBody GradeDTO gradeDTO) {
        try {
            GradeDTO updatedGrade = gradeService.updateGrade(id, gradeDTO);
            return ResponseEntity.ok(updatedGrade);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar la nota: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /api/grades/course/{courseId}/averages
     * Obtiene los promedios de todos los estudiantes de un curso.
     * Retorna una lista con información de cada estudiante y su promedio calculado.
     * Si un estudiante no tiene notas, el promedio será null y hasGrades será false.
     */
    @GetMapping("/course/{courseId}/averages")
    public ResponseEntity<?> getAveragesByCourse(@PathVariable Long courseId) {
        try {
            List<StudentAverageDTO> averages = gradeService.getAveragesByCourse(courseId);
            return ResponseEntity.ok(averages);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener los promedios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /api/grades/student/{studentId}/course/{courseId}/average
     * Calcula el promedio de notas de un estudiante en un curso.
     */
    @GetMapping("/student/{studentId}/course/{courseId}/average")
    public ResponseEntity<?> calculateAverage(
            @PathVariable Long studentId, 
            @PathVariable Long courseId,
            @RequestParam(required = false) Long subjectId) {
        try {
            Double average = gradeService.calculateAverage(studentId, courseId, subjectId);
            Map<String, Object> response = new HashMap<>();
            if (average != null) {
                response.put("average", average);
                response.put("studentId", studentId);
                response.put("courseId", courseId);
            } else {
                response.put("average", null);
                response.put("message", "El estudiante no tiene notas registradas en este curso");
                response.put("studentId", studentId);
                response.put("courseId", courseId);
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al calcular el promedio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /api/grades/student/{studentId}/course/{courseId}/grouped-averages
     * Obtiene los promedios agrupados por tipo de evaluación para un estudiante en un curso.
     * Calcula el promedio de cada grupo de evaluaciones del mismo tipo y el promedio final.
     */
    @GetMapping("/student/{studentId}/course/{courseId}/grouped-averages")
    public ResponseEntity<?> getGroupedAverages(@PathVariable Long studentId, @PathVariable Long courseId) {
        try {
            StudentGroupedAveragesDTO result = gradeService.getGroupedAverages(studentId, courseId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener los promedios agrupados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /api/grades/course/{courseId}/grouped-averages
     * Obtiene los promedios agrupados por tipo de evaluación para todos los estudiantes de un curso.
     */
    @GetMapping("/course/{courseId}/grouped-averages")
    public ResponseEntity<?> getGroupedAveragesByCourse(@PathVariable Long courseId) {
        try {
            List<StudentGroupedAveragesDTO> results = gradeService.getGroupedAveragesByCourse(courseId);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener los promedios agrupados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

