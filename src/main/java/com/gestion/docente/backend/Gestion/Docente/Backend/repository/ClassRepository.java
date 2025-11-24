package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    
    List<Class> findByProfessorId(Long professorId);
}

