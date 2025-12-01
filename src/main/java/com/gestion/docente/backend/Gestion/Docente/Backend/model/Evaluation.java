package com.gestion.docente.backend.Gestion.Docente.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private String tipo; // Ejemplo: "examen", "práctica", "tarea" (mantenido para compatibilidad)
    
    @Column(nullable = true)
    private Long evaluationTypeId; // Relación con EvaluationType (opcional para compatibilidad)
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean gradesSentByEmail = false;
    
    @Column(nullable = true, length = 1000)
    private String customMessage; // Mensaje personalizado opcional
    
    @Column(nullable = true)
    private Long gradeScaleId; // null = notas numéricas (comportamiento actual)
    
    @Column(nullable = true)
    private Double approvalGrade; // Nota mínima para aprobar (opcional, sobrescribe default del curso)
    
    @Column(nullable = true)
    private Double qualificationGrade; // Nota mínima para habilitar (opcional, sobrescribe default del curso)
    
    @Column(nullable = true)
    private Long subjectId; // Relación con Subject (nullable para compatibilidad)
    
    @Column(nullable = false)
    private Long courseId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId", insertable = false, updatable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluationTypeId", insertable = false, updatable = false)
    private EvaluationType evaluationType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gradeScaleId", insertable = false, updatable = false)
    private GradeScale gradeScale;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subjectId", insertable = false, updatable = false)
    private Subject subject;
    
    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();
}

