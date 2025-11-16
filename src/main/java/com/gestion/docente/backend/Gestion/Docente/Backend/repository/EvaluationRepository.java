package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    
    List<Evaluation> findByCourseId(Long courseId);
    
    Page<Evaluation> findByCourseId(Long courseId, Pageable pageable);
}

