package com.gestion.docente.backend.Gestion.Docente.Backend.security;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilidades de seguridad para obtener información del usuario autenticado.
 * 
 * Esta clase centraliza el acceso al SecurityContext para evitar duplicación
 * de código en los servicios.
 */
public class SecurityUtils {
    
    /**
     * Obtiene el ID del profesor autenticado desde el SecurityContext.
     * 
     * @return ID del profesor autenticado
     * @throws IllegalStateException si no hay un profesor autenticado
     */
    public static Long getCurrentProfessorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay un profesor autenticado");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof ProfessorPrincipal) {
            return ((ProfessorPrincipal) principal).getId();
        }
        
        throw new IllegalStateException("El usuario autenticado no es un profesor");
    }
    
    /**
     * Obtiene el rol del profesor autenticado desde el SecurityContext.
     * 
     * @return Rol del profesor autenticado (ADMIN o PROFESSOR)
     * @throws IllegalStateException si no hay un profesor autenticado
     */
    public static Role getCurrentProfessorRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay un profesor autenticado");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof ProfessorPrincipal) {
            ProfessorPrincipal professorPrincipal = (ProfessorPrincipal) principal;
            Role role = professorPrincipal.getProfessor().getRole();
            return role != null ? role : Role.PROFESSOR; // Por defecto PROFESSOR
        }
        
        throw new IllegalStateException("El usuario autenticado no es un profesor");
    }
    
    /**
     * Valida que el usuario autenticado NO sea administrador.
     * Los administradores no pueden realizar operaciones relacionadas con cursos.
     * 
     * @throws IllegalStateException si el usuario es administrador
     */
    public static void validateNotAdmin() {
        Role role = getCurrentProfessorRole();
        if (role == Role.ADMIN) {
            throw new IllegalStateException("Los administradores no pueden realizar esta acción. Solo los profesores pueden gestionar cursos.");
        }
    }
}

