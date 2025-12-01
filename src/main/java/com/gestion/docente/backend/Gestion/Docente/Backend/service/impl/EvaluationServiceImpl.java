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
    
    @Autowired
    private com.gestion.docente.backend.Gestion.Docente.Backend.repository.ProfessorRepository professorRepository;
    
    @Autowired
    private com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailTemplateService emailTemplateService;
    
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
        
        // Log para depuración
        System.out.println("📝 Creando evaluación - nombre: " + evaluation.getNombre() + ", subjectId: " + evaluation.getSubjectId() + ", courseId: " + evaluation.getCourseId());
        
        // Guardar en la base de datos
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        
        System.out.println("✅ Evaluación guardada - id: " + savedEvaluation.getId() + ", subjectId: " + savedEvaluation.getSubjectId());
        
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
        
        // 4. Obtener datos del profesor
        com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor professor = 
            professorRepository.findById(course.getProfessorId())
                .orElseThrow(() -> new IllegalArgumentException("El profesor no existe"));
        String professorEmail = professor.getEmail();
        String professorName = professor.getName() + 
            (professor.getLastname() != null ? " " + professor.getLastname() : "");
        
        // 5. Obtener todas las notas de esta evaluación
        List<GradeDTO> grades = gradeService.getGradesByEvaluation(evaluationId);
        
        // 6. Obtener todos los estudiantes del curso
        List<Student> students = studentRepository.findByCourseId(evaluation.getCourseId());
        
        // 7. Crear un mapa de studentId -> Student para acceso rápido
        java.util.Map<Long, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getId, student -> student));
        
        // 8. Enviar email a cada estudiante con su nota
        int emailsSent = 0;
        int emailsFailed = 0;
        
        for (GradeDTO grade : grades) {
            Student student = studentMap.get(grade.getStudentId());
            if (student != null && student.getEmail() != null && !student.getEmail().trim().isEmpty()) {
                try {
                    String studentName = student.getFirstName() + 
                            (student.getLastName() != null ? " " + student.getLastName() : "");
                    String gradeValue = grade.getGrade() != null ? String.format("%.2f", grade.getGrade()) : 
                                      (grade.getGradeValue() != null ? grade.getGradeValue() : "Sin calificar");
                    // Usar el método con template para incluir email del profesor y mensaje de NO responder
                    emailService.sendEvaluationGradeEmailWithTemplate(
                            student.getEmail(),
                            studentName,
                            evaluation.getNombre(),
                            course.getName(),
                            grade.getGrade(),
                            gradeValue,
                            null, // Sin template personalizado
                            null, // Sin mensaje personalizado
                            professorEmail,
                            professorName
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
        
        // 8. Marcar que se enviaron las notas por email
        evaluation.setGradesSentByEmail(true);
        evaluationRepository.save(evaluation);
        
        System.out.println("Envío de notas completado. Enviados: " + emailsSent + ", Fallidos: " + emailsFailed);
    }
    
    @Override
    public EvaluationDTO updateEvaluation(Long id, EvaluationDTO evaluationDTO) {
        Evaluation existing = evaluationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La evaluación con ID " + id + " no existe"));
        
        validateCourseOwnership(existing.getCourseId());
        
        // Validar que no se intente editar nombre o fecha si ya se enviaron las notas
        if (Boolean.TRUE.equals(existing.getGradesSentByEmail())) {
            if (!existing.getNombre().equals(evaluationDTO.getNombre()) || 
                !existing.getDate().equals(evaluationDTO.getDate())) {
                throw new IllegalArgumentException(
                    "No se puede editar el nombre o fecha de una evaluación después de enviar las notas por email"
                );
            }
        }
        
        // Actualizar campos permitidos
        existing.setNombre(evaluationDTO.getNombre());
        existing.setDate(evaluationDTO.getDate());
        existing.setTipo(evaluationDTO.getTipo());
        existing.setEvaluationTypeId(evaluationDTO.getEvaluationTypeId());
        existing.setGradeScaleId(evaluationDTO.getGradeScaleId());
        existing.setCustomMessage(evaluationDTO.getCustomMessage());
        
        Evaluation saved = evaluationRepository.save(existing);
        return convertToDTO(saved);
    }
    
    @Override
    public void sendGradesByEmailCustom(Long evaluationId, com.gestion.docente.backend.Gestion.Docente.Backend.dto.SendGradesCustomDTO sendDTO) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("La evaluación con ID " + evaluationId + " no existe"));
        
        validateCourseOwnership(evaluation.getCourseId());
        
        Course course = courseRepository.findById(evaluation.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + evaluation.getCourseId() + " no existe"));
        
        // Obtener datos del profesor
        com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor professor = 
            professorRepository.findById(course.getProfessorId())
                .orElseThrow(() -> new IllegalArgumentException("El profesor no existe"));
        String professorEmail = professor.getEmail();
        String professorName = professor.getName() + 
            (professor.getLastname() != null ? " " + professor.getLastname() : "");
        
        List<GradeDTO> grades = gradeService.getGradesByEvaluation(evaluationId);
        List<Student> students = studentRepository.findByCourseId(evaluation.getCourseId());
        
        java.util.Map<Long, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getId, student -> student));
        
        // Obtener template si se especifica
        com.gestion.docente.backend.Gestion.Docente.Backend.dto.EmailTemplateDTO template = null;
        if (Boolean.TRUE.equals(sendDTO.getUseTemplate()) && sendDTO.getTemplateId() != null) {
            template = emailTemplateService.getEmailTemplateById(sendDTO.getTemplateId());
        }
        
        int emailsSent = 0;
        int emailsFailed = 0;
        
        for (GradeDTO grade : grades) {
            Student student = studentMap.get(grade.getStudentId());
            if (student != null && student.getEmail() != null && !student.getEmail().trim().isEmpty()) {
                try {
                    String studentName = student.getFirstName() + 
                            (student.getLastName() != null ? " " + student.getLastName() : "");
                    
                    String gradeValue = grade.getGrade() != null ? String.format("%.2f", grade.getGrade()) : 
                                      (grade.getGradeValue() != null ? grade.getGradeValue() : "Sin calificar");
                    
                    emailService.sendEvaluationGradeEmailWithTemplate(
                            student.getEmail(),
                            studentName,
                            evaluation.getNombre(),
                            course.getName(),
                            grade.getGrade(),
                            grade.getGradeValue(),
                            sendDTO.getTemplateId(),
                            sendDTO.getCustomMessage(),
                            professorEmail,
                            professorName
                    );
                    emailsSent++;
                } catch (Exception e) {
                    System.err.println("Error al enviar email a " + student.getEmail() + ": " + e.getMessage());
                    emailsFailed++;
                }
            } else {
                emailsFailed++;
            }
        }
        
        // Marcar que se enviaron las notas
        evaluation.setGradesSentByEmail(true);
        evaluationRepository.save(evaluation);
        
        System.out.println("Envío de notas personalizado completado. Enviados: " + emailsSent + ", Fallidos: " + emailsFailed);
    }
    
    @Override
    public EvaluationDTO updateEvaluationGradeScale(Long id, Long gradeScaleId) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La evaluación con ID " + id + " no existe"));
        
        validateCourseOwnership(evaluation.getCourseId());
        
        evaluation.setGradeScaleId(gradeScaleId);
        Evaluation saved = evaluationRepository.save(evaluation);
        return convertToDTO(saved);
    }
    
    // Métodos auxiliares para conversión
    private EvaluationDTO convertToDTO(Evaluation evaluation) {
        EvaluationDTO dto = new EvaluationDTO();
        dto.setId(evaluation.getId());
        dto.setNombre(evaluation.getNombre());
        dto.setDate(evaluation.getDate());
        dto.setTipo(evaluation.getTipo());
        dto.setEvaluationTypeId(evaluation.getEvaluationTypeId());
        dto.setGradesSentByEmail(evaluation.getGradesSentByEmail());
        dto.setCustomMessage(evaluation.getCustomMessage());
        dto.setGradeScaleId(evaluation.getGradeScaleId());
        dto.setSubjectId(evaluation.getSubjectId()); // Agregar subjectId
        dto.setApprovalGrade(evaluation.getApprovalGrade());
        dto.setQualificationGrade(evaluation.getQualificationGrade());
        dto.setCourseId(evaluation.getCourseId());
        return dto;
    }
    
    private Evaluation convertToEntity(EvaluationDTO dto) {
        Evaluation evaluation = new Evaluation();
        evaluation.setId(dto.getId()); // null para nuevas evaluaciones
        evaluation.setNombre(dto.getNombre());
        evaluation.setDate(dto.getDate());
        evaluation.setTipo(dto.getTipo());
        evaluation.setEvaluationTypeId(dto.getEvaluationTypeId());
        evaluation.setGradeScaleId(dto.getGradeScaleId());
        evaluation.setCustomMessage(dto.getCustomMessage());
        evaluation.setGradesSentByEmail(dto.getGradesSentByEmail() != null ? dto.getGradesSentByEmail() : false);
        evaluation.setSubjectId(dto.getSubjectId()); // Agregar subjectId
        evaluation.setApprovalGrade(dto.getApprovalGrade());
        evaluation.setQualificationGrade(dto.getQualificationGrade());
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

