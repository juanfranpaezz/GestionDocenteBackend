package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.strategy;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Subject;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.SubjectRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.CourseCopyStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Estrategia para crear una materia default (sin nombre) cuando no se copian subjects.
 * Esto es necesario porque las evaluaciones requieren al menos una materia.
 */
@Component
public class CreateDefaultSubjectStrategy implements CourseCopyStrategy {
    
    /**
     * Constante especial para identificar materias sin nombre en el mapeo.
     * Usamos -1L en lugar de null para evitar problemas con claves null en Map.
     */
    private static final Long DEFAULT_SUBJECT_KEY = -1L;
    
    private final SubjectRepository subjectRepository;
    
    public CreateDefaultSubjectStrategy(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
    
    @Override
    public void copy(Long originalCourseId, Long newCourseId, Map<String, Map<Long, Long>> idMappings) {
        Subject defaultSubject = new Subject();
        defaultSubject.setName(null);
        defaultSubject.setCourseId(newCourseId);
        Subject savedDefaultSubject = subjectRepository.save(defaultSubject);
        
        if (savedDefaultSubject.getId() != null) {
            Map<Long, Long> subjectIdMap = new HashMap<>();
            // Usar constante especial en lugar de null para evitar problemas con claves null
            subjectIdMap.put(DEFAULT_SUBJECT_KEY, savedDefaultSubject.getId());
            idMappings.put("subjects", subjectIdMap);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "CreateDefaultSubject";
    }
}

