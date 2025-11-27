package com.gestion.docente.backend.Gestion.Docente.Backend.security;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.ProfessorRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro JWT que se ejecuta en cada request.
 * 
 * Este filtro:
 * 1. Extrae el token JWT del header Authorization
 * 2. Valida el token
 * 3. Carga los datos del profesor desde la BD
 * 4. Establece la autenticación en el SecurityContext
 * 
 * Solo procesa requests que incluyan un token válido.
 * Si no hay token o es inválido, deja pasar el request (otras configuraciones de seguridad lo manejarán).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            System.out.println("=== JWT Filter - Request: " + request.getRequestURI() + " ===");
            System.out.println("Token extracted: " + (token != null ? "YES (length: " + token.length() + ")" : "NO"));
            System.out.println("Authorization header: " + request.getHeader("Authorization"));
            
            if (token != null && jwtService.validateToken(token)) {
                System.out.println("Token is valid");
                // Extraer información del token
                Long professorId = jwtService.extractProfessorId(token);
                String email = jwtService.extractEmail(token);
                
                // Cargar el profesor desde la BD
                Professor professor = professorRepository.findById(professorId)
                        .orElse(null);
                
                if (professor != null && professor.getEmail().equals(email)) {
                    // Log para debugging
                    System.out.println("=== JWT Filter DEBUG ===");
                    System.out.println("Professor ID: " + professor.getId());
                    System.out.println("Professor Email: " + professor.getEmail());
                    System.out.println("Professor Role: " + (professor.getRole() != null ? professor.getRole().name() : "null"));
                    
                    // Crear autenticación y establecerla en el SecurityContext
                    Authentication authentication = createAuthentication(professor, request);
                    System.out.println("Authentication Authorities: " + authentication.getAuthorities());
                    System.out.println("Authentication set in SecurityContext: YES");
                    System.out.println("=========================");
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.err.println("❌ JWT Filter - Professor not found or email mismatch");
                    System.err.println("Professor from DB: " + (professor != null ? professor.getEmail() : "null"));
                    System.err.println("Email from token: " + email);
                }
            } else {
                if (token == null) {
                    System.err.println("❌ JWT Filter - No token in request");
                } else {
                    System.err.println("❌ JWT Filter - Token is invalid or expired");
                }
            }
        } catch (Exception e) {
            // Si hay algún error al procesar el token, loguear el error completo
            System.err.println("❌❌❌ EXCEPTION en JWT Filter ❌❌❌");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=====================================");
            // El SecurityContext quedará sin autenticación y Spring Security
            // rechazará el request si requiere autenticación
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extrae el token JWT del header Authorization.
     * 
     * @param request HttpServletRequest
     * @return Token JWT o null si no se encuentra
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
    
    /**
     * Crea un objeto Authentication para el profesor autenticado.
     */
    private Authentication createAuthentication(Professor professor, HttpServletRequest request) {
        // Crear el principal (objeto que representa al usuario autenticado)
        ProfessorPrincipal principal = new ProfessorPrincipal(professor);
        
        // Crear authorities (roles) basado en el rol del profesor
        // El ProfessorPrincipal ya maneja esto en getAuthorities(), pero lo establecemos aquí también
        var authorities = principal.getAuthorities();
        
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        
        return authentication;
    }
}

