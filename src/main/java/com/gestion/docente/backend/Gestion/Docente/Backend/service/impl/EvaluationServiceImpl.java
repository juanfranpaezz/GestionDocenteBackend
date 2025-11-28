package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.EvaluationDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Student;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.StudentRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailService;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.EvaluationService;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.GradeService;

@Service
@Transactional
public class EvaluationServiceImpl implements EvaluationService {
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private GradeService gradeService;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public List<EvaluationDTO> getEvaluationsByCourse(Long courseId) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        List<Evaluation> evaluations = evaluationRepository.findByCourseId(courseId);
        return evaluations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<EvaluationDTO> getEvaluationsByCourse(Long courseId, Pageable pageable) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        Page<Evaluation> evaluationsPage = evaluationRepository.findByCourseId(courseId, pageable);
        return evaluationsPage.map(this::convertToDTO);
    }
    
    @Override
    public EvaluationDTO addEvaluation(EvaluationDTO evaluationDTO) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(evaluationDTO.getCourseId());
        
        // Validar que no exista una evaluación con el mismo nombre y fecha en el mismo curso
        if (evaluationRepository.existsByCourseIdAndNombreAndDate(
                evaluationDTO.getCourseId(), 
                evaluationDTO.getNombre(), 
                evaluationDTO.getDate())) {
            throw new IllegalArgumentException(
                "Ya existe una evaluación con el nombre '" + evaluationDTO.getNombre() + 
                "' y fecha '" + evaluationDTO.getDate() + "' en este curso");
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
        // Buscar la evaluación para obtener su curso
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La evaluación con ID " + id + " no existe"));
        
        // Validar ownership: el curso de la evaluación debe pertenecer al profesor autenticado
        validateCourseOwnership(evaluation.getCourseId());
        
        // Eliminar la evaluación
        evaluationRepository.deleteById(id);
    }
    
    @Override
    public void sendGradesByEmail(Long evaluationId) {
        // 1. Buscar la evaluación
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("La evaluación con ID " + evaluationId + " no existe"));
        
        // 2. Validar ownership: el curso de la evaluación debe pertenecer al profesor autenticado
        validateCourseOwnership(evaluation.getCourseId());
        
        // 3. Obtener el curso para el nombre
        Course course = courseRepository.findById(evaluation.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + evaluation.getCourseId() + " no existe"));
        
        // 4. Obtener todas las notas de esta evaluación
        List<GradeDTO> grades = gradeService.getGradesByEvaluation(evaluationId);
        
        // 5. Obtener todos los estudiantes del curso
        List<Student> students = studentRepository.findByCourseId(evaluation.getCourseId());
        
        // 6. Crear un mapa de studentId -> Student para acceso rápido
        java.util.Map<Long, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getId, student -> student));
        
        // 7. Enviar email a cada estudiante con su nota
        int emailsSent = 0;
        int emailsFailed = 0;
        
        for (GradeDTO grade : grades) {
            Student student = studentMap.get(grade.getStudentId());
            if (student != null && student.getEmail() != null && !student.getEmail().trim().isEmpty()) {
                try {
                    String studentName = student.getFirstName() + 
                            (student.getLastName() != null ? " " + student.getLastName() : "");
                    emailService.sendEvaluationGradeEmail(
                            student.getEmail(),
                            studentName,
                            evaluation.getNombre(),
                            course.getName(),
                            grade.getGrade()
                    );
                    emailsSent++;
                } catch (Exception e) {
                    System.err.println("Error al enviar email a " + student.getEmail() + ": " + e.getMessage());
                    emailsFailed++;
                }
            } else {
                System.err.println("Estudiante con ID " + grade.getStudentId() + " no tiene email configurado");
                emailsFailed++;
            }
        }
        
        System.out.println("Envío de notas completado. Enviados: " + emailsSent + ", Fallidos: " + emailsFailed);
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

