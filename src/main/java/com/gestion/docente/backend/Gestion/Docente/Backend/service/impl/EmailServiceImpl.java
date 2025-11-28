package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.base-url:http://localhost:4200}")
    private String appBaseUrl;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Override
    public void sendRegistrationEmail(String email, String name) {
        // Implementación existente si la hay
    }
    
    @Override
    public void sendGradesEmail(String studentEmail, String courseName, List<GradeDTO> grades) {
        // Implementación existente si la hay
    }
    
    @Override
    public void sendVerificationEmail(String email, String name, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Verifica tu cuenta - Gestión Docente");
            
            String verificationUrl = appBaseUrl + "/auth/verify-email?token=" + verificationToken;
            
            String emailBody = String.format(
                "Hola %s,\n\n" +
                "Gracias por registrarte en Gestión Docente.\n\n" +
                "Para verificar tu cuenta, por favor haz clic en el siguiente enlace o copia y pega la URL en tu navegador:\n\n" +
                "%s\n\n" +
                "Este enlace expirará en 24 horas.\n\n" +
                "Si no creaste esta cuenta, puedes ignorar este email.\n\n" +
                "Saludos,\n" +
                "Equipo de Gestión Docente",
                name, verificationUrl
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            System.out.println("Email de verificación enviado a: " + email);
        } catch (Exception e) {
            System.err.println("Error al enviar email de verificación: " + e.getMessage());
            e.printStackTrace();
            // No lanzar excepción para no interrumpir el registro
            // En producción, considerar usar un sistema de cola de mensajes
        }
    }
    
    @Override
    public void sendEvaluationGradeEmail(String studentEmail, String studentName, String evaluationName, String courseName, Double grade) {
        try {
            if (studentEmail == null || studentEmail.trim().isEmpty()) {
                System.err.println("No se puede enviar email: el estudiante no tiene email configurado");
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(studentEmail);
            message.setSubject("Nota de " + evaluationName + " - " + courseName);
            
            String gradeText = (grade != null) ? String.format("%.2f", grade) : "Sin calificar";
            
            String emailBody = String.format(
                "Hola %s,\n\n" +
                "Te informamos tu calificación en la evaluación:\n\n" +
                "Curso: %s\n" +
                "Evaluación: %s\n" +
                "Nota: %s\n\n" +
                "Saludos,\n" +
                "Equipo de Gestión Docente",
                studentName, courseName, evaluationName, gradeText
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            System.out.println("Email de nota enviado a: " + studentEmail);
        } catch (Exception e) {
            System.err.println("Error al enviar email de nota a " + studentEmail + ": " + e.getMessage());
            e.printStackTrace();
            // No lanzar excepción para no interrumpir el proceso
        }
    }
}

