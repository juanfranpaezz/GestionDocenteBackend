package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EvaluationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EvaluationService {
    
    List<EvaluationDTO> getEvaluationsByCourse(Long courseId);
    
    Page<EvaluationDTO> getEvaluationsByCourse(Long courseId, Pageable pageable);
    
    EvaluationDTO addEvaluation(EvaluationDTO evaluationDTO);
    
    EvaluationDTO updateEvaluation(Long id, EvaluationDTO evaluationDTO);
    
    void deleteEvaluation(Long id);
    
    void sendGradesByEmail(Long evaluationId);
    
    void sendGradesByEmailCustom(Long evaluationId, com.gestion.docente.backend.Gestion.Docente.Backend.dto.SendGradesCustomDTO sendDTO);
    
    EvaluationDTO updateEvaluationGradeScale(Long id, Long gradeScaleId);
}

