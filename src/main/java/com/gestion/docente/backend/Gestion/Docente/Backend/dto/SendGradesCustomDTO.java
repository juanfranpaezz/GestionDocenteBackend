package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendGradesCustomDTO {
    private Long templateId; // ID del template (opcional)
    
    private String customMessage; // Mensaje personalizado opcional
    
    private Boolean useTemplate = true; // Si usar template o solo mensaje personalizado
}

