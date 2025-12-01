package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.GradeScaleOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeScaleOptionRepository extends JpaRepository<GradeScaleOption, Long> {
    
    List<GradeScaleOption> findByGradeScaleIdOrderByOrderValueAsc(Long gradeScaleId);
    
    void deleteByGradeScaleId(Long gradeScaleId);
}

