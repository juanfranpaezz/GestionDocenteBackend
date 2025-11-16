package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import org.springframework.core.io.ByteArrayResource;

public interface ExcelService {
    
    ByteArrayResource generateGradesExcel(Long courseId);
}

