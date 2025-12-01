package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CourseScheduleDTO;

import java.util.List;

public interface CourseScheduleService {
    
    List<CourseScheduleDTO> getSchedulesByCourse(Long courseId);
    
    List<CourseScheduleDTO> createSchedules(Long courseId, List<CourseScheduleDTO> schedules);
    
    void deleteSchedule(Long scheduleId);
    
    void deleteAllSchedulesByCourse(Long courseId);
    
    /**
     * Obtiene el ID del curso que corresponde al horario actual.
     * Método legacy mantenido para compatibilidad.
     */
    Long getCurrentScheduleCourseId(Long professorId);
    
    /**
     * Obtiene el curso y materia que corresponde al horario actual.
     * Retorna un objeto con courseId y subjectId, o null si no hay horario activo.
     * No considera cursos archivados.
     */
    java.util.Map<String, Long> getCurrentScheduleCourseAndSubject(Long professorId);
}

