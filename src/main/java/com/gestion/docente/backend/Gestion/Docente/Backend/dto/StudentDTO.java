package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    
    @NotBlank(message = "El nombre del estudiante es obligatorio")
    private String firstName;
    
    private String lastName;
    
    @Pattern(regexp = "^[0-9]{10,15}$", message = "El celular debe contener entre 10 y 15 dígitos numéricos")
    private String cel;
    
    @NotBlank(message = "El email del estudiante es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @Size(max = 20, message = "El documento no puede tener más de 20 caracteres")
    private String document;
    
    // El courseId es necesario para asociar el estudiante a un curso.
    // El ownership del curso se valida automáticamente (el curso debe pertenecer al profesor del JWT).
    private Long courseId;
}

