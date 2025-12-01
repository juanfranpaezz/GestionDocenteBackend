package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationTypeDTO {
    private Long id;
    
    @NotBlank(message = "El nombre del tipo de evaluación es obligatorio")
    private String nombre;
    
    @NotNull(message = "El ID del curso es obligatorio")
    private Long courseId;
    
    private Double weight; // Porcentaje de peso para el promedio (0-100), null = peso igual
}

