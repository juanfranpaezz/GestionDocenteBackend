package com.gestion.docente.backend.Gestion.Docente.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name; // ej: "Template Notas Parcial"
    
    @Column(nullable = false, length = 500)
    private String subject; // Con variables: "Nota de {{evaluationName}} - {{courseName}}"
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body; // HTML con variables: {{studentName}}, {{grade}}, etc.
    
    @Column(nullable = true)
    private Long professorId; // null si isGlobal = true
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isGlobal = false;
}

