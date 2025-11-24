package com.gestion.docente.backend.Gestion.Docente.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private Long professorId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professorId", insertable = false, updatable = false)
    private Professor professor;
}

