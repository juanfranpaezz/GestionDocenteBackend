package com.gestion.docente.backend.Gestion.Docente.Backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "professors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Professor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String lastname;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String cel;
    
    private String photoUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.PROFESSOR; // Por defecto PROFESSOR
    
    // Campos para verificación de email
    @Column(nullable = false)
    private Boolean emailVerified = false;
    
    @Column(length = 100)
    private String verificationToken;
    
    private LocalDateTime tokenExpiryDate;
}

