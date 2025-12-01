package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendPersonalizedMessageDTO {
    @NotBlank(message = "El asunto es obligatorio")
    private String subject;
    
    @NotBlank(message = "El mensaje es obligatorio")
    private String message;
}

