package com.gestion.docente.backend.Gestion.Docente.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grade_scale_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeScaleOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long gradeScaleId;
    
    @Column(nullable = false, length = 50)
    private String label; // ej: "aprobado", "distinguido"
    
    @Column(nullable = true)
    private Double numericValue; // Mapeo opcional (ej: "aprobado" = 6.0)
    
    @Column(name = "order_index", nullable = false)
    private Integer orderValue; // Orden de visualización
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gradeScaleId", insertable = false, updatable = false)
    private GradeScale gradeScale;
}

