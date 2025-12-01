package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EmailTemplateDTO;

import java.util.List;

public interface EmailTemplateService {
    
    List<EmailTemplateDTO> getEmailTemplates(Boolean includeGlobal);
    
    EmailTemplateDTO createEmailTemplate(EmailTemplateDTO templateDTO);
    
    EmailTemplateDTO updateEmailTemplate(Long id, EmailTemplateDTO templateDTO);
    
    void deleteEmailTemplate(Long id);
    
    EmailTemplateDTO getEmailTemplateById(Long id);
}

