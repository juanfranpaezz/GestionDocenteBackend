package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeScaleDTO;

import java.util.List;

public interface GradeScaleService {
    
    List<GradeScaleDTO> getGradeScales(Boolean includeGlobal);
    
    GradeScaleDTO createGradeScale(GradeScaleDTO gradeScaleDTO);
    
    GradeScaleDTO updateGradeScale(Long id, GradeScaleDTO gradeScaleDTO);
    
    void deleteGradeScale(Long id);
    
    List<GradeScaleDTO> getAvailableGradeScalesForEvaluation(Long evaluationId);
}

