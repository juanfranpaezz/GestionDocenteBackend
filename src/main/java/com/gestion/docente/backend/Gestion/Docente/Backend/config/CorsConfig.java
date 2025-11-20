package com.gestion.docente.backend.Gestion.Docente.Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración CORS para permitir peticiones desde el frontend Angular.
 * 
 * Permite:
 * - Origen: http://localhost:4200 (frontend Angular)
 * - Métodos: GET, POST, PUT, DELETE, OPTIONS
 * - Headers: Authorization, Content-Type
 * - Credenciales: permitidas
 */
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir origen del frontend
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        
        // Permitir métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Permitir headers necesarios
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        
        // Permitir credenciales (cookies, headers de autenticación)
        configuration.setAllowCredentials(true);
        
        // Exponer headers en la respuesta
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // Cachear configuración preflight por 1 hora
        configuration.setMaxAge(3600L);
        
        // Aplicar configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

