package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailService;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailTemplateService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.base-url:http://localhost:4200}")
    private String appBaseUrl;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Autowired
    private EmailTemplateService emailTemplateService;
    
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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Verifica tu cuenta - Gestión Docente");
            
            String verificationUrl = appBaseUrl + "/auth/verify-email?token=" + verificationToken;
            
            StringBuilder body = new StringBuilder();
            body.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            body.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
            body.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
            body.append(".header{background-color:#1976d2;color:white;padding:20px;text-align:center;border-radius:8px 8px 0 0;}");
            body.append(".content{padding:20px;background-color:#f9f9f9;}");
            body.append(".button-container{text-align:center;margin:25px 0;}");
            body.append(".verify-button{display:inline-block;padding:12px 30px;background-color:#1976d2;color:white;text-decoration:none;border-radius:6px;font-weight:bold;font-size:16px;}");
            body.append(".verify-button:hover{background-color:#1565c0;}");
            body.append(".info{margin:15px 0;padding:15px;background:white;border-left:4px solid #1976d2;border-radius:4px;}");
            body.append(".footer{padding:20px;background-color:#e0e0e0;font-size:12px;color:#666;border-radius:0 0 8px 8px;}");
            body.append(".url-text{word-break:break-all;color:#666;font-size:11px;margin-top:10px;}");
            body.append("</style></head><body>");
            body.append("<div class='container'>");
            body.append("<div class='header'><h1>Gestión Docente</h1></div>");
            body.append("<div class='content'>");
            body.append("<h2>Hola ").append(name != null ? name : "").append("</h2>");
            body.append("<p>Gracias por registrarte en <strong>Gestión Docente</strong>.</p>");
            body.append("<p>Para completar tu registro y activar tu cuenta, por favor verifica tu dirección de email haciendo clic en el siguiente botón:</p>");
            body.append("<div class='button-container'>");
            body.append("<a href='").append(verificationUrl).append("' class='verify-button' style='color:white;text-decoration:none;'>Verificar mi cuenta</a>");
            body.append("</div>");
            body.append("<div class='info'>");
            body.append("<p><strong>¿El botón no funciona?</strong></p>");
            body.append("<p>Copia y pega el siguiente enlace en tu navegador:</p>");
            body.append("<p class='url-text'>").append(verificationUrl).append("</p>");
            body.append("</div>");
            body.append("<p><strong>Importante:</strong> Este enlace expirará en 24 horas.</p>");
            body.append("<p>Si no creaste esta cuenta, puedes ignorar este email de forma segura.</p>");
            body.append("</div>");
            body.append("<div class='footer'>");
            body.append("<p><strong>Equipo de Gestión Docente</strong></p>");
            body.append("<p>Este es un email automático. Por favor, no respondas directamente a este correo.</p>");
            body.append("</div>");
            body.append("</div></body></html>");
            
            helper.setText(body.toString(), true); // true = HTML
            
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
    
    @Override
    public void sendEvaluationGradeEmailWithTemplate(String studentEmail, String studentName, String evaluationName,
                                                      String courseName, Double grade, String gradeValue,
                                                      Long templateId, String customMessage, String professorEmail, String professorName) {
        try {
            if (studentEmail == null || studentEmail.trim().isEmpty()) {
                System.err.println("No se puede enviar email: el estudiante no tiene email configurado");
                return;
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(studentEmail);
            
            String subject;
            String body;
            
            // Si hay template, usarlo
            if (templateId != null) {
                com.gestion.docente.backend.Gestion.Docente.Backend.dto.EmailTemplateDTO template = 
                    emailTemplateService.getEmailTemplateById(templateId);
                
                subject = replaceVariables(template.getSubject(), studentName, courseName, evaluationName, 
                                          grade, gradeValue, professorEmail, professorName);
                body = replaceVariables(template.getBody(), studentName, courseName, evaluationName, 
                                      grade, gradeValue, professorEmail, professorName);
            } else {
                // Template por defecto
                subject = "Nota de " + evaluationName + " - " + courseName;
                body = getDefaultEmailBody(studentName, courseName, evaluationName, grade, gradeValue,
                                          customMessage, professorEmail, professorName);
            }
            
            // Agregar mensaje personalizado si existe
            if (customMessage != null && !customMessage.trim().isEmpty()) {
                body += "\n\n" + customMessage;
            }
            
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML
            
            mailSender.send(message);
            System.out.println("Email de nota enviado a: " + studentEmail);
        } catch (Exception e) {
            System.err.println("Error al enviar email de nota a " + studentEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String replaceVariables(String text, String studentName, String courseName, String evaluationName,
                                   Double grade, String gradeValue, String professorEmail, String professorName) {
        if (text == null) return "";
        
        String gradeText = (grade != null) ? String.format("%.2f", grade) : 
                          (gradeValue != null ? gradeValue : "Sin calificar");
        
        return text
                .replace("{{studentName}}", studentName != null ? studentName : "")
                .replace("{{courseName}}", courseName != null ? courseName : "")
                .replace("{{evaluationName}}", evaluationName != null ? evaluationName : "")
                .replace("{{grade}}", gradeText)
                .replace("{{professorEmail}}", professorEmail != null ? professorEmail : "")
                .replace("{{professorName}}", professorName != null ? professorName : "");
    }
    
    private String getDefaultEmailBody(String studentName, String courseName, String evaluationName,
                                      Double grade, String gradeValue, String customMessage, String professorEmail, String professorName) {
        String gradeText = (grade != null) ? String.format("%.2f", grade) : 
                          (gradeValue != null ? gradeValue : "Sin calificar");
        
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        body.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
        body.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
        body.append(".header{background-color:#1976d2;color:white;padding:20px;text-align:center;}");
        body.append(".content{padding:20px;background-color:#f9f9f9;}");
        body.append(".info{margin:10px 0;}");
        body.append(".footer{padding:20px;background-color:#e0e0e0;font-size:12px;color:#666;}");
        body.append("</style></head><body>");
        body.append("<div class='container'>");
        body.append("<div class='header'><h1>Gestión Docente</h1></div>");
        body.append("<div class='content'>");
        body.append("<h2>Hola ").append(studentName != null ? studentName : "").append("</h2>");
        body.append("<p>Te informamos tu calificación en la evaluación:</p>");
        body.append("<div class='info'><strong>Curso:</strong> ").append(courseName != null ? courseName : "").append("</div>");
        body.append("<div class='info'><strong>Profesor:</strong> ").append(professorName != null ? professorName : "").append("</div>");
        body.append("<div class='info'><strong>Evaluación:</strong> ").append(evaluationName != null ? evaluationName : "").append("</div>");
        body.append("<div class='info'><strong>Nota:</strong> ").append(gradeText).append("</div>");
        body.append("</div>");
        body.append("<div class='footer'>");
        body.append("<p><strong>Importante:</strong></p>");
        body.append("<p>Este es un email automático. Por favor, NO respondas directamente a este correo.</p>");
        body.append("<p>Si necesitas comunicarte, envía un email a: <a href='mailto:").append(professorEmail != null ? professorEmail : "").append("'>").append(professorEmail != null ? professorEmail : "").append("</a></p>");
        body.append("<p>Saludos,<br>Equipo de Gestión Docente</p>");
        body.append("</div>");
        body.append("</div></body></html>");
        
        return body.toString();
    }
    
    @Override
    public void sendPersonalizedMessageToAllStudents(String studentEmail, String studentName, String courseName, 
                                                      String subject, String message, String professorName, String professorEmail) {
        try {
            if (studentEmail == null || studentEmail.trim().isEmpty()) {
                System.err.println("No se puede enviar email: el estudiante no tiene email configurado");
                return;
            }
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(studentEmail);
            helper.setSubject(subject != null && !subject.trim().isEmpty() ? subject : "Mensaje de " + courseName);
            
            StringBuilder body = new StringBuilder();
            body.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            body.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
            body.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
            body.append(".header{background-color:#1976d2;color:white;padding:20px;text-align:center;}");
            body.append(".content{padding:20px;background-color:#f9f9f9;}");
            body.append(".message{margin:15px 0;padding:15px;background:white;border-left:4px solid #1976d2;}");
            body.append(".footer{padding:20px;background-color:#e0e0e0;font-size:12px;color:#666;}");
            body.append("</style></head><body>");
            body.append("<div class='container'>");
            body.append("<div class='header'><h1>Gestión Docente</h1></div>");
            body.append("<div class='content'>");
            body.append("<h2>Hola ").append(studentName != null ? studentName : "").append("</h2>");
            body.append("<p><strong>Curso:</strong> ").append(courseName != null ? courseName : "").append("</p>");
            if (professorName != null && !professorName.trim().isEmpty()) {
                body.append("<p><strong>Profesor:</strong> ").append(professorName).append("</p>");
            }
            body.append("<div class='message'>");
            body.append(message != null ? message.replace("\n", "<br>") : "");
            body.append("</div>");
            body.append("</div>");
            body.append("<div class='footer'>");
            body.append("<p><strong>Importante:</strong></p>");
            body.append("<p>Este es un email automático. Por favor, NO respondas directamente a este correo.</p>");
            body.append("<p>Si necesitas comunicarte, envía un email a: <a href='mailto:").append(professorEmail != null ? professorEmail : "").append("'>").append(professorEmail != null ? professorEmail : "").append("</a></p>");
            body.append("<p>Saludos,<br>Equipo de Gestión Docente</p>");
            body.append("</div>");
            body.append("</div></body></html>");
            
            helper.setText(body.toString(), true); // true = HTML
            
            mailSender.send(mimeMessage);
            System.out.println("Email personalizado enviado a: " + studentEmail);
        } catch (Exception e) {
            System.err.println("Error al enviar email personalizado a " + studentEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

