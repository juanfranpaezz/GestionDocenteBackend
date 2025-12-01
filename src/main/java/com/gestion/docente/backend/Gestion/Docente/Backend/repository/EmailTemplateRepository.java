package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    
    List<EmailTemplate> findByProfessorId(Long professorId);
    
    List<EmailTemplate> findByIsGlobalTrue();
    
    @Query("SELECT e FROM EmailTemplate e WHERE e.professorId = :professorId OR e.isGlobal = true")
    List<EmailTemplate> findByProfessorIdOrIsGlobalTrue(@Param("professorId") Long professorId);
    
    boolean existsByIdAndProfessorId(Long id, Long professorId);
}

