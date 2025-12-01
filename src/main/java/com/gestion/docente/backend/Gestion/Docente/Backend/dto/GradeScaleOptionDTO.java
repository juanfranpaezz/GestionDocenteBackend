package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeScaleOptionDTO {
    private Long id;
    
    @NotBlank(message = "La etiqueta de la opción es obligatoria")
    private String label;
    
    private Double numericValue; // Opcional, para mapeo numérico
    
    @NotNull(message = "El orden es obligatorio")
    private Integer order;
}

