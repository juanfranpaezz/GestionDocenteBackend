package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ClassDTO;

import java.util.List;

public interface ClassService {
    
    List<ClassDTO> getAllClasses();
    
    ClassDTO getClassById(Long id);
    
    ClassDTO createClass(ClassDTO classDTO);
    
    ClassDTO updateClass(Long id, ClassDTO classDTO);
    
    void deleteClass(Long id);
}

