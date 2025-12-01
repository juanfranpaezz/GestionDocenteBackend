package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EvaluationTypeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.EvaluationType;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationTypeRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EvaluationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluationTypeServiceImpl implements EvaluationTypeService {
    
    @Autowired
    private EvaluationTypeRepository evaluationTypeRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Override
    public List<EvaluationTypeDTO> getEvaluationTypesByCourse(Long courseId) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        List<EvaluationType> types = evaluationTypeRepository.findByCourseId(courseId);
        return types.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public EvaluationTypeDTO createEvaluationType(EvaluationTypeDTO evaluationTypeDTO) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(evaluationTypeDTO.getCourseId());
        
        // Validar que no exista un tipo con el mismo nombre en el mismo curso
        if (evaluationTypeRepository.existsByCourseIdAndNombre(
                evaluationTypeDTO.getCourseId(), 
                evaluationTypeDTO.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe un tipo de evaluación con el nombre '" + evaluationTypeDTO.getNombre() + 
                "' en este curso");
        }
        
        // Convertir DTO a entidad
        EvaluationType evaluationType = convertToEntity(evaluationTypeDTO);
        
        // Guardar en la base de datos
        EvaluationType savedType = evaluationTypeRepository.save(evaluationType);
        
        // Convertir y retornar DTO
        return convertToDTO(savedType);
    }
    
    @Override
    public EvaluationTypeDTO updateEvaluationType(Long id, EvaluationTypeDTO evaluationTypeDTO) {
        // Buscar el tipo existente
        EvaluationType existingType = evaluationTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El tipo de evaluación con ID " + id + " no existe"));
        
        // Validar ownership: el curso del tipo debe pertenecer al profesor autenticado
        validateCourseOwnership(existingType.getCourseId());
        
        // Validar que el courseId no cambie
        if (!existingType.getCourseId().equals(evaluationTypeDTO.getCourseId())) {
            throw new IllegalArgumentException("No se puede cambiar el curso de un tipo de evaluación");
        }
        
        // Validar que no exista otro tipo con el mismo nombre en el mismo curso (excepto el actual)
        if (evaluationTypeRepository.existsByCourseIdAndNombre(
                evaluationTypeDTO.getCourseId(), 
                evaluationTypeDTO.getNombre()) && 
            !existingType.getNombre().equals(evaluationTypeDTO.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe un tipo de evaluación con el nombre '" + evaluationTypeDTO.getNombre() + 
                "' en este curso");
        }
        
        // Actualizar campos
        existingType.setNombre(evaluationTypeDTO.getNombre());
        existingType.setWeight(evaluationTypeDTO.getWeight());
        
        // Guardar cambios
        EvaluationType updatedType = evaluationTypeRepository.save(existingType);
        
        // Convertir y retornar DTO
        return convertToDTO(updatedType);
    }
    
    @Override
    public void deleteEvaluationType(Long id) {
        // Buscar el tipo para obtener su curso
        EvaluationType evaluationType = evaluationTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El tipo de evaluación con ID " + id + " no existe"));
        
        // Validar ownership: el curso del tipo debe pertenecer al profesor autenticado
        validateCourseOwnership(evaluationType.getCourseId());
        
        // Validar que no haya evaluaciones usando este tipo
        List<Evaluation> evaluations = evaluationRepository.findByCourseId(evaluationType.getCourseId());
        boolean hasEvaluations = evaluations.stream()
                .anyMatch(e -> e.getEvaluationTypeId() != null && e.getEvaluationTypeId().equals(id));
        
        if (hasEvaluations) {
            throw new IllegalArgumentException(
                "No se puede eliminar el tipo de evaluación porque hay evaluaciones que lo están usando. " +
                "Primero elimine o cambie el tipo de las evaluaciones asociadas.");
        }
        
        // Eliminar el tipo
        evaluationTypeRepository.deleteById(id);
    }
    
    // Métodos auxiliares para conversión
    private EvaluationTypeDTO convertToDTO(EvaluationType evaluationType) {
        EvaluationTypeDTO dto = new EvaluationTypeDTO();
        dto.setId(evaluationType.getId());
        dto.setNombre(evaluationType.getNombre());
        dto.setCourseId(evaluationType.getCourseId());
        dto.setWeight(evaluationType.getWeight());
        return dto;
    }
    
    private EvaluationType convertToEntity(EvaluationTypeDTO dto) {
        EvaluationType evaluationType = new EvaluationType();
        evaluationType.setId(dto.getId()); // null para nuevos tipos
        evaluationType.setNombre(dto.getNombre());
        evaluationType.setCourseId(dto.getCourseId());
        evaluationType.setWeight(dto.getWeight());
        return evaluationType;
    }
    
    /**
     * Valida que un curso pertenezca al profesor autenticado.
     * 
     * @param courseId ID del curso a validar
     * @throws IllegalArgumentException si el curso no existe o no pertenece al profesor autenticado
     */
    private void validateCourseOwnership(Long courseId) {
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + courseId + " no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene acceso a este curso");
        }
    }
}

