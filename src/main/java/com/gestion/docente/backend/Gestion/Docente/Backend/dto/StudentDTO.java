package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String cel;
    private String email;
    private String document;
    
    // El courseId es necesario para asociar el estudiante a un curso.
    // El ownership del curso se valida automáticamente (el curso debe pertenecer al profesor del JWT).
    private Long courseId;
}

