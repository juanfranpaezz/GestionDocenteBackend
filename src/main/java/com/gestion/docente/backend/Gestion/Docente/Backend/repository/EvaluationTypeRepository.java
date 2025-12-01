package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationTypeRepository extends JpaRepository<EvaluationType, Long> {
    
    List<EvaluationType> findByCourseId(Long courseId);
    
    boolean existsByCourseIdAndNombre(Long courseId, String nombre);
}

