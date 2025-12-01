package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.AttendanceDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    
    @Autowired
    private AttendanceService attendanceService;
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByCourse(@PathVariable Long courseId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendancesByCourse(courseId);
        return ResponseEntity.ok(attendances);
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByStudent(@PathVariable Long studentId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendancesByStudent(studentId);
        return ResponseEntity.ok(attendances);
    }
    
    @PostMapping
    public ResponseEntity<AttendanceDTO> markAttendance(@RequestBody AttendanceDTO attendanceDTO) {
        AttendanceDTO created = attendanceService.markAttendance(attendanceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(
            @PathVariable Long id,
            @RequestBody AttendanceDTO attendanceDTO) {
        AttendanceDTO updated = attendanceService.updateAttendance(id, attendanceDTO);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/student/{studentId}/course/{courseId}/percentage")
    public ResponseEntity<Double> getAttendancePercentage(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        Double percentage = attendanceService.calculateAttendancePercentage(studentId, courseId);
        return ResponseEntity.ok(percentage);
    }
    
    @GetMapping("/course/{courseId}/averages")
    public ResponseEntity<?> getAttendanceAverages(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long subjectId) {
        try {
            List<com.gestion.docente.backend.Gestion.Docente.Backend.dto.AttendanceAverageDTO> averages = 
                attendanceService.getAttendanceAverages(courseId, subjectId);
            return ResponseEntity.ok(averages);
        } catch (IllegalArgumentException e) {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<?> saveAttendancesBulk(@RequestBody List<AttendanceDTO> attendances) {
        try {
            List<AttendanceDTO> saved = attendanceService.saveAttendancesBulk(attendances);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

