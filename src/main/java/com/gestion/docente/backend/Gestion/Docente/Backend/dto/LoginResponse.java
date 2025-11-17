package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del endpoint de login.
 * Contiene el token JWT, tiempo de expiración y datos del profesor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long expiresIn; // en segundos
    private ProfessorDTO professor;
}

