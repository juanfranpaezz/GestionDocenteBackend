package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentAverageDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentGroupedAveragesDTO;
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
    
    Double calculateAverage(Long studentId, Long courseId, Long subjectId);
    
    /**
     * Obtiene los promedios de todos los estudiantes de un curso.
     * Incluye información del estudiante y su promedio calculado.
     * Si un estudiante no tiene notas, el promedio será null y hasGrades será false.
     * 
     * @param courseId ID del curso
     * @return Lista de StudentAverageDTO con información de cada estudiante y su promedio
     */
    List<StudentAverageDTO> getAveragesByCourse(Long courseId);
    
    /**
     * Obtiene los promedios agrupados por tipo de evaluación para un estudiante en un curso.
     * Calcula el promedio de cada grupo de evaluaciones del mismo tipo y el promedio final.
     * 
     * @param studentId ID del estudiante
     * @param courseId ID del curso
     * @return StudentGroupedAveragesDTO con promedios agrupados por tipo y promedio final
     */
    StudentGroupedAveragesDTO getGroupedAverages(Long studentId, Long courseId);
    
    /**
     * Obtiene los promedios agrupados por tipo de evaluación para todos los estudiantes de un curso.
     * 
     * @param courseId ID del curso
     * @return Lista de StudentGroupedAveragesDTO con promedios agrupados por tipo y promedio final para cada estudiante
     */
    List<StudentGroupedAveragesDTO> getGroupedAveragesByCourse(Long courseId);
}

