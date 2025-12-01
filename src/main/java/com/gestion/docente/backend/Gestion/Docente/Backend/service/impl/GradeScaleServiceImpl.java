package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeScaleDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeScaleOptionDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.GradeScale;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.GradeScaleOption;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.GradeScaleOptionRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.GradeScaleRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.GradeScaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GradeScaleServiceImpl implements GradeScaleService {
    
    @Autowired
    private GradeScaleRepository gradeScaleRepository;
    
    @Autowired
    private GradeScaleOptionRepository gradeScaleOptionRepository;
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Override
    public List<GradeScaleDTO> getGradeScales(Boolean includeGlobal) {
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        if (Boolean.TRUE.equals(includeGlobal)) {
            return gradeScaleRepository.findByProfessorIdOrIsGlobalTrue(currentProfessorId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            return gradeScaleRepository.findByProfessorId(currentProfessorId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }
    
    @Override
    public GradeScaleDTO createGradeScale(GradeScaleDTO gradeScaleDTO) {
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // Si no es global, asignar al profesor autenticado
        if (!Boolean.TRUE.equals(gradeScaleDTO.getIsGlobal())) {
            gradeScaleDTO.setProfessorId(currentProfessorId);
        } else {
            // Solo admin puede crear escalas globales (validar si es necesario)
            gradeScaleDTO.setProfessorId(null);
        }
        
        GradeScale gradeScale = convertToEntity(gradeScaleDTO);
        GradeScale saved = gradeScaleRepository.save(gradeScale);
        
        // Guardar opciones
        if (gradeScaleDTO.getOptions() != null && !gradeScaleDTO.getOptions().isEmpty()) {
            List<GradeScaleOption> options = gradeScaleDTO.getOptions().stream()
                    .map(optDTO -> {
                        GradeScaleOption option = new GradeScaleOption();
                        option.setGradeScaleId(saved.getId());
                        option.setLabel(optDTO.getLabel());
                        option.setNumericValue(optDTO.getNumericValue());
                        option.setOrderValue(optDTO.getOrder());
                        return option;
                    })
                    .collect(Collectors.toList());
            gradeScaleOptionRepository.saveAll(options);
        }
        
        return convertToDTO(saved);
    }
    
    @Override
    public GradeScaleDTO updateGradeScale(Long id, GradeScaleDTO gradeScaleDTO) {
        GradeScale existing = gradeScaleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La escala con ID " + id + " no existe"));
        
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // Validar ownership (solo el profesor dueño o admin para globales)
        if (!Boolean.TRUE.equals(existing.getIsGlobal()) && 
            !existing.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene permiso para editar esta escala");
        }
        
        existing.setName(gradeScaleDTO.getName());
        existing.setIsGlobal(gradeScaleDTO.getIsGlobal());
        
        if (!Boolean.TRUE.equals(gradeScaleDTO.getIsGlobal())) {
            existing.setProfessorId(currentProfessorId);
        } else {
            existing.setProfessorId(null);
        }
        
        GradeScale saved = gradeScaleRepository.save(existing);
        
        // Eliminar opciones existentes y crear nuevas
        gradeScaleOptionRepository.deleteByGradeScaleId(id);
        
        if (gradeScaleDTO.getOptions() != null && !gradeScaleDTO.getOptions().isEmpty()) {
            List<GradeScaleOption> options = gradeScaleDTO.getOptions().stream()
                    .map(optDTO -> {
                        GradeScaleOption option = new GradeScaleOption();
                        option.setGradeScaleId(saved.getId());
                        option.setLabel(optDTO.getLabel());
                        option.setNumericValue(optDTO.getNumericValue());
                        option.setOrderValue(optDTO.getOrder());
                        return option;
                    })
                    .collect(Collectors.toList());
            gradeScaleOptionRepository.saveAll(options);
        }
        
        return convertToDTO(saved);
    }
    
    @Override
    public void deleteGradeScale(Long id) {
        GradeScale gradeScale = gradeScaleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La escala con ID " + id + " no existe"));
        
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // Validar ownership
        if (!Boolean.TRUE.equals(gradeScale.getIsGlobal()) && 
            !gradeScale.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene permiso para eliminar esta escala");
        }
        
        // Validar que no esté en uso
        List<Evaluation> evaluations = evaluationRepository.findByGradeScaleId(id);
        if (!evaluations.isEmpty()) {
            throw new IllegalArgumentException(
                "No se puede eliminar la escala porque está siendo usada por " + 
                evaluations.size() + " evaluación(es)"
            );
        }
        
        gradeScaleRepository.deleteById(id);
    }
    
    @Override
    public List<GradeScaleDTO> getAvailableGradeScalesForEvaluation(Long evaluationId) {
        // Retornar escalas disponibles (del profesor + globales)
        return getGradeScales(true);
    }
    
    private GradeScaleDTO convertToDTO(GradeScale gradeScale) {
        GradeScaleDTO dto = new GradeScaleDTO();
        dto.setId(gradeScale.getId());
        dto.setName(gradeScale.getName());
        dto.setProfessorId(gradeScale.getProfessorId());
        dto.setIsGlobal(gradeScale.getIsGlobal());
        
        // Cargar opciones
        List<GradeScaleOption> options = gradeScaleOptionRepository.findByGradeScaleIdOrderByOrderValueAsc(gradeScale.getId());
        dto.setOptions(options.stream()
                .map(opt -> {
                    GradeScaleOptionDTO optDTO = new GradeScaleOptionDTO();
                    optDTO.setId(opt.getId());
                    optDTO.setLabel(opt.getLabel());
                    optDTO.setNumericValue(opt.getNumericValue());
                    optDTO.setOrder(opt.getOrderValue());
                    return optDTO;
                })
                .collect(Collectors.toList()));
        
        return dto;
    }
    
    private GradeScale convertToEntity(GradeScaleDTO dto) {
        GradeScale gradeScale = new GradeScale();
        gradeScale.setId(dto.getId());
        gradeScale.setName(dto.getName());
        gradeScale.setProfessorId(dto.getProfessorId());
        gradeScale.setIsGlobal(dto.getIsGlobal());
        return gradeScale;
    }
}

