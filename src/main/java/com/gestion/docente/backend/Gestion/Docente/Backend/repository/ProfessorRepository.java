package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    
    Optional<Professor> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Professor> findByLastnameContainingIgnoreCase(String lastname);
    
    // Búsqueda por nombre, apellido o email
    List<Professor> findByNameContainingIgnoreCaseOrLastnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String name, String lastname, String email);
    
    // Búsqueda por token de verificación
    Optional<Professor> findByVerificationToken(String token);
}

