package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un estudiante con su promedio de notas en un curso.
 * Se utiliza para mostrar la lista de estudiantes con sus promedios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAverageDTO {
    
    private Long studentId;
    
    private String firstName;
    
    private String lastName;
    
    /**
     * Promedio de notas del estudiante en el curso.
     * Puede ser null si el estudiante no tiene notas registradas.
     */
    private Double average;
    
    /**
     * Indica si el estudiante tiene notas registradas.
     * true si tiene al menos una nota, false si no tiene ninguna.
     */
    private Boolean hasGrades;
    
    /**
     * Número de evaluaciones con nota registrada.
     */
    private Integer gradesCount;
}

