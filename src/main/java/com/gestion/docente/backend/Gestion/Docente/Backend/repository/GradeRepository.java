package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    List<Grade> findByCourseId(Long courseId);
    
    Page<Grade> findByCourseId(Long courseId, Pageable pageable);
    
    List<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    Optional<Grade> findByStudentIdAndEvaluationId(Long studentId, Long evaluationId);
    
    List<Grade> findByEvaluationId(Long evaluationId);
    
    Page<Grade> findByEvaluationId(Long evaluationId, Pageable pageable);
}

