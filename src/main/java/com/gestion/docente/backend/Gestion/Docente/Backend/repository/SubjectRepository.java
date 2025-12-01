package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByCourseId(Long courseId);
    boolean existsByCourseIdAndName(Long courseId, String name);
    // Buscar materia sin nombre (default) en un curso
    List<Subject> findByCourseIdAndNameIsNull(Long courseId);
    // Buscar primera materia de un curso (para materia default)
    Subject findFirstByCourseIdOrderByIdAsc(Long courseId);
}

