package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.strategy;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Subject;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.SubjectRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.CourseCopyStrategy;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Estrategia para copiar Subjects (materias) de un curso a otro.
 */
@Component
public class CopySubjectsStrategy implements CourseCopyStrategy {
    
    private final SubjectRepository subjectRepository;
    
    public CopySubjectsStrategy(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
    
    @Override
    public void copy(Long originalCourseId, Long newCourseId, Map<String, Map<Long, Long>> idMappings) {
        List<Subject> originalSubjects = subjectRepository.findByCourseId(originalCourseId);
        
        List<Subject> newSubjects = originalSubjects.stream()
            .map(original -> {
                Subject newSubject = new Subject();
                newSubject.setName(original.getName());
                newSubject.setCourseId(newCourseId);
                return newSubject;
            })
            .collect(Collectors.toList());
        
        List<Subject> savedSubjects = subjectRepository.saveAll(newSubjects);
        
        // Crear mapeo de IDs usando EntityMapper
        Map<Long, Long> mapping = EntityMapper.createIdMapping(
                originalSubjects,
                savedSubjects,
                Subject::getId
        );
        
        // Almacenar en el mapa de mapeos con la clave correspondiente
        idMappings.put("subjects", mapping);
    }
    
    @Override
    public String getStrategyName() {
        return "CopySubjects";
    }
}

