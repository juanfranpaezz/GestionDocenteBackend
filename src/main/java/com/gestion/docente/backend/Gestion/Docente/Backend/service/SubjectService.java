package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.SubjectDTO;

import java.util.List;

public interface SubjectService {
    
    List<SubjectDTO> getSubjectsByCourse(Long courseId);
    
    SubjectDTO getDefaultSubject(Long courseId);
    
    SubjectDTO createSubject(SubjectDTO subjectDTO);
    
    SubjectDTO updateSubject(Long id, SubjectDTO subjectDTO);
    
    void deleteSubject(Long id);
}

