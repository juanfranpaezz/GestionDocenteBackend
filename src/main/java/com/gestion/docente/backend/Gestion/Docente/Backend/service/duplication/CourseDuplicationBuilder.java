package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;

/**
 * Builder para construir cursos duplicados.
 * Aplica el patrón Builder (GOF) para facilitar la construcción de cursos duplicados
 * de forma más flexible y legible.
 */
public class CourseDuplicationBuilder {
    
    private final CourseRepository courseRepository;
    private final Course originalCourse;
    private Course newCourse;
    
    public CourseDuplicationBuilder(CourseRepository courseRepository, Course originalCourse) {
        this.courseRepository = courseRepository;
        this.originalCourse = originalCourse;
        this.newCourse = new Course();
    }
    
    /**
     * Establece el nombre del curso duplicado.
     * 
     * @param name Nombre del curso
     * @return Este builder para encadenamiento
     */
    public CourseDuplicationBuilder withName(String name) {
        this.newCourse.setName(name);
        return this;
    }
    
    /**
     * Establece el nombre con sufijo por defecto " - Copia".
     * 
     * @return Este builder para encadenamiento
     */
    public CourseDuplicationBuilder withDefaultName() {
        return withName(originalCourse.getName() + " - Copia");
    }
    
    /**
     * Copia los datos básicos del curso original.
     * 
     * @return Este builder para encadenamiento
     */
    public CourseDuplicationBuilder copyBasicData() {
        this.newCourse.setSchool(originalCourse.getSchool());
        this.newCourse.setDescription(originalCourse.getDescription());
        this.newCourse.setApprovalGrade(originalCourse.getApprovalGrade());
        this.newCourse.setQualificationGrade(originalCourse.getQualificationGrade());
        return this;
    }
    
    /**
     * Establece el profesor del curso duplicado.
     * 
     * @param professorId ID del profesor
     * @return Este builder para encadenamiento
     */
    public CourseDuplicationBuilder withProfessor(Long professorId) {
        this.newCourse.setProfessorId(professorId);
        return this;
    }
    
    /**
     * Establece el profesor actual autenticado.
     * 
     * @return Este builder para encadenamiento
     */
    public CourseDuplicationBuilder withCurrentProfessor() {
        return withProfessor(SecurityUtils.getCurrentProfessorId());
    }
    
    /**
     * Establece el estado de archivado.
     * 
     * @param archived Estado de archivado
     * @return Este builder para encadenamiento
     */
    public CourseDuplicationBuilder withArchived(boolean archived) {
        this.newCourse.setArchived(archived);
        if (!archived) {
            this.newCourse.setArchivedDate(null);
        }
        return this;
    }
    
    /**
     * Construye y guarda el curso duplicado.
     * 
     * @return El curso duplicado guardado
     */
    public Course build() {
        // Valores por defecto si no se establecieron
        if (newCourse.getName() == null) {
            withDefaultName();
        }
        if (newCourse.getProfessorId() == null) {
            withCurrentProfessor();
        }
        if (newCourse.getArchived() == null) {
            withArchived(false);
        }
        
        return courseRepository.save(newCourse);
    }
}

