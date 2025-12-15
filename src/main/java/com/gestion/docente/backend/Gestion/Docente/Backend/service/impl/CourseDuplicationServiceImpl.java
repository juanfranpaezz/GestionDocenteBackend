package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.DuplicateCourseDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.CourseDuplicationService;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.CourseDuplicationBuilder;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.strategy.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio de duplicación de cursos.
 * Maneja toda la lógica de copia de entidades relacionadas con un curso.
 */
@Service
@Transactional
public class CourseDuplicationServiceImpl implements CourseDuplicationService {
    
    private final CourseRepository courseRepository;
    private final CopySubjectsStrategy copySubjectsStrategy;
    private final CopyStudentsStrategy copyStudentsStrategy;
    private final CopyEvaluationTypesStrategy copyEvaluationTypesStrategy;
    private final CopyEvaluationsStrategy copyEvaluationsStrategy;
    private final CopySchedulesStrategy copySchedulesStrategy;
    private final CopyGradesStrategy copyGradesStrategy;
    private final CreateDefaultSubjectStrategy createDefaultSubjectStrategy;
    
    // Inyección por constructor (Dependency Inversion Principle)
    public CourseDuplicationServiceImpl(
            CourseRepository courseRepository,
            CopySubjectsStrategy copySubjectsStrategy,
            CopyStudentsStrategy copyStudentsStrategy,
            CopyEvaluationTypesStrategy copyEvaluationTypesStrategy,
            CopyEvaluationsStrategy copyEvaluationsStrategy,
            CopySchedulesStrategy copySchedulesStrategy,
            CopyGradesStrategy copyGradesStrategy,
            CreateDefaultSubjectStrategy createDefaultSubjectStrategy) {
        this.courseRepository = courseRepository;
        this.copySubjectsStrategy = copySubjectsStrategy;
        this.copyStudentsStrategy = copyStudentsStrategy;
        this.copyEvaluationTypesStrategy = copyEvaluationTypesStrategy;
        this.copyEvaluationsStrategy = copyEvaluationsStrategy;
        this.copySchedulesStrategy = copySchedulesStrategy;
        this.copyGradesStrategy = copyGradesStrategy;
        this.createDefaultSubjectStrategy = createDefaultSubjectStrategy;
    }
    
    @Override
    public Course duplicateCourse(Course originalCourse, DuplicateCourseDTO options) {
        // Crear nuevo curso usando Builder pattern
        Course savedCourse = new CourseDuplicationBuilder(courseRepository, originalCourse)
                .withDefaultName()
                .copyBasicData()
                .withCurrentProfessor()
                .withArchived(false)
                .build();
        
        // Mapeos de IDs para mantener relaciones (usando Map<String, Map<Long, Long>> para las estrategias)
        Map<String, Map<Long, Long>> idMappings = new HashMap<>();
        
        // Copiar Subjects primero (necesario para evaluaciones) usando Strategy Pattern
        if (Boolean.TRUE.equals(options.getCopySubjects())) {
            copySubjectsStrategy.copy(originalCourse.getId(), savedCourse.getId(), idMappings);
        } else {
            // Si no se copian subjects, crear al menos una materia default
            createDefaultSubjectStrategy.copy(originalCourse.getId(), savedCourse.getId(), idMappings);
        }
        
        // Copiar EvaluationTypes con mapeo usando Strategy Pattern
        if (Boolean.TRUE.equals(options.getCopyEvaluationTypes())) {
            copyEvaluationTypesStrategy.copy(originalCourse.getId(), savedCourse.getId(), idMappings);
        }
        
        // Copiar Students con mapeo usando Strategy Pattern
        if (Boolean.TRUE.equals(options.getCopyStudents())) {
            copyStudentsStrategy.copy(originalCourse.getId(), savedCourse.getId(), idMappings);
        }
        
        // Copiar Evaluations usando los mapeos para mantener relaciones usando Strategy Pattern
        if (Boolean.TRUE.equals(options.getCopyEvaluations())) {
            copyEvaluationsStrategy.copy(originalCourse.getId(), savedCourse.getId(), idMappings);
            
            // Copiar Grades si se solicitó y se copiaron evaluaciones usando Strategy Pattern
            if (Boolean.TRUE.equals(options.getCopyGrades())) {
                Map<Long, Long> evaluationIdMap = idMappings.getOrDefault("evaluations", Map.of());
                if (!evaluationIdMap.isEmpty()) {
                    copyGradesStrategy.copy(originalCourse.getId(), savedCourse.getId(), idMappings);
                }
            }
        }
        
        // Copiar Schedules usando Strategy Pattern
        if (Boolean.TRUE.equals(options.getCopySchedules())) {
            copySchedulesStrategy.copy(originalCourse.getId(), savedCourse.getId(), idMappings);
        }
        
        return savedCourse;
    }
}

