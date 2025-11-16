package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentAverageDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Grade;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Student;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.GradeRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.StudentRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de notas.
 * Maneja la creación, actualización y consulta de notas de estudiantes.
 */
@Service
@Transactional
public class GradeServiceImpl implements GradeService {
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public List<GradeDTO> getGradesByCourse(Long courseId) {
        // Validar que el curso exista
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("El curso con ID " + courseId + " no existe");
        }
        
        List<Grade> grades = gradeRepository.findByCourseId(courseId);
        return grades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<GradeDTO> getGradesByCourse(Long courseId, Pageable pageable) {
        // Validar que el curso exista
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("El curso con ID " + courseId + " no existe");
        }
        
        Page<Grade> gradesPage = gradeRepository.findByCourseId(courseId, pageable);
        return gradesPage.map(this::convertToDTO);
    }
    
    @Override
    public GradeDTO setGrade(GradeDTO gradeDTO) {
        // Validar rango de nota (0-10 inclusive)
        // Nota: El DTO ya tiene validaciones @DecimalMin(0.0) y @DecimalMax(10.0)
        // Esta validación adicional asegura que 0.0 y 10.0 sean permitidos
        if (gradeDTO.getGrade() != null) {
            if (gradeDTO.getGrade() < 0.0 || gradeDTO.getGrade() > 10.0) {
                throw new IllegalArgumentException("La nota debe estar entre 0 y 10 (inclusive)");
            }
        }
        
        // Validar que el estudiante exista
        if (!studentRepository.existsById(gradeDTO.getStudentId())) {
            throw new IllegalArgumentException("El estudiante con ID " + gradeDTO.getStudentId() + " no existe");
        }
        
        // Validar que la evaluación exista
        if (!evaluationRepository.existsById(gradeDTO.getEvaluationId())) {
            throw new IllegalArgumentException("La evaluación con ID " + gradeDTO.getEvaluationId() + " no existe");
        }
        
        // Validar que el curso exista
        if (!courseRepository.existsById(gradeDTO.getCourseId())) {
            throw new IllegalArgumentException("El curso con ID " + gradeDTO.getCourseId() + " no existe");
        }
        
        // Validar consistencia: verificar que el estudiante pertenezca al curso
        Student student = studentRepository.findById(gradeDTO.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("El estudiante con ID " + gradeDTO.getStudentId() + " no existe"));
        if (!student.getCourseId().equals(gradeDTO.getCourseId())) {
            throw new IllegalArgumentException("El estudiante con ID " + gradeDTO.getStudentId() + 
                    " no pertenece al curso con ID " + gradeDTO.getCourseId());
        }
        
        // Validar consistencia: verificar que la evaluación pertenezca al curso
        Evaluation evaluation = evaluationRepository.findById(gradeDTO.getEvaluationId())
                .orElseThrow(() -> new IllegalArgumentException("La evaluación con ID " + gradeDTO.getEvaluationId() + " no existe"));
        if (!evaluation.getCourseId().equals(gradeDTO.getCourseId())) {
            throw new IllegalArgumentException("La evaluación con ID " + gradeDTO.getEvaluationId() + 
                    " no pertenece al curso con ID " + gradeDTO.getCourseId());
        }
        
        // Verificar si ya existe una nota para este estudiante y evaluación
        Optional<Grade> existingGrade = gradeRepository.findByStudentIdAndEvaluationId(
            gradeDTO.getStudentId(), 
            gradeDTO.getEvaluationId()
        );
        
        Grade grade;
        if (existingGrade.isPresent()) {
            // Si existe, actualizar la nota existente
            grade = existingGrade.get();
            grade.setGrade(gradeDTO.getGrade());
            grade.setCourseId(gradeDTO.getCourseId());
        } else {
            // Si no existe, crear una nueva nota
            grade = convertToEntity(gradeDTO);
        }
        
        // Guardar en la base de datos
        Grade savedGrade = gradeRepository.save(grade);
        
        // Convertir y retornar DTO
        return convertToDTO(savedGrade);
    }
    
    @Override
    public Double calculateAverage(Long studentId, Long courseId) {
        // Validar que el estudiante exista
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("El estudiante con ID " + studentId + " no existe");
        }
        
        // Validar que el curso exista
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("El curso con ID " + courseId + " no existe");
        }
        
        // Obtener todas las notas del estudiante en el curso
        List<Grade> grades = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        // Filtrar solo las notas que tienen valor (no null)
        List<Double> validGrades = grades.stream()
                .filter(g -> g.getGrade() != null)
                .map(Grade::getGrade)
                .collect(Collectors.toList());
        
        // Si no hay notas válidas, retornar null
        if (validGrades.isEmpty()) {
            return null;
        }
        
        // Calcular el promedio
        double sum = validGrades.stream().mapToDouble(Double::doubleValue).sum();
        return sum / validGrades.size();
    }
    
    /**
     * Actualiza una nota existente por ID
     */
    public GradeDTO updateGrade(Long id, GradeDTO gradeDTO) {
        // Buscar la nota existente
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La nota con ID " + id + " no existe"));
        
        // Validar rango de nota (0-10 inclusive)
        // Nota: El DTO ya tiene validaciones @DecimalMin(0.0) y @DecimalMax(10.0)
        // Esta validación adicional asegura que 0.0 y 10.0 sean permitidos
        if (gradeDTO.getGrade() != null) {
            if (gradeDTO.getGrade() < 0.0 || gradeDTO.getGrade() > 10.0) {
                throw new IllegalArgumentException("La nota debe estar entre 0 y 10 (inclusive)");
            }
        }
        
        // Determinar el courseId final a usar (el del DTO si se actualiza, o el existente)
        Long finalCourseId = gradeDTO.getCourseId() != null ? gradeDTO.getCourseId() : grade.getCourseId();
        
        // Validar consistencia de studentId si se actualiza
        if (gradeDTO.getStudentId() != null) {
            Student student = studentRepository.findById(gradeDTO.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("El estudiante con ID " + gradeDTO.getStudentId() + " no existe"));
            
            // Validar consistencia: verificar que el estudiante pertenezca al curso final
            if (!student.getCourseId().equals(finalCourseId)) {
                throw new IllegalArgumentException("El estudiante con ID " + gradeDTO.getStudentId() + 
                        " no pertenece al curso con ID " + finalCourseId);
            }
        }
        
        // Validar consistencia de evaluationId si se actualiza
        if (gradeDTO.getEvaluationId() != null) {
            Evaluation evaluation = evaluationRepository.findById(gradeDTO.getEvaluationId())
                    .orElseThrow(() -> new IllegalArgumentException("La evaluación con ID " + gradeDTO.getEvaluationId() + " no existe"));
            
            // Validar consistencia: verificar que la evaluación pertenezca al curso final
            if (!evaluation.getCourseId().equals(finalCourseId)) {
                throw new IllegalArgumentException("La evaluación con ID " + gradeDTO.getEvaluationId() + 
                        " no pertenece al curso con ID " + finalCourseId);
            }
        }
        
        // Si se actualiza el courseId, validar consistencia con studentId y evaluationId existentes
        if (gradeDTO.getCourseId() != null) {
            Long newCourseId = gradeDTO.getCourseId();
            Long studentIdToCheck = gradeDTO.getStudentId() != null ? gradeDTO.getStudentId() : grade.getStudentId();
            Long evaluationIdToCheck = gradeDTO.getEvaluationId() != null ? gradeDTO.getEvaluationId() : grade.getEvaluationId();
            
            if (studentIdToCheck != null) {
                Student student = studentRepository.findById(studentIdToCheck)
                        .orElseThrow(() -> new IllegalArgumentException("El estudiante asociado no existe"));
                if (!student.getCourseId().equals(newCourseId)) {
                    throw new IllegalArgumentException("El estudiante con ID " + studentIdToCheck + 
                            " no pertenece al curso con ID " + newCourseId);
                }
            }
            
            if (evaluationIdToCheck != null) {
                Evaluation evaluation = evaluationRepository.findById(evaluationIdToCheck)
                        .orElseThrow(() -> new IllegalArgumentException("La evaluación asociada no existe"));
                if (!evaluation.getCourseId().equals(newCourseId)) {
                    throw new IllegalArgumentException("La evaluación con ID " + evaluationIdToCheck + 
                            " no pertenece al curso con ID " + newCourseId);
                }
            }
        }
        
        // Actualizar los campos
        if (gradeDTO.getGrade() != null) {
            grade.setGrade(gradeDTO.getGrade());
        }
        if (gradeDTO.getCourseId() != null) {
            grade.setCourseId(gradeDTO.getCourseId());
        }
        if (gradeDTO.getStudentId() != null) {
            grade.setStudentId(gradeDTO.getStudentId());
        }
        if (gradeDTO.getEvaluationId() != null) {
            grade.setEvaluationId(gradeDTO.getEvaluationId());
        }
        
        // Guardar cambios
        Grade savedGrade = gradeRepository.save(grade);
        
        // Convertir y retornar DTO
        return convertToDTO(savedGrade);
    }
    
    /**
     * Obtiene todas las notas de una evaluación específica
     */
    @Override
    public List<GradeDTO> getGradesByEvaluation(Long evaluationId) {
        // Validar que la evaluación exista
        if (!evaluationRepository.existsById(evaluationId)) {
            throw new IllegalArgumentException("La evaluación con ID " + evaluationId + " no existe");
        }
        
        List<Grade> grades = gradeRepository.findByEvaluationId(evaluationId);
        return grades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<GradeDTO> getGradesByEvaluation(Long evaluationId, Pageable pageable) {
        // Validar que la evaluación exista
        if (!evaluationRepository.existsById(evaluationId)) {
            throw new IllegalArgumentException("La evaluación con ID " + evaluationId + " no existe");
        }
        
        Page<Grade> gradesPage = gradeRepository.findByEvaluationId(evaluationId, pageable);
        return gradesPage.map(this::convertToDTO);
    }
    
    @Override
    public List<StudentAverageDTO> getAveragesByCourse(Long courseId) {
        // Validar que el curso exista
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("El curso con ID " + courseId + " no existe");
        }
        
        // Obtener todos los estudiantes del curso
        List<Student> students = studentRepository.findByCourseId(courseId);
        
        // Si no hay estudiantes, retornar lista vacía
        if (students.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Para cada estudiante, calcular su promedio y crear el DTO
        // Optimización: evitamos validaciones redundantes ya que sabemos que los estudiantes
        // pertenecen al curso y existen (los obtuvimos del repositorio)
        return students.stream()
                .map(student -> {
                    // Obtener todas las notas del estudiante en el curso (una sola consulta)
                    List<Grade> studentGrades = gradeRepository.findByStudentIdAndCourseId(
                            student.getId(), courseId);
                    
                    // Filtrar solo las notas que tienen valor (no null)
                    List<Double> validGrades = studentGrades.stream()
                            .filter(g -> g.getGrade() != null)
                            .map(Grade::getGrade)
                            .collect(Collectors.toList());
                    
                    // Calcular el promedio
                    Double average = null;
                    if (!validGrades.isEmpty()) {
                        double sum = validGrades.stream().mapToDouble(Double::doubleValue).sum();
                        average = sum / validGrades.size();
                    }
                    
                    // Crear el DTO
                    StudentAverageDTO dto = new StudentAverageDTO();
                    dto.setStudentId(student.getId());
                    dto.setFirstName(student.getFirstName());
                    dto.setLastName(student.getLastName()); // Puede ser null, está permitido
                    dto.setAverage(average);
                    dto.setHasGrades(!validGrades.isEmpty());
                    dto.setGradesCount(validGrades.size());
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte una entidad Grade a GradeDTO
     */
    private GradeDTO convertToDTO(Grade grade) {
        GradeDTO dto = new GradeDTO();
        dto.setId(grade.getId());
        dto.setGrade(grade.getGrade());
        dto.setCourseId(grade.getCourseId());
        dto.setStudentId(grade.getStudentId());
        dto.setEvaluationId(grade.getEvaluationId());
        return dto;
    }
    
    /**
     * Convierte un GradeDTO a entidad Grade
     */
    private Grade convertToEntity(GradeDTO dto) {
        Grade grade = new Grade();
        grade.setId(dto.getId()); // null para nuevas notas
        grade.setGrade(dto.getGrade());
        grade.setCourseId(dto.getCourseId());
        grade.setStudentId(dto.getStudentId());
        grade.setEvaluationId(dto.getEvaluationId());
        return grade;
    }
}


