package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para que un ADMIN cree nuevos administradores.
 * No requiere verificación de email ya que es creado por un admin.
 * 
 * IMPORTANTE: Solo se pueden crear ADMINS desde este endpoint.
 * Los profesores deben auto-registrarse con verificación de email
 * para garantizar la legitimidad de su cuenta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfessorByAdminRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    @NotBlank(message = "El apellido es obligatorio")
    private String lastname;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    @Pattern(regexp = "^[0-9]{10,15}$|^$", message = "El celular debe contener entre 10 y 15 dígitos numéricos (si se proporciona)")
    private String cel;
    
    private String photoUrl;
    
    @NotNull(message = "El rol es obligatorio")
    private Role role; // PROFESSOR o ADMIN
}
