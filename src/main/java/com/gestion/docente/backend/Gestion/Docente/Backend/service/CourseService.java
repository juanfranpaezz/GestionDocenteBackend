package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CourseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    
    List<CourseDTO> getAllCourses();
    
    Page<CourseDTO> getAllCourses(Pageable pageable);
    
    List<CourseDTO> getCoursesByProfessor(Long professorId);
    
    Page<CourseDTO> getCoursesByProfessor(Long professorId, Pageable pageable);
    
    CourseDTO getCourseById(Long id);
    
    CourseDTO createCourse(CourseDTO courseDTO);
    
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    
    void deleteCourse(Long id);
}

