package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EmailTemplateDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailTemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/email-templates")
@CrossOrigin(origins = "http://localhost:4200")
public class EmailTemplateController {
    
    @Autowired
    private EmailTemplateService emailTemplateService;
    
    @GetMapping
    public ResponseEntity<List<EmailTemplateDTO>> getEmailTemplates(@RequestParam(required = false) Boolean global) {
        try {
            List<EmailTemplateDTO> templates = emailTemplateService.getEmailTemplates(global);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmailTemplateById(@PathVariable Long id) {
        try {
            EmailTemplateDTO template = emailTemplateService.getEmailTemplateById(id);
            return ResponseEntity.ok(template);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createEmailTemplate(@Valid @RequestBody EmailTemplateDTO templateDTO) {
        try {
            EmailTemplateDTO created = emailTemplateService.createEmailTemplate(templateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmailTemplate(
            @PathVariable Long id,
            @Valid @RequestBody EmailTemplateDTO templateDTO) {
        try {
            EmailTemplateDTO updated = emailTemplateService.updateEmailTemplate(id, templateDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmailTemplate(@PathVariable Long id) {
        try {
            emailTemplateService.deleteEmailTemplate(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

