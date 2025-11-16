package com.gestion.docente.backend.Gestion.Docente.Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el backend.
 * Por ahora, permite todas las peticiones sin autenticación (desarrollo).
 * Configura BCryptPasswordEncoder para encriptar contraseñas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Bean para encriptar contraseñas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configuración de seguridad HTTP.
     * Por ahora permite todas las peticiones sin autenticación.
     * Más adelante se implementará JWT.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para desarrollo
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permitir todas las peticiones sin autenticación
            );
        
        return http.build();
    }
}

