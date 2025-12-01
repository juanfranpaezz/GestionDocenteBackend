package com.gestion.docente.backend.Gestion.Docente.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Double grade; // Puede ser null si el estudiante aún no fue evaluado
    
    @Column(nullable = true, length = 50)
    private String gradeValue; // Para notas categóricas (ej: "aprobado", "distinguido")
    
    @Column(nullable = false)
    private Long courseId;
    
    @Column(nullable = false)
    private Long studentId;
    
    @Column(nullable = false)
    private Long evaluationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId", insertable = false, updatable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studentId", insertable = false, updatable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluationId", insertable = false, updatable = false)
    private Evaluation evaluation;
}

