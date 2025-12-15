package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication;

import java.util.Map;

/**
 * Interfaz Strategy para diferentes estrategias de copia de entidades relacionadas con un curso.
 * Aplica el patrón Strategy (GOF) para permitir diferentes algoritmos de copia.
 */
public interface CourseCopyStrategy {
    
    /**
     * Copia las entidades relacionadas desde el curso original al nuevo curso.
     * 
     * @param originalCourseId ID del curso original
     * @param newCourseId ID del nuevo curso
     * @param idMappings Mapa para almacenar mapeos de IDs entre entidades originales y nuevas
     */
    void copy(Long originalCourseId, Long newCourseId, Map<String, Map<Long, Long>> idMappings);
    
    /**
     * Obtiene el nombre de la estrategia para identificación.
     * 
     * @return Nombre de la estrategia
     */
    String getStrategyName();
}

