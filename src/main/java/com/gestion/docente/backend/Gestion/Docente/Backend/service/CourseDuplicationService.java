package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.DuplicateCourseDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;

/**
 * Servicio dedicado a la duplicación de cursos.
 * Aplica el principio de responsabilidad única (SRP) separando la lógica de duplicación
 * del servicio principal de cursos.
 */
public interface CourseDuplicationService {
    
    /**
     * Duplica un curso con todas sus entidades relacionadas según las opciones especificadas.
     * 
     * @param originalCourse El curso original a duplicar
     * @param options Opciones de duplicación (qué copiar)
     * @return El nuevo curso duplicado
     */
    Course duplicateCourse(Course originalCourse, DuplicateCourseDTO options);
}

