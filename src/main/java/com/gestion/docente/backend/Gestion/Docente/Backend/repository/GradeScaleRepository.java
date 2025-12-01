package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.GradeScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeScaleRepository extends JpaRepository<GradeScale, Long> {
    
    List<GradeScale> findByProfessorId(Long professorId);
    
    List<GradeScale> findByIsGlobalTrue();
    
    @Query("SELECT g FROM GradeScale g WHERE g.professorId = :professorId OR g.isGlobal = true")
    List<GradeScale> findByProfessorIdOrIsGlobalTrue(@Param("professorId") Long professorId);
    
    boolean existsByIdAndProfessorId(Long id, Long professorId);
}

