package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private LocalDate date;
    private Boolean present;
    
    // El courseId es necesario para asociar la asistencia a un curso.
    // El ownership del curso se valida automáticamente (el curso debe pertenecer al profesor del JWT).
    private Long courseId;
    
    private Long studentId;
    
    private Long subjectId; // ID de la materia (nullable para compatibilidad)
}

