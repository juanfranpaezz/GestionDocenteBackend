package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private Long id;
    
    // Permite null o vacío para materia default sin nombre
    private String name;
    
    // El courseId es necesario para asociar la materia a un curso.
    // El ownership del curso se valida automáticamente (el curso debe pertenecer al profesor del JWT).
    private Long courseId;
}

