package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    
    @NotBlank(message = "El nombre del curso es obligatorio")
    private String name;
    
    @NotBlank(message = "La escuela es obligatoria")
    private String school;
    
    private String description;
    
    // El professorId se obtiene automáticamente del JWT, no es necesario enviarlo
    private Long professorId;
}

