package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeDTO {
    private Long id;
    
    // grade es opcional si hay gradeValue (notas categóricas)
    @DecimalMin(value = "0.0", message = "La nota no puede ser menor a 0", inclusive = true)
    @DecimalMax(value = "10.0", message = "La nota no puede ser mayor a 10", inclusive = true)
    private Double grade;
    
    private String gradeValue; // Para notas categóricas (ej: "aprobado", "distinguido")
    
    // El courseId es necesario para asociar la nota a un curso.
    // El ownership del curso se valida automáticamente (el curso debe pertenecer al profesor del JWT).
    @NotNull(message = "El ID del curso es obligatorio")
    private Long courseId;
    
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long studentId;
    
    @NotNull(message = "El ID de la evaluación es obligatorio")
    private Long evaluationId;
}

