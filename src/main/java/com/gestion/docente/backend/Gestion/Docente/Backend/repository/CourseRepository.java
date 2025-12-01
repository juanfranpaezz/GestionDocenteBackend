package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByProfessorId(Long professorId);
    
    Page<Course> findByProfessorId(Long professorId, Pageable pageable);
    
    List<Course> findByProfessorIdAndArchived(Long professorId, Boolean archived);
    
    Page<Course> findByProfessorIdAndArchived(Long professorId, Boolean archived, Pageable pageable);
    
    List<Course> findByProfessorIdAndArchivedAndNameContainingIgnoreCase(Long professorId, Boolean archived, String name);
    
    List<Course> findByProfessorIdAndArchivedAndSchoolContainingIgnoreCase(Long professorId, Boolean archived, String school);
}

