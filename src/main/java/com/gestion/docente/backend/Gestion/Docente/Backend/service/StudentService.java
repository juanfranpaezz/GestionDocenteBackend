package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentDTO;

import java.util.List;

public interface StudentService {
    
    List<StudentDTO> getStudentsByCourse(Long courseId);
    
    StudentDTO addStudentToCourse(StudentDTO studentDTO);
    
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    
    void removeStudent(Long id);
}

