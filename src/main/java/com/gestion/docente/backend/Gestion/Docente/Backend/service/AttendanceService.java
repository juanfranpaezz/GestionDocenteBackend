package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import java.util.List;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.AttendanceDTO;

public interface AttendanceService {
    
    List<AttendanceDTO> getAttendancesByCourse(Long courseId);
    
    List<AttendanceDTO> getAttendancesByStudent(Long studentId);
    
    AttendanceDTO markAttendance(AttendanceDTO attendanceDTO);
    
    AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO);
    
    Double calculateAttendancePercentage(Long studentId, Long courseId);
    
    List<com.gestion.docente.backend.Gestion.Docente.Backend.dto.AttendanceAverageDTO> getAttendanceAverages(Long courseId, Long subjectId);
    
    List<AttendanceDTO> saveAttendancesBulk(List<AttendanceDTO> attendances);
}

