package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeScaleDTO {
    private Long id;
    
    @NotBlank(message = "El nombre de la escala es obligatorio")
    private String name;
    
    private Long professorId; // null si isGlobal = true
    
    @NotNull(message = "El campo isGlobal es obligatorio")
    private Boolean isGlobal = false;
    
    @Valid
    @NotNull(message = "Las opciones son obligatorias")
    private List<GradeScaleOptionDTO> options;
}

