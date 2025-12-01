package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    
    List<Evaluation> findByCourseId(Long courseId);
    
    Page<Evaluation> findByCourseId(Long courseId, Pageable pageable);
    
    // Buscar evaluación por curso, nombre y fecha (para validar duplicados)
    boolean existsByCourseIdAndNombreAndDate(Long courseId, String nombre, java.time.LocalDate date);
    
    // Buscar evaluaciones que usan una escala de notas
    List<Evaluation> findByGradeScaleId(Long gradeScaleId);
}

