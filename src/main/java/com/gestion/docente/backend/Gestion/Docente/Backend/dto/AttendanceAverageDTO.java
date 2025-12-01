package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceAverageDTO {
    private Long studentId;
    
    private String firstName;
    
    private String lastName;
    
    private Double attendancePercentage;
    
    private Integer totalDays;
    
    private Integer presentDays;
    
    private Integer absentDays;
}

