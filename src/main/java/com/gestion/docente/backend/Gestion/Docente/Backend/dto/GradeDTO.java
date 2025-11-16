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
    
    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota no puede ser menor a 0")
    @DecimalMax(value = "10.0", message = "La nota no puede ser mayor a 10")
    private Double grade;
    
    @NotNull(message = "El ID del curso es obligatorio")
    private Long courseId;
    
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long studentId;
    
    @NotNull(message = "El ID de la evaluación es obligatorio")
    private Long evaluationId;
}

