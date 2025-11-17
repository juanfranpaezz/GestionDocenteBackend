package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para generar y validar tokens JWT.
 * 
 * Este servicio maneja:
 * - Generación de tokens con información del profesor (id, email, role)
 * - Validación de tokens (firma, expiración)
 * - Extracción de claims del token
 * 
 * Preparado para futura extensión con refresh tokens.
 */
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration; // en segundos
    
    /**
     * Obtiene la clave secreta para firmar los tokens.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Genera un token JWT para un profesor.
     * 
     * @param professorId ID del profesor
     * @param email Email del profesor
     * @return Token JWT como string
     */
    public String generateToken(Long professorId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("professorId", professorId);
        claims.put("email", email);
        claims.put("role", "PROFESSOR"); // Preparado para futuro uso de roles
        
        return createToken(claims, email);
    }
    
    /**
     * Crea un token JWT con los claims especificados.
     * 
     * @param claims Claims a incluir en el token
     * @param subject Subject del token (normalmente el email)
     * @return Token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Extrae todos los claims de un token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Extrae un claim específico del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extrae el email (subject) del token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extrae el ID del profesor del token.
     */
    public Long extractProfessorId(String token) {
        Claims claims = extractAllClaims(token);
        Object professorIdObj = claims.get("professorId");
        if (professorIdObj instanceof Integer) {
            return ((Integer) professorIdObj).longValue();
        } else if (professorIdObj instanceof Long) {
            return (Long) professorIdObj;
        }
        throw new IllegalArgumentException("professorId no encontrado o formato inválido en el token");
    }
    
    /**
     * Extrae la fecha de expiración del token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Verifica si un token ha expirado.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Valida un token JWT.
     * Verifica que el token no haya expirado y que la firma sea válida.
     * 
     * @param token Token a validar
     * @return true si el token es válido, false en caso contrario
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token); // Esto valida la firma
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtiene el tiempo de expiración en segundos.
     */
    public Long getExpirationInSeconds() {
        return expiration;
    }
}

