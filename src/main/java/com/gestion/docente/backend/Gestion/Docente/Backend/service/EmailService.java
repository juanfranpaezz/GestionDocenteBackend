package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;

import java.util.List;

public interface EmailService {
    
    void sendRegistrationEmail(String email, String name);
    
    void sendGradesEmail(String studentEmail, String courseName, List<GradeDTO> grades);
    
    void sendVerificationEmail(String email, String name, String verificationToken);
    
    void sendEvaluationGradeEmail(String studentEmail, String studentName, String evaluationName, String courseName, Double grade);
    
    void sendEvaluationGradeEmailWithTemplate(String studentEmail, String studentName, String evaluationName, 
                                               String courseName, Double grade, String gradeValue, 
                                               Long templateId, String customMessage, String professorEmail, String professorName);
    
    void sendPersonalizedMessageToAllStudents(String studentEmail, String studentName, String courseName, 
                                               String subject, String message, String professorName, String professorEmail);
}

