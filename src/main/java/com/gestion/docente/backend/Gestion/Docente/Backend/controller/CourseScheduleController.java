package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CourseScheduleDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.CourseScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseScheduleController {
    
    @Autowired
    private CourseScheduleService courseScheduleService;
    
    @Autowired
    private com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository courseRepository;
    
    @GetMapping("/{courseId}/schedules")
    public ResponseEntity<List<CourseScheduleDTO>> getSchedulesByCourse(@PathVariable Long courseId) {
        try {
            List<CourseScheduleDTO> schedules = courseScheduleService.getSchedulesByCourse(courseId);
            return ResponseEntity.ok(schedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{courseId}/schedules")
    public ResponseEntity<?> createSchedules(
            @PathVariable Long courseId,
            @Valid @RequestBody List<CourseScheduleDTO> schedules) {
        try {
            List<CourseScheduleDTO> created = courseScheduleService.createSchedules(courseId, schedules);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{courseId}/schedules/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long courseId, @PathVariable Long scheduleId) {
        try {
            courseScheduleService.deleteSchedule(scheduleId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/current-schedule-redirect")
    public ResponseEntity<?> getCurrentScheduleRedirect() {
        try {
            Long currentProfessorId = com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils.getCurrentProfessorId();
            java.util.Map<String, Long> courseAndSubject = courseScheduleService.getCurrentScheduleCourseAndSubject(currentProfessorId);
            
            Map<String, Object> response = new HashMap<>();
            if (courseAndSubject != null) {
                Long courseId = courseAndSubject.get("courseId");
                Long subjectId = courseAndSubject.get("subjectId");
                
                response.put("hasCurrentCourse", true);
                response.put("courseId", courseId);
                response.put("subjectId", subjectId); // Puede ser null
                
                // Obtener nombre del curso
                com.gestion.docente.backend.Gestion.Docente.Backend.model.Course course = 
                    courseRepository.findById(courseId).orElse(null);
                if (course != null) {
                    response.put("courseName", course.getName());
                }
            } else {
                response.put("hasCurrentCourse", false);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

