package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.strategy;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Grade;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.GradeRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.CourseCopyStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Estrategia para copiar Grades (notas) de un curso a otro.
 * Solo copia las notas si tanto los estudiantes como las evaluaciones fueron copiados.
 */
@Component
public class CopyGradesStrategy implements CourseCopyStrategy {
    
    private final GradeRepository gradeRepository;
    
    public CopyGradesStrategy(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }
    
    @Override
    public void copy(Long originalCourseId, Long newCourseId, Map<String, Map<Long, Long>> idMappings) {
        List<Grade> originalGrades = gradeRepository.findByCourseId(originalCourseId);
        
        Map<Long, Long> studentIdMap = idMappings.getOrDefault("students", Map.of());
        Map<Long, Long> evaluationIdMap = idMappings.getOrDefault("evaluations", Map.of());
        
        List<Grade> newGrades = originalGrades.stream()
            .filter(original -> {
                // Solo copiar si tanto el estudiante como la evaluación fueron copiados
                return studentIdMap.containsKey(original.getStudentId()) && 
                       evaluationIdMap.containsKey(original.getEvaluationId());
            })
            .map(original -> {
                Grade newGrade = new Grade();
                newGrade.setGrade(original.getGrade());
                newGrade.setGradeValue(original.getGradeValue());
                newGrade.setCourseId(newCourseId);
                newGrade.setStudentId(studentIdMap.get(original.getStudentId()));
                newGrade.setEvaluationId(evaluationIdMap.get(original.getEvaluationId()));
                return newGrade;
            })
            .collect(Collectors.toList());
        
        gradeRepository.saveAll(newGrades);
    }
    
    @Override
    public String getStrategyName() {
        return "CopyGrades";
    }
}

