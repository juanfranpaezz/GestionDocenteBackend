package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentAverageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GradeService {
    
    List<GradeDTO> getGradesByCourse(Long courseId);
    
    Page<GradeDTO> getGradesByCourse(Long courseId, Pageable pageable);
    
    GradeDTO setGrade(GradeDTO gradeDTO);
    
    GradeDTO updateGrade(Long id, GradeDTO gradeDTO);
    
    List<GradeDTO> getGradesByEvaluation(Long evaluationId);
    
    Page<GradeDTO> getGradesByEvaluation(Long evaluationId, Pageable pageable);
    
    Double calculateAverage(Long studentId, Long courseId);
    
    /**
     * Obtiene los promedios de todos los estudiantes de un curso.
     * Incluye información del estudiante y su promedio calculado.
     * Si un estudiante no tiene notas, el promedio será null y hasGrades será false.
     * 
     * @param courseId ID del curso
     * @return Lista de StudentAverageDTO con información de cada estudiante y su promedio
     */
    List<StudentAverageDTO> getAveragesByCourse(Long courseId);
}

