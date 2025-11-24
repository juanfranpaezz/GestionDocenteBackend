package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.LoginResponse;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ProfessorDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.RegisterRequest;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.ProfessorRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.ProfessorPrincipal;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.JwtService;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.ProfessorService;

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
    private CourseRepository courseRepository;
    
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
        try {
            // 1. Buscar profesor por email
            Professor professor = professorRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
            
            // 2. Verificar contraseña
            if (!passwordEncoder.matches(password, professor.getPassword())) {
                throw new IllegalArgumentException("Credenciales inválidas");
            }
            
            // 3. Generar token JWT con el rol del profesor
            String role = professor.getRole() != null ? professor.getRole().name() : "PROFESSOR";
            String token = jwtService.generateToken(professor.getId(), professor.getEmail(), role);
            
            // 4. Convertir profesor a DTO
            ProfessorDTO professorDTO = convertToDTO(professor);
            
            // 5. Crear respuesta con token y datos del profesor
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setExpiresIn(jwtService.getExpirationInSeconds());
            response.setProfessor(professorDTO);
            
            return response;
        } catch (IllegalArgumentException e) {
            // Re-lanzar excepciones de validación
            throw e;
        } catch (Exception e) {
            // Log del error para debugging
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al procesar el login: " + e.getMessage(), e);
        }
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
        // 1. Buscar el profesor existente
        Professor existingProfessor = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El profesor con ID " + id + " no existe"));
        
        // 2. Obtener el profesor autenticado
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 3. Validar permisos:
        //    - Si es administrador, puede actualizar cualquier profesor
        //    - Si no es administrador, solo puede actualizar su propio perfil
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !existingProfessor.getId().equals(currentProfessorId)) {
            throw new IllegalStateException("Solo puedes actualizar tu propio perfil");
        }
        
        // 4. Actualizar los campos permitidos
        if (professorDTO.getName() != null) {
            existingProfessor.setName(professorDTO.getName());
        }
        if (professorDTO.getLastname() != null) {
            existingProfessor.setLastname(professorDTO.getLastname());
        }
        if (professorDTO.getCel() != null) {
            existingProfessor.setCel(professorDTO.getCel());
        }
        if (professorDTO.getPhotoUrl() != null) {
            existingProfessor.setPhotoUrl(professorDTO.getPhotoUrl());
        }
        // Solo administradores pueden cambiar el rol
        if (isAdmin && professorDTO.getRole() != null) {
            existingProfessor.setRole(professorDTO.getRole());
        }
        // El email no se puede cambiar
        // La contraseña se cambia en un endpoint separado
        
        // 5. Guardar los cambios
        Professor updatedProfessor = professorRepository.save(existingProfessor);
        
        // 6. Convertir a DTO y retornar
        return convertToDTO(updatedProfessor);
    }
    
    @Override
    public boolean emailExists(String email) {
        return professorRepository.existsByEmail(email);
    }
    
    @Override
    public List<ProfessorDTO> getAllProfessors() {
        // Solo administradores pueden listar todos los profesores
        validateAdminAccess();
        
        List<Professor> professors = professorRepository.findAll();
        return professors.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public ProfessorDTO getProfessorById(Long id) {
        // Solo administradores pueden ver otros profesores
        validateAdminAccess();
        
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El profesor con ID " + id + " no existe"));
        
        return convertToDTO(professor);
    }
    
    @Override
    public List<ProfessorDTO> searchProfessorsByLastname(String lastname) {
        // Solo administradores pueden buscar profesores
        validateAdminAccess();
        
        List<Professor> professors = professorRepository.findByLastnameContainingIgnoreCase(lastname);
        return professors.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public void deleteProfessor(Long id) {
        // Solo administradores pueden eliminar profesores
        validateAdminAccess();
        
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El profesor con ID " + id + " no existe"));
        
        // Verificar si el profesor tiene cursos asociados
        List<com.gestion.docente.backend.Gestion.Docente.Backend.model.Course> courses = courseRepository.findByProfessorId(id);
        if (!courses.isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el profesor porque tiene " + courses.size() + " curso(s) asociado(s). Elimine primero los cursos del profesor.");
        }
        
        professorRepository.deleteById(id);
    }
    
    /**
     * Valida que el usuario autenticado sea administrador
     */
    private void validateAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay un usuario autenticado");
        }
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw new IllegalStateException("Solo los administradores pueden realizar esta acción");
        }
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
        dto.setRole(professor.getRole());
        // NO incluir password por seguridad
        return dto;
    }
}

