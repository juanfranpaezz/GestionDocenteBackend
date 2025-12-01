package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EmailTemplateDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.EmailTemplate;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EmailTemplateRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmailTemplateServiceImpl implements EmailTemplateService {
    
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;
    
    @Override
    public List<EmailTemplateDTO> getEmailTemplates(Boolean includeGlobal) {
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        if (Boolean.TRUE.equals(includeGlobal)) {
            return emailTemplateRepository.findByProfessorIdOrIsGlobalTrue(currentProfessorId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            return emailTemplateRepository.findByProfessorId(currentProfessorId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }
    
    @Override
    public EmailTemplateDTO createEmailTemplate(EmailTemplateDTO templateDTO) {
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // Si no es global, asignar al profesor autenticado
        if (!Boolean.TRUE.equals(templateDTO.getIsGlobal())) {
            templateDTO.setProfessorId(currentProfessorId);
        } else {
            // Solo admin puede crear templates globales (validar si es necesario)
            templateDTO.setProfessorId(null);
        }
        
        EmailTemplate template = convertToEntity(templateDTO);
        EmailTemplate saved = emailTemplateRepository.save(template);
        return convertToDTO(saved);
    }
    
    @Override
    public EmailTemplateDTO updateEmailTemplate(Long id, EmailTemplateDTO templateDTO) {
        EmailTemplate existing = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El template con ID " + id + " no existe"));
        
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // Validar ownership
        if (!Boolean.TRUE.equals(existing.getIsGlobal()) && 
            !existing.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene permiso para editar este template");
        }
        
        existing.setName(templateDTO.getName());
        existing.setSubject(templateDTO.getSubject());
        existing.setBody(templateDTO.getBody());
        existing.setIsGlobal(templateDTO.getIsGlobal());
        
        if (!Boolean.TRUE.equals(templateDTO.getIsGlobal())) {
            existing.setProfessorId(currentProfessorId);
        } else {
            existing.setProfessorId(null);
        }
        
        EmailTemplate saved = emailTemplateRepository.save(existing);
        return convertToDTO(saved);
    }
    
    @Override
    public void deleteEmailTemplate(Long id) {
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El template con ID " + id + " no existe"));
        
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // Validar ownership
        if (!Boolean.TRUE.equals(template.getIsGlobal()) && 
            !template.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene permiso para eliminar este template");
        }
        
        emailTemplateRepository.deleteById(id);
    }
    
    @Override
    public EmailTemplateDTO getEmailTemplateById(Long id) {
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El template con ID " + id + " no existe"));
        
        return convertToDTO(template);
    }
    
    private EmailTemplateDTO convertToDTO(EmailTemplate template) {
        EmailTemplateDTO dto = new EmailTemplateDTO();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setSubject(template.getSubject());
        dto.setBody(template.getBody());
        dto.setProfessorId(template.getProfessorId());
        dto.setIsGlobal(template.getIsGlobal());
        return dto;
    }
    
    private EmailTemplate convertToEntity(EmailTemplateDTO dto) {
        EmailTemplate template = new EmailTemplate();
        template.setId(dto.getId());
        template.setName(dto.getName());
        template.setSubject(dto.getSubject());
        template.setBody(dto.getBody());
        template.setProfessorId(dto.getProfessorId());
        template.setIsGlobal(dto.getIsGlobal());
        return template;
    }
}

