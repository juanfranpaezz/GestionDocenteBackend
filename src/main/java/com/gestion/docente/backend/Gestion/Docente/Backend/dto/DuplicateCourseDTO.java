package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuplicateCourseDTO {
    private Boolean copyStudents = true;
    
    private Boolean copyEvaluationTypes = false; // No copiar tipos de evaluación por defecto
    
    private Boolean copyEvaluations = false; // Por defecto false (sin notas)
    
    private Boolean copySchedules = true;
    
    private Boolean copySubjects = true; // Copiar materias por defecto
    
    private Boolean copyGrades = false; // No copiar notas por defecto (solo si se copian evaluaciones)
}

