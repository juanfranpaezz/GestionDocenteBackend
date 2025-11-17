package com.gestion.docente.backend.Gestion.Docente.Backend.config;

import com.gestion.docente.backend.Gestion.Docente.Backend.security.JwtAuthenticationEntryPoint;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para el backend con JWT.
 * 
 * Endpoints públicos:
 * - POST /api/auth/register
 * - POST /api/auth/login
 * 
 * Todos los demás endpoints requieren autenticación JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    /**
     * Bean para encriptar contraseñas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configuración de seguridad HTTP con JWT.
     * 
     * - Deshabilita CSRF (no necesario para APIs stateless con JWT)
     * - Configura sesiones como STATELESS (no usa sesiones HTTP)
     * - Permite acceso público solo a /api/auth/register y /api/auth/login
     * - Requiere autenticación para todos los demás endpoints
     * - Agrega el filtro JWT antes del filtro de autenticación por defecto
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No usar sesiones HTTP
            )
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                // Todos los demás endpoints requieren autenticación (incluyendo /api/auth/logout y /api/auth/me)
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // Manejar errores de autenticación
            )
            // Agregar el filtro JWT antes del filtro de autenticación por defecto
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

