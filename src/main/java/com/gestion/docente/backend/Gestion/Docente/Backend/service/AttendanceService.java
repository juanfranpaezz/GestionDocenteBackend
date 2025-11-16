package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.AttendanceDTO;

import java.util.List;

public interface AttendanceService {
    
    List<AttendanceDTO> getAttendancesByCourse(Long courseId);
    
    List<AttendanceDTO> getAttendancesByStudent(Long studentId);
    
    AttendanceDTO markAttendance(AttendanceDTO attendanceDTO);
    
    AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO);
    
    Double calculateAttendancePercentage(Long studentId, Long courseId);
}

