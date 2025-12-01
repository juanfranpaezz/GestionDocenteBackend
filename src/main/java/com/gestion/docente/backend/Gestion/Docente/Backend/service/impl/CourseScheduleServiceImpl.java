package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CourseScheduleDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.CourseSchedule;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseScheduleRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.CourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseScheduleServiceImpl implements CourseScheduleService {
    
    @Autowired
    private CourseScheduleRepository courseScheduleRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public List<CourseScheduleDTO> getSchedulesByCourse(Long courseId) {
        validateCourseOwnership(courseId);
        
        List<CourseSchedule> schedules = courseScheduleRepository.findByCourseId(courseId);
        return schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CourseScheduleDTO> createSchedules(Long courseId, List<CourseScheduleDTO> scheduleDTOs) {
        validateCourseOwnership(courseId);
        
        // Validar que no haya solapamientos
        for (CourseScheduleDTO dto : scheduleDTOs) {
            validateNoOverlap(courseId, dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime(), null);
        }
        
        List<CourseSchedule> schedules = scheduleDTOs.stream()
                .map(dto -> {
                    CourseSchedule schedule = convertToEntity(dto);
                    schedule.setCourseId(courseId); // Asegurar que courseId esté asignado
                    // subjectId ya viene del DTO, no sobrescribirlo
                    return schedule;
                })
                .collect(Collectors.toList());
        
        List<CourseSchedule> saved = courseScheduleRepository.saveAll(schedules);
        courseScheduleRepository.flush(); // Forzar flush para asegurar persistencia
        return saved.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteSchedule(Long scheduleId) {
        CourseSchedule schedule = courseScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("El horario con ID " + scheduleId + " no existe"));
        
        validateCourseOwnership(schedule.getCourseId());
        courseScheduleRepository.deleteById(scheduleId);
    }
    
    @Override
    public void deleteAllSchedulesByCourse(Long courseId) {
        validateCourseOwnership(courseId);
        courseScheduleRepository.deleteByCourseId(courseId);
    }
    
    @Override
    public Long getCurrentScheduleCourseId(Long professorId) {
        // Mantener método legacy para compatibilidad
        java.util.Map<String, Long> result = getCurrentScheduleCourseAndSubject(professorId);
        return result != null ? result.get("courseId") : null;
    }
    
    @Override
    public java.util.Map<String, Long> getCurrentScheduleCourseAndSubject(Long professorId) {
        List<Course> courses = courseRepository.findByProfessorId(professorId);
        java.time.DayOfWeek currentDay = java.time.DayOfWeek.from(java.time.LocalDate.now());
        java.time.LocalTime currentTime = java.time.LocalTime.now();
        
        // Buscar el primer curso con horario activo (no considerar cursos archivados)
        for (Course course : courses) {
            if (course.getArchived() != null && course.getArchived()) {
                continue; // Ignorar cursos archivados
            }
            
            List<CourseSchedule> schedules = courseScheduleRepository.findByCourseId(course.getId());
            for (CourseSchedule schedule : schedules) {
                if (schedule.getDayOfWeek() == currentDay) {
                    if (!currentTime.isBefore(schedule.getStartTime()) && 
                        !currentTime.isAfter(schedule.getEndTime())) {
                        // Encontró un horario activo, retornar curso y materia
                        java.util.Map<String, Long> result = new java.util.HashMap<>();
                        result.put("courseId", course.getId());
                        result.put("subjectId", schedule.getSubjectId()); // Puede ser null
                        return result;
                    }
                }
            }
        }
        
        return null; // No hay horario activo
    }
    
    private void validateNoOverlap(Long courseId, DayOfWeek day, LocalTime start, LocalTime end, Long excludeScheduleId) {
        List<CourseSchedule> existing = courseScheduleRepository.findByCourseId(courseId);
        
        for (CourseSchedule schedule : existing) {
            if (excludeScheduleId != null && schedule.getId().equals(excludeScheduleId)) {
                continue;
            }
            
            if (schedule.getDayOfWeek() == day) {
                // Verificar solapamiento
                if ((start.isBefore(schedule.getEndTime()) && end.isAfter(schedule.getStartTime()))) {
                    throw new IllegalArgumentException(
                        "El horario se solapa con otro horario existente: " + 
                        schedule.getDayOfWeek() + " " + schedule.getStartTime() + "-" + schedule.getEndTime()
                    );
                }
            }
        }
    }
    
    private CourseScheduleDTO convertToDTO(CourseSchedule schedule) {
        CourseScheduleDTO dto = new CourseScheduleDTO();
        dto.setId(schedule.getId());
        dto.setCourseId(schedule.getCourseId());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setSubjectId(schedule.getSubjectId()); // Agregar subjectId
        return dto;
    }
    
    private CourseSchedule convertToEntity(CourseScheduleDTO dto) {
        CourseSchedule schedule = new CourseSchedule();
        schedule.setId(dto.getId());
        schedule.setCourseId(dto.getCourseId());
        schedule.setDayOfWeek(dto.getDayOfWeek());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setSubjectId(dto.getSubjectId()); // Agregar subjectId
        return schedule;
    }
    
    private void validateCourseOwnership(Long courseId) {
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + courseId + " no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene acceso a este curso");
        }
    }
}

