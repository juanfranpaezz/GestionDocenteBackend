package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.LoginResponse;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ProfessorDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.RegisterRequest;

public interface ProfessorService {
    
    ProfessorDTO register(RegisterRequest registerRequest);
    
    LoginResponse login(String email, String password);
    
    ProfessorDTO getCurrentProfessor();
    
    ProfessorDTO updateProfessor(Long id, ProfessorDTO professorDTO);
    
    boolean emailExists(String email);
}

