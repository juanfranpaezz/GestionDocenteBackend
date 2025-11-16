package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ProfessorDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.RegisterRequest;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.ProfessorRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de profesores.
 * Maneja el registro, autenticación y gestión de profesores.
 */
@Service
@Transactional
public class ProfessorServiceImpl implements ProfessorService {
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public ProfessorDTO register(RegisterRequest registerRequest) {
        // 1. Validar que el email no exista
        if (professorRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("El email " + registerRequest.getEmail() + " ya está registrado");
        }
        
        // 2. Crear nueva entidad Professor
        Professor professor = new Professor();
        professor.setName(registerRequest.getName());
        professor.setLastname(registerRequest.getLastname());
        professor.setEmail(registerRequest.getEmail());
        
        // 3. Encriptar contraseña con BCrypt
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        professor.setPassword(encodedPassword);
        
        // 4. Campos opcionales
        professor.setCel(registerRequest.getCel());
        professor.setPhotoUrl(registerRequest.getPhotoUrl());
        
        // 5. Guardar en la base de datos
        Professor savedProfessor = professorRepository.save(professor);
        
        // 6. Convertir a DTO (sin password) y retornar
        return convertToDTO(savedProfessor);
    }
    
    @Override
    public String login(String email, String password) {
        // TODO: Implementar cuando se agregue JWT
        throw new UnsupportedOperationException("Login aún no implementado. Se implementará con JWT.");
    }
    
    @Override
    public ProfessorDTO getCurrentProfessor() {
        // TODO: Implementar cuando se agregue autenticación
        throw new UnsupportedOperationException("getCurrentProfessor aún no implementado.");
    }
    
    @Override
    public ProfessorDTO updateProfessor(Long id, ProfessorDTO professorDTO) {
        // TODO: Implementar actualización de profesor
        throw new UnsupportedOperationException("updateProfessor aún no implementado.");
    }
    
    @Override
    public boolean emailExists(String email) {
        return professorRepository.existsByEmail(email);
    }
    
    /**
     * Convierte una entidad Professor a ProfessorDTO (sin incluir password)
     */
    private ProfessorDTO convertToDTO(Professor professor) {
        ProfessorDTO dto = new ProfessorDTO();
        dto.setId(professor.getId());
        dto.setName(professor.getName());
        dto.setLastname(professor.getLastname());
        dto.setEmail(professor.getEmail());
        dto.setCel(professor.getCel());
        dto.setPhotoUrl(professor.getPhotoUrl());
        // NO incluir password por seguridad
        return dto;
    }
}

