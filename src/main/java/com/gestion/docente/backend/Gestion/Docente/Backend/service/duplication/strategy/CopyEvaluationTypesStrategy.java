package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.strategy;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.EvaluationType;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationTypeRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.CourseCopyStrategy;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Estrategia para copiar EvaluationTypes (tipos de evaluación) de un curso a otro.
 */
@Component
public class CopyEvaluationTypesStrategy implements CourseCopyStrategy {
    
    private final EvaluationTypeRepository evaluationTypeRepository;
    
    public CopyEvaluationTypesStrategy(EvaluationTypeRepository evaluationTypeRepository) {
        this.evaluationTypeRepository = evaluationTypeRepository;
    }
    
    @Override
    public void copy(Long originalCourseId, Long newCourseId, Map<String, Map<Long, Long>> idMappings) {
        List<EvaluationType> originalTypes = evaluationTypeRepository.findByCourseId(originalCourseId);
        
        List<EvaluationType> newTypes = originalTypes.stream()
            .map(original -> {
                EvaluationType newType = new EvaluationType();
                newType.setNombre(original.getNombre());
                newType.setCourseId(newCourseId);
                return newType;
            })
            .collect(Collectors.toList());
        
        List<EvaluationType> savedTypes = evaluationTypeRepository.saveAll(newTypes);
        
        // Crear mapeo de IDs usando EntityMapper
        Map<Long, Long> mapping = EntityMapper.createIdMapping(
                originalTypes,
                savedTypes,
                EvaluationType::getId
        );
        
        idMappings.put("evaluationTypes", mapping);
    }
    
    @Override
    public String getStrategyName() {
        return "CopyEvaluationTypes";
    }
}

