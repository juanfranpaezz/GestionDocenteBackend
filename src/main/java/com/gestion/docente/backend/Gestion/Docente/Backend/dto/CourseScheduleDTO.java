package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseScheduleDTO {
    private Long id;
    
    @NotNull(message = "El ID del curso es obligatorio")
    private Long courseId;
    
    @NotNull(message = "El día de la semana es obligatorio")
    private DayOfWeek dayOfWeek;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;
    
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;
    
    private Long subjectId; // ID de la materia (nullable)
}

