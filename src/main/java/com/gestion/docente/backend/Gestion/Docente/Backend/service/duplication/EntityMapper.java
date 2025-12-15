package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad para mapear IDs entre entidades originales y duplicadas.
 * Facilita el mantenimiento de relaciones entre entidades durante la duplicación.
 */
public class EntityMapper {
    
    /**
     * Crea un mapeo de IDs entre una lista de entidades originales y nuevas.
     * 
     * @param <T> Tipo de entidad
     * @param originalEntities Lista de entidades originales
     * @param newEntities Lista de entidades nuevas
     * @param idExtractor Función para extraer el ID de una entidad
     * @return Mapa de IDs originales a IDs nuevos
     */
    public static <T> Map<Long, Long> createIdMapping(
            List<T> originalEntities,
            List<T> newEntities,
            Function<T, Long> idExtractor) {
        
        Map<Long, Long> idMap = new HashMap<>();
        
        int minSize = Math.min(originalEntities.size(), newEntities.size());
        for (int i = 0; i < minSize; i++) {
            Long originalId = idExtractor.apply(originalEntities.get(i));
            Long newId = idExtractor.apply(newEntities.get(i));
            
            if (originalId != null && newId != null) {
                idMap.put(originalId, newId);
            }
        }
        
        return idMap;
    }
    
    /**
     * Obtiene el ID mapeado o retorna null si no existe.
     * 
     * @param idMap Mapa de IDs
     * @param originalId ID original
     * @return ID mapeado o null
     */
    public static Long getMappedId(Map<Long, Long> idMap, Long originalId) {
        if (originalId == null || idMap == null) {
            return null;
        }
        return idMap.get(originalId);
    }
}

