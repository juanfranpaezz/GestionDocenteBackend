package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EvaluationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EvaluationService {
    
    List<EvaluationDTO> getEvaluationsByCourse(Long courseId);
    
    Page<EvaluationDTO> getEvaluationsByCourse(Long courseId, Pageable pageable);
    
    EvaluationDTO addEvaluation(EvaluationDTO evaluationDTO);
    
    void deleteEvaluation(Long id);
}

