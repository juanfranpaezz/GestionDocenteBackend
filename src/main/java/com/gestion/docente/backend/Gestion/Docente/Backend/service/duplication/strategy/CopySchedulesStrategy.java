package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.strategy;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.CourseSchedule;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseScheduleRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.CourseCopyStrategy;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Estrategia para copiar CourseSchedules (horarios) de un curso a otro.
 * Mantiene las relaciones con Subjects usando los mapeos de IDs.
 */
@Component
public class CopySchedulesStrategy implements CourseCopyStrategy {
    
    private final CourseScheduleRepository courseScheduleRepository;
    
    public CopySchedulesStrategy(CourseScheduleRepository courseScheduleRepository) {
        this.courseScheduleRepository = courseScheduleRepository;
    }
    
    @Override
    public void copy(Long originalCourseId, Long newCourseId, Map<String, Map<Long, Long>> idMappings) {
        List<CourseSchedule> originalSchedules = courseScheduleRepository.findByCourseId(originalCourseId);
        
        Map<Long, Long> subjectIdMap = idMappings.getOrDefault("subjects", Map.of());
        
        List<CourseSchedule> newSchedules = originalSchedules.stream()
            .map(original -> {
                CourseSchedule newSchedule = new CourseSchedule();
                newSchedule.setCourseId(newCourseId);
                newSchedule.setDayOfWeek(original.getDayOfWeek());
                newSchedule.setStartTime(original.getStartTime());
                newSchedule.setEndTime(original.getEndTime());
                
                // Mapear subjectId usando EntityMapper
                newSchedule.setSubjectId(EntityMapper.getMappedId(
                        subjectIdMap, original.getSubjectId()));
                
                return newSchedule;
            })
            .collect(Collectors.toList());
        
        courseScheduleRepository.saveAll(newSchedules);
    }
    
    @Override
    public String getStrategyName() {
        return "CopySchedules";
    }
}

