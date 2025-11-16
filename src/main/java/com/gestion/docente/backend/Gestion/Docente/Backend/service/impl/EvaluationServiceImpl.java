package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EvaluationDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluationServiceImpl implements EvaluationService {
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public List<EvaluationDTO> getEvaluationsByCourse(Long courseId) {
        // Validar que el curso exista
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("El curso con ID " + courseId + " no existe");
        }
        
        List<Evaluation> evaluations = evaluationRepository.findByCourseId(courseId);
        return evaluations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<EvaluationDTO> getEvaluationsByCourse(Long courseId, Pageable pageable) {
        // Validar que el curso exista
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("El curso con ID " + courseId + " no existe");
        }
        
        Page<Evaluation> evaluationsPage = evaluationRepository.findByCourseId(courseId, pageable);
        return evaluationsPage.map(this::convertToDTO);
    }
    
    @Override
    public EvaluationDTO addEvaluation(EvaluationDTO evaluationDTO) {
        // Validar que el curso exista
        if (!courseRepository.existsById(evaluationDTO.getCourseId())) {
            throw new IllegalArgumentException("El curso con ID " + evaluationDTO.getCourseId() + " no existe");
        }
        
        // Convertir DTO a entidad
        Evaluation evaluation = convertToEntity(evaluationDTO);
        
        // Guardar en la base de datos
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        
        // Convertir y retornar DTO
        return convertToDTO(savedEvaluation);
    }
    
    @Override
    public void deleteEvaluation(Long id) {
        if (!evaluationRepository.existsById(id)) {
            throw new IllegalArgumentException("La evaluación con ID " + id + " no existe");
        }
        evaluationRepository.deleteById(id);
    }
    
    // Métodos auxiliares para conversión
    private EvaluationDTO convertToDTO(Evaluation evaluation) {
        EvaluationDTO dto = new EvaluationDTO();
        dto.setId(evaluation.getId());
        dto.setNombre(evaluation.getNombre());
        dto.setDate(evaluation.getDate());
        dto.setTipo(evaluation.getTipo());
        dto.setCourseId(evaluation.getCourseId());
        return dto;
    }
    
    private Evaluation convertToEntity(EvaluationDTO dto) {
        Evaluation evaluation = new Evaluation();
        evaluation.setId(dto.getId()); // null para nuevas evaluaciones
        evaluation.setNombre(dto.getNombre());
        evaluation.setDate(dto.getDate());
        evaluation.setTipo(dto.getTipo());
        evaluation.setCourseId(dto.getCourseId());
        return evaluation;
    }
}

