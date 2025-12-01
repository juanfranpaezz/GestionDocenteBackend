package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EvaluationTypeDTO;

import java.util.List;

public interface EvaluationTypeService {
    
    List<EvaluationTypeDTO> getEvaluationTypesByCourse(Long courseId);
    
    EvaluationTypeDTO createEvaluationType(EvaluationTypeDTO evaluationTypeDTO);
    
    EvaluationTypeDTO updateEvaluationType(Long id, EvaluationTypeDTO evaluationTypeDTO);
    
    void deleteEvaluationType(Long id);
}

