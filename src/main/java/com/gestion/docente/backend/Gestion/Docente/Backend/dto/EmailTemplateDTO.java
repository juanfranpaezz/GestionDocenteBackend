package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateDTO {
    private Long id;
    
    @NotBlank(message = "El nombre del template es obligatorio")
    private String name;
    
    @NotBlank(message = "El asunto es obligatorio")
    private String subject;
    
    @NotBlank(message = "El cuerpo del email es obligatorio")
    private String body;
    
    private Long professorId; // null si isGlobal = true
    
    @NotNull(message = "El campo isGlobal es obligatorio")
    private Boolean isGlobal = false;
}

