package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;

import java.util.List;

public interface EmailService {
    
    void sendRegistrationEmail(String email, String name);
    
    void sendGradesEmail(String studentEmail, String courseName, List<GradeDTO> grades);
}

