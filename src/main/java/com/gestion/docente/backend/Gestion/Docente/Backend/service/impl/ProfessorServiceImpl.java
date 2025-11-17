package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.LoginResponse;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ProfessorDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.RegisterRequest;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.ProfessorRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.JwtService;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.docente.backend.Gestion.Docente.Backend.security.ProfessorPrincipal;

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
    
    @Autowired
    private JwtService jwtService;
    
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
    public LoginResponse login(String email, String password) {
        // 1. Buscar profesor por email
        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        
        // 2. Verificar contraseña
        if (!passwordEncoder.matches(password, professor.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        
        // 3. Generar token JWT
        String token = jwtService.generateToken(professor.getId(), professor.getEmail());
        
        // 4. Convertir profesor a DTO
        ProfessorDTO professorDTO = convertToDTO(professor);
        
        // 5. Crear respuesta con token y datos del profesor
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpiresIn(jwtService.getExpirationInSeconds());
        response.setProfessor(professorDTO);
        
        return response;
    }
    
    @Override
    public ProfessorDTO getCurrentProfessor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay un profesor autenticado");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof ProfessorPrincipal) {
            Professor professor = ((ProfessorPrincipal) principal).getProfessor();
            return convertToDTO(professor);
        }
        
        throw new IllegalStateException("El usuario autenticado no es un profesor");
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

