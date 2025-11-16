package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDTO {
    private Long id;
    
    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    private String nombre;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;
    
    @NotBlank(message = "El tipo de evaluación es obligatorio")
    private String tipo; // Ejemplo: "examen", "práctica", "tarea"
    
    @NotNull(message = "El ID del curso es obligatorio")
    private Long courseId;
}

