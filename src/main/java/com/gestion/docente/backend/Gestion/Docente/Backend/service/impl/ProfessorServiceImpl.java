package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CreateProfessorByAdminRequest;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.LoginResponse;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ProfessorDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.RegisterRequest;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Role;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.ProfessorRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.ProfessorPrincipal;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailService;
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
    
    @Autowired
    private EmailService emailService;
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    
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
        // Asignar imagen por defecto si no se proporciona una
        if (registerRequest.getPhotoUrl() == null || registerRequest.getPhotoUrl().trim().isEmpty()) {
            professor.setPhotoUrl("/assets/default-profile.svg");
        } else {
            professor.setPhotoUrl(registerRequest.getPhotoUrl());
        }
        
        // 5. Generar token de verificación
        String verificationToken = generateVerificationToken();
        professor.setVerificationToken(verificationToken);
        professor.setTokenExpiryDate(LocalDateTime.now().plusHours(24)); // Expira en 24 horas
        professor.setEmailVerified(false); // Email no verificado inicialmente
        
        // 6. Guardar en la base de datos
        Professor savedProfessor = professorRepository.save(professor);
        
        // 7. Enviar email de verificación
        try {
            emailService.sendVerificationEmail(
                savedProfessor.getEmail(),
                savedProfessor.getName(),
                verificationToken
            );
        } catch (Exception e) {
            System.err.println("Error al enviar email de verificación: " + e.getMessage());
            // No lanzar excepción para no interrumpir el registro
            // El usuario puede solicitar reenvío del token más tarde
        }
        
        // 8. Convertir a DTO (sin password) y retornar
        return convertToDTO(savedProfessor);
    }
    
    /**
     * Genera un token único de verificación
     */
    private String generateVerificationToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
    
    @Override
    public ProfessorDTO createByAdmin(CreateProfessorByAdminRequest request) {
        // 1. Validar que solo admins puedan crear profesores/admins
        validateAdminAccess();
        
        // 2. Validar que solo se puedan crear ADMINS (seguridad)
        // Los profesores deben auto-registrarse con verificación de email
        if (request.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Solo se pueden crear administradores desde esta funcionalidad. " +
                "Los profesores deben registrarse por su cuenta con verificación de email.");
        }
        
        // 3. Validar que el email no exista
        if (professorRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email " + request.getEmail() + " ya está registrado");
        }
        
        // 4. Crear nueva entidad Professor
        Professor professor = new Professor();
        professor.setName(request.getName());
        professor.setLastname(request.getLastname());
        professor.setEmail(request.getEmail());
        
        // 5. Encriptar contraseña con BCrypt
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        professor.setPassword(encodedPassword);
        
        // 6. Campos opcionales
        professor.setCel(request.getCel());
        
        // Asignar imagen por defecto si no se proporciona una
        if (request.getPhotoUrl() == null || request.getPhotoUrl().trim().isEmpty()) {
            professor.setPhotoUrl("/assets/default-profile.svg");
        } else {
            professor.setPhotoUrl(request.getPhotoUrl());
        }
        
        // 7. Asignar rol (siempre ADMIN por validación)
        professor.setRole(Role.ADMIN);
        
        // 8. IMPORTANTE: Usuario creado por admin está verificado automáticamente
        professor.setEmailVerified(true); // Ya verificado
        professor.setVerificationToken(null); // No necesita token
        professor.setTokenExpiryDate(null); // No hay expiración
        
        // 9. Guardar en la base de datos
        Professor savedProfessor = professorRepository.save(professor);
        
        // 10. NO enviar email de verificación (creado por admin)
        System.out.println("✓ Administrador creado por administrador: " + savedProfessor.getEmail());
        System.out.println("✓ Email verificado automáticamente: true");
        System.out.println("✓ Rol asignado: " + savedProfessor.getRole());
        
        // 11. Convertir a DTO (sin password) y retornar
        return convertToDTO(savedProfessor);
    }
    
    @Override
    public LoginResponse login(String email, String password) {
        try {
            System.out.println("=== LOGIN ATTEMPT ===");
            System.out.println("Email: " + email);
            
            // 1. Buscar profesor por email
            Professor professor = professorRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        System.out.println("❌ Profesor no encontrado con email: " + email);
                        return new IllegalArgumentException("Credenciales inválidas");
                    });
            
            System.out.println("✓ Profesor encontrado: ID=" + professor.getId() + ", Email=" + professor.getEmail());
            System.out.println("Email verificado: " + professor.getEmailVerified());
            
            // 2. Verificar contraseña
            if (!passwordEncoder.matches(password, professor.getPassword())) {
                System.out.println("❌ Contraseña incorrecta");
                throw new IllegalArgumentException("Credenciales inválidas");
            }
            
            System.out.println("✓ Contraseña correcta");
            
            // 3. Verificar que el email esté verificado
            if (professor.getEmailVerified() == null || !professor.getEmailVerified()) {
                System.out.println("❌ Email no verificado. EmailVerified=" + professor.getEmailVerified());
                throw new IllegalArgumentException("Por favor, verifica tu email antes de iniciar sesión. Revisa tu bandeja de entrada.");
            }
            
            System.out.println("✓ Email verificado");
            
            // 4. Generar token JWT con el rol del profesor
            String role = professor.getRole() != null ? professor.getRole().name() : "PROFESSOR";
            String token = jwtService.generateToken(professor.getId(), professor.getEmail(), role);
            
            // 5. Convertir profesor a DTO
            ProfessorDTO professorDTO = convertToDTO(professor);
            
            // 6. Crear respuesta con token y datos del profesor
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setExpiresIn(jwtService.getExpirationInSeconds());
            response.setProfessor(professorDTO);
            
            System.out.println("✅ Login exitoso para: " + email);
            System.out.println("===============================");
            
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
        
        // 3. SEGURIDAD: Los admins no pueden editar a otros admins
        if (existingProfessor.getRole() == Role.ADMIN && !existingProfessor.getId().equals(currentProfessorId)) {
            throw new IllegalStateException("No se puede editar a otro administrador. Los administradores solo pueden editar su propio perfil.");
        }
        
        // 4. Validar permisos:
        //    - Si es administrador, puede actualizar cualquier PROFESOR (no admin)
        //    - Si no es administrador, solo puede actualizar su propio perfil
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !existingProfessor.getId().equals(currentProfessorId)) {
            throw new IllegalStateException("Solo puedes actualizar tu propio perfil");
        }
        
        // 5. Actualizar los campos permitidos
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
    public List<ProfessorDTO> searchProfessors(String query) {
        System.out.println("=== searchProfessors called ===");
        System.out.println("Query: " + query);
        
        // Solo administradores pueden buscar profesores
        try {
            validateAdminAccess();
            System.out.println("Admin access validated successfully");
        } catch (IllegalStateException e) {
            System.err.println("Admin access validation FAILED: " + e.getMessage());
            throw e;
        }
        
        // Buscar por nombre, apellido o email
        List<Professor> professors = professorRepository
                .findByNameContainingIgnoreCaseOrLastnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    query, query, query);
        System.out.println("Found " + professors.size() + " professors");
        System.out.println("===============================");
        
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
        
        // SEGURIDAD: No se pueden eliminar administradores
        if (professor.getRole() == Role.ADMIN) {
            throw new IllegalStateException("No se puede eliminar a un administrador. Los administradores no pueden ser eliminados del sistema.");
        }
        
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
        
        // Obtener el principal para verificar el rol directamente
        Object principal = authentication.getPrincipal();
        boolean isAdmin = false;
        
        // Verificar directamente desde el ProfessorPrincipal (más confiable)
        if (principal instanceof ProfessorPrincipal) {
            ProfessorPrincipal professorPrincipal = (ProfessorPrincipal) principal;
            Professor professor = professorPrincipal.getProfessor();
            
            System.out.println("=== validateAdminAccess DEBUG ===");
            System.out.println("Professor ID: " + professor.getId());
            System.out.println("Professor Email: " + professor.getEmail());
            System.out.println("Professor Role object: " + professor.getRole());
            System.out.println("Professor Role name: " + (professor.getRole() != null ? professor.getRole().name() : "null"));
            System.out.println("Role equals ADMIN: " + (professor.getRole() != null && professor.getRole().name().equals("ADMIN")));
            System.out.println("Role == Role.ADMIN: " + (professor.getRole() == com.gestion.docente.backend.Gestion.Docente.Backend.model.Role.ADMIN));
            
            // Verificar si el profesor tiene rol ADMIN (múltiples formas)
            if (professor.getRole() != null) {
                if (professor.getRole().name().equals("ADMIN") || 
                    professor.getRole() == com.gestion.docente.backend.Gestion.Docente.Backend.model.Role.ADMIN) {
                    isAdmin = true;
                    System.out.println("✓ Admin access granted via ProfessorPrincipal");
                }
            }
            System.out.println("isAdmin after principal check: " + isAdmin);
        }
        
        // Si no se encontró por el principal, verificar las autoridades como fallback
        if (!isAdmin) {
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> {
                        String authorityStr = authority.getAuthority();
                        // Aceptar tanto "ROLE_ADMIN" como "ADMIN"
                        return authorityStr.equals("ROLE_ADMIN") || authorityStr.equals("ADMIN");
                    });
        }
        
        if (!isAdmin) {
            // Log detallado para debugging
            System.err.println("=== ❌ ADMIN ACCESS DENIED ===");
            System.err.println("Principal type: " + (principal != null ? principal.getClass().getName() : "null"));
            System.err.println("Is authenticated: " + authentication.isAuthenticated());
            System.err.println("Authorities: " + authentication.getAuthorities());
            if (principal instanceof ProfessorPrincipal) {
                ProfessorPrincipal pp = (ProfessorPrincipal) principal;
                Professor prof = pp.getProfessor();
                System.err.println("Professor ID: " + prof.getId());
                System.err.println("Professor Email: " + prof.getEmail());
                System.err.println("Professor Role object: " + prof.getRole());
                System.err.println("Professor Role name: " + (prof.getRole() != null ? prof.getRole().name() : "null"));
                System.err.println("Is Role.ADMIN? " + (prof.getRole() == com.gestion.docente.backend.Gestion.Docente.Backend.model.Role.ADMIN));
            }
            System.err.println("=================================");
            throw new IllegalStateException("Solo los administradores pueden realizar esta acción");
        }
        
        System.out.println("✓ Admin access granted - user is ADMIN");
    }
    
    @Override
    public boolean verifyEmail(String token) {
        // 1. Buscar profesor por token
        Professor professor = professorRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de verificación inválido"));
        
        // 2. Verificar que el token no haya expirado
        if (professor.getTokenExpiryDate() == null || 
            professor.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token de verificación ha expirado. Por favor, solicita uno nuevo.");
        }
        
        // 3. Verificar que el email no esté ya verificado
        if (professor.getEmailVerified() != null && professor.getEmailVerified()) {
            throw new IllegalArgumentException("El email ya ha sido verificado anteriormente");
        }
        
        // 4. Marcar email como verificado y limpiar token
        professor.setEmailVerified(true);
        professor.setVerificationToken(null);
        professor.setTokenExpiryDate(null);
        
        // 5. Guardar cambios
        professorRepository.save(professor);
        
        return true;
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
        // Asignar imagen por defecto si no tiene foto
        if (professor.getPhotoUrl() == null || professor.getPhotoUrl().trim().isEmpty()) {
            dto.setPhotoUrl("/assets/default-profile.svg");
        } else {
            dto.setPhotoUrl(professor.getPhotoUrl());
        }
        dto.setRole(professor.getRole());
        // NO incluir password por seguridad
        return dto;
    }
}

