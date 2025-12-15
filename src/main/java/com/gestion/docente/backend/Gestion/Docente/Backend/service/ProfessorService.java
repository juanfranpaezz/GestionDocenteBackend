package com.gestion.docente.backend.Gestion.Docente.Backend.service;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CreateProfessorByAdminRequest;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.LoginResponse;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ProfessorDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.RegisterRequest;

import java.util.List;

public interface ProfessorService {
    
    ProfessorDTO register(RegisterRequest registerRequest);
    
    ProfessorDTO createByAdmin(CreateProfessorByAdminRequest request);
    
    LoginResponse login(String email, String password);
    
    ProfessorDTO getCurrentProfessor();
    
    ProfessorDTO updateProfessor(Long id, ProfessorDTO professorDTO);
    
    boolean emailExists(String email);
    
    List<ProfessorDTO> getAllProfessors();
    
    ProfessorDTO getProfessorById(Long id);
    
    List<ProfessorDTO> searchProfessorsByLastname(String lastname);
    
    List<ProfessorDTO> searchProfessors(String query);
    
    void deleteProfessor(Long id);
    
    boolean verifyEmail(String token);
}

