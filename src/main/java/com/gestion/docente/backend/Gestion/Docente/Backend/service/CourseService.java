package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CourseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    
    List<CourseDTO> getAllCourses();
    
    Page<CourseDTO> getAllCourses(Pageable pageable);
    
    CourseDTO getCourseById(Long id);
    
    CourseDTO createCourse(CourseDTO courseDTO);
    
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    
    void deleteCourse(Long id);
    
    CourseDTO archiveCourse(Long id);
    
    CourseDTO unarchiveCourse(Long id);
    
    List<CourseDTO> getArchivedCourses();
    
    CourseDTO duplicateCourse(Long id, com.gestion.docente.backend.Gestion.Docente.Backend.dto.DuplicateCourseDTO options);
    
    List<CourseDTO> searchCourses(String searchQuery, Boolean archived);
    
    void sendPersonalizedMessageToAllStudents(Long courseId, com.gestion.docente.backend.Gestion.Docente.Backend.dto.SendPersonalizedMessageDTO messageDTO);
}

