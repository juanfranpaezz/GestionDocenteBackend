package com.gestion.docente.backend.Gestion.Docente.Backend.security;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementación de UserDetails para representar al profesor autenticado.
 * 
 * Este objeto se almacena en el SecurityContext y permite acceder a los datos
 * del profesor autenticado en cualquier parte de la aplicación.
 */
@Getter
public class ProfessorPrincipal implements UserDetails {
    
    private final Professor professor;
    
    public ProfessorPrincipal(Professor professor) {
        this.professor = professor;
    }
    
    @Override
    public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        String roleName = professor.getRole() != null 
            ? professor.getRole().name() 
            : "PROFESSOR";
        return Collections.singletonList(() -> "ROLE_" + roleName);
    }
    
    @Override
    public String getPassword() {
        return professor.getPassword();
    }
    
    @Override
    public String getUsername() {
        return professor.getEmail();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    /**
     * Obtiene el ID del profesor.
     */
    public Long getId() {
        return professor.getId();
    }
    
    /**
     * Obtiene el email del profesor.
     */
    public String getEmail() {
        return professor.getEmail();
    }
}

