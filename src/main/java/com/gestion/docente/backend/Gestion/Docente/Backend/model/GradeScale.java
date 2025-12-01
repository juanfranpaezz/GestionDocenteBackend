package com.gestion.docente.backend.Gestion.Docente.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grade_scales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeScale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name; // ej: "Escala Aprobado/Desaprobado"
    
    @Column(nullable = true)
    private Long professorId; // null si isGlobal = true
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isGlobal = false; // Si es global, disponible para todos
    
    @OneToMany(mappedBy = "gradeScale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GradeScaleOption> options = new ArrayList<>();
}

