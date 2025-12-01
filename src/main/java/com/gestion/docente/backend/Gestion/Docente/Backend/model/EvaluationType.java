package com.gestion.docente.backend.Gestion.Docente.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evaluation_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre; // Ejemplo: "Parcial", "TP", "Examen Final"
    
    @Column(nullable = false)
    private Long courseId;
    
    @Column(nullable = true)
    private Double weight; // Porcentaje de peso para el promedio (0-100), null = peso igual
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId", insertable = false, updatable = false)
    private Course course;
    
    @OneToMany(mappedBy = "evaluationType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations = new ArrayList<>();
}

