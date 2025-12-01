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
    private String tipo; // Ejemplo: "examen", "práctica", "tarea" (mantenido para compatibilidad)
    
    private Long evaluationTypeId; // ID del tipo de evaluación agrupado (opcional)
    
    private Boolean gradesSentByEmail; // Flag para controlar edición
    
    private String customMessage; // Mensaje personalizado opcional
    
    private Long gradeScaleId; // ID de la escala de notas (null = numérico)
    
    private Long subjectId; // ID de la materia (nullable para compatibilidad)
    
    private Double approvalGrade; // Nota mínima para aprobar (opcional, sobrescribe default del curso)
    
    private Double qualificationGrade; // Nota mínima para habilitar (opcional, sobrescribe default del curso)
    
    // El courseId es necesario para asociar la evaluación a un curso.
    // El ownership del curso se valida automáticamente (el curso debe pertenecer al profesor del JWT).
    @NotNull(message = "El ID del curso es obligatorio")
    private Long courseId;
}

