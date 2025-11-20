package com.gestion.docente.backend.Gestion.Docente.Backend.security;

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
}

