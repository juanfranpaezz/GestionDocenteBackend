package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.strategy;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.CourseCopyStrategy;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Estrategia para copiar Evaluations (evaluaciones) de un curso a otro.
 * Mantiene las relaciones con EvaluationTypes y Subjects usando los mapeos de IDs.
 */
@Component
public class CopyEvaluationsStrategy implements CourseCopyStrategy {
    
    private final EvaluationRepository evaluationRepository;
    
    public CopyEvaluationsStrategy(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }
    
    @Override
    public void copy(Long originalCourseId, Long newCourseId, Map<String, Map<Long, Long>> idMappings) {
        List<Evaluation> originalEvaluations = evaluationRepository.findByCourseId(originalCourseId);
        
        Map<Long, Long> evaluationTypeIdMap = idMappings.getOrDefault("evaluationTypes", Map.of());
        Map<Long, Long> subjectIdMap = idMappings.getOrDefault("subjects", Map.of());
        
        List<Evaluation> newEvaluations = originalEvaluations.stream()
            .map(original -> {
                Evaluation newEval = new Evaluation();
                newEval.setNombre(original.getNombre());
                newEval.setDate(original.getDate());
                newEval.setTipo(original.getTipo());
                
                // Mapear evaluationTypeId usando EntityMapper
                newEval.setEvaluationTypeId(EntityMapper.getMappedId(
                        evaluationTypeIdMap, original.getEvaluationTypeId()));
                
                // Mapear subjectId usando EntityMapper
                Long mappedSubjectId = EntityMapper.getMappedId(subjectIdMap, original.getSubjectId());
                if (mappedSubjectId == null && !subjectIdMap.isEmpty()) {
                    // Si hay subjects pero el original no tenía subjectId, usar el primero disponible
                    mappedSubjectId = subjectIdMap.values().iterator().next();
                }
                newEval.setSubjectId(mappedSubjectId);
                
                newEval.setGradeScaleId(original.getGradeScaleId());
                newEval.setApprovalGrade(original.getApprovalGrade());
                newEval.setQualificationGrade(original.getQualificationGrade());
                newEval.setCourseId(newCourseId);
                newEval.setGradesSentByEmail(false);
                newEval.setCustomMessage(null);
                return newEval;
            })
            .collect(Collectors.toList());
        
        List<Evaluation> savedEvaluations = evaluationRepository.saveAll(newEvaluations);
        
        // Crear mapeo de IDs usando EntityMapper
        Map<Long, Long> mapping = EntityMapper.createIdMapping(
                originalEvaluations,
                savedEvaluations,
                Evaluation::getId
        );
        
        idMappings.put("evaluations", mapping);
    }
    
    @Override
    public String getStrategyName() {
        return "CopyEvaluations";
    }
}

