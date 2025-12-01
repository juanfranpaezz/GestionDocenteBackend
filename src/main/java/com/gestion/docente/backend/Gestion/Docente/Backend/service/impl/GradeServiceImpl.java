package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GradeDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GroupedAverageDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentAverageDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentGroupedAveragesDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.EvaluationType;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Grade;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Student;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationTypeRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.GradeRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.StudentRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
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
    
    @Autowired
    private EvaluationTypeRepository evaluationTypeRepository;
    
    @Autowired
    private com.gestion.docente.backend.Gestion.Docente.Backend.repository.GradeScaleOptionRepository gradeScaleOptionRepository;
    
    @Override
    public List<GradeDTO> getGradesByCourse(Long courseId) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        List<Grade> grades = gradeRepository.findByCourseId(courseId);
        return grades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<GradeDTO> getGradesByCourse(Long courseId, Pageable pageable) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        Page<Grade> gradesPage = gradeRepository.findByCourseId(courseId, pageable);
        return gradesPage.map(this::convertToDTO);
    }
    
    @Override
    public GradeDTO setGrade(GradeDTO gradeDTO) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(gradeDTO.getCourseId());
        
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
    public Double calculateAverage(Long studentId, Long courseId, Long subjectId) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        // Validar que el estudiante exista
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("El estudiante con ID " + studentId + " no existe");
        }
        
        // Obtener todas las notas del estudiante en el curso
        List<Grade> grades = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        // Obtener todas las evaluaciones para acceder a sus escalas
        List<Evaluation> evaluations = evaluationRepository.findByCourseId(courseId);
        
        // Filtrar evaluaciones por materia si se proporciona
        if (subjectId != null) {
            evaluations = evaluations.stream()
                    .filter(eval -> subjectId.equals(eval.getSubjectId()))
                    .collect(Collectors.toList());
        }
        
        java.util.Map<Long, Evaluation> evalMap = evaluations.stream()
                .collect(Collectors.toMap(Evaluation::getId, eval -> eval));
        
        // Filtrar notas solo de las evaluaciones de la materia (si se proporciona)
        List<Grade> filteredGrades = grades;
        if (subjectId != null) {
            java.util.Set<Long> evaluationIds = evaluations.stream()
                    .map(Evaluation::getId)
                    .collect(Collectors.toSet());
            filteredGrades = grades.stream()
                    .filter(grade -> grade.getEvaluationId() != null && evaluationIds.contains(grade.getEvaluationId()))
                    .collect(Collectors.toList());
        }
        
        // Convertir notas a valores numéricos (incluyendo mapeo de escalas categóricas)
        List<Double> validGrades = new ArrayList<>();
        for (Grade grade : filteredGrades) {
            Double numericValue = getNumericGradeValue(grade, evalMap);
            if (numericValue != null) {
                validGrades.add(numericValue);
            }
        }
        
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
        
        // Validar ownership: el curso de la nota debe pertenecer al profesor autenticado
        validateCourseOwnership(grade.getCourseId());
        
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
        
        // Si se actualiza el courseId, validar ownership del nuevo curso
        if (gradeDTO.getCourseId() != null && !gradeDTO.getCourseId().equals(grade.getCourseId())) {
            validateCourseOwnership(gradeDTO.getCourseId());
        }
        
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
        // Buscar la evaluación para obtener su curso
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("La evaluación con ID " + evaluationId + " no existe"));
        
        // Validar ownership: el curso de la evaluación debe pertenecer al profesor autenticado
        validateCourseOwnership(evaluation.getCourseId());
        
        List<Grade> grades = gradeRepository.findByEvaluationId(evaluationId);
        return grades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<GradeDTO> getGradesByEvaluation(Long evaluationId, Pageable pageable) {
        // Buscar la evaluación para obtener su curso
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("La evaluación con ID " + evaluationId + " no existe"));
        
        // Validar ownership: el curso de la evaluación debe pertenecer al profesor autenticado
        validateCourseOwnership(evaluation.getCourseId());
        
        Page<Grade> gradesPage = gradeRepository.findByEvaluationId(evaluationId, pageable);
        return gradesPage.map(this::convertToDTO);
    }
    
    @Override
    public List<StudentAverageDTO> getAveragesByCourse(Long courseId) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
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
                    
                    // Obtener evaluaciones para mapeo de escalas
                    List<Evaluation> evaluations = evaluationRepository.findByCourseId(courseId);
                    java.util.Map<Long, Evaluation> evalMap = evaluations.stream()
                            .collect(Collectors.toMap(Evaluation::getId, eval -> eval));
                    
                    // Convertir notas a valores numéricos (incluyendo mapeo de escalas)
                    List<Double> validGrades = new ArrayList<>();
                    for (Grade grade : studentGrades) {
                        Double numericValue = getNumericGradeValue(grade, evalMap);
                        if (numericValue != null) {
                            validGrades.add(numericValue);
                        }
                    }
                    
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
        dto.setGradeValue(grade.getGradeValue());
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
        grade.setGradeValue(dto.getGradeValue());
        grade.setCourseId(dto.getCourseId());
        grade.setStudentId(dto.getStudentId());
        grade.setEvaluationId(dto.getEvaluationId());
        return grade;
    }
    
    @Override
    public StudentGroupedAveragesDTO getGroupedAverages(Long studentId, Long courseId) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        // Validar que el estudiante exista
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("El estudiante con ID " + studentId + " no existe"));
        
        // Obtener todas las evaluaciones del curso
        List<Evaluation> evaluations = evaluationRepository.findByCourseId(courseId);
        
        // Obtener todos los tipos de evaluación del curso
        List<EvaluationType> evaluationTypes = evaluationTypeRepository.findByCourseId(courseId);
        
        // Crear un mapa de evaluationTypeId -> EvaluationType para acceso rápido
        java.util.Map<Long, EvaluationType> typeMap = evaluationTypes.stream()
                .collect(Collectors.toMap(EvaluationType::getId, type -> type));
        
        // Obtener todas las notas del estudiante en el curso
        List<Grade> studentGrades = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        // Crear un mapa de evaluationId -> Grade para acceso rápido (incluir todas las notas)
        java.util.Map<Long, Grade> gradeMap = studentGrades.stream()
                .filter(g -> g.getGrade() != null || g.getGradeValue() != null)
                .collect(Collectors.toMap(Grade::getEvaluationId, grade -> grade));
        
        // Crear mapa de evaluaciones para acceder a escalas
        java.util.Map<Long, Evaluation> evalMap = evaluations.stream()
                .collect(Collectors.toMap(Evaluation::getId, eval -> eval));
        
        // Agrupar evaluaciones por tipo
        java.util.Map<Long, List<Evaluation>> evaluationsByType = evaluations.stream()
                .filter(e -> e.getEvaluationTypeId() != null)
                .filter(e -> gradeMap.containsKey(e.getId())) // Solo evaluaciones con notas
                .collect(Collectors.groupingBy(Evaluation::getEvaluationTypeId));
        
        // Calcular promedio por cada tipo
        List<GroupedAverageDTO> groupedAverages = new ArrayList<>();
        for (java.util.Map.Entry<Long, List<Evaluation>> entry : evaluationsByType.entrySet()) {
            Long typeId = entry.getKey();
            List<Evaluation> typeEvaluations = entry.getValue();
            
            EvaluationType type = typeMap.get(typeId);
            if (type == null) continue;
            
            // Calcular promedio de las notas de este tipo (incluyendo mapeo de escalas)
            List<Double> typeGrades = new ArrayList<>();
            for (Evaluation eval : typeEvaluations) {
                Grade grade = gradeMap.get(eval.getId());
                if (grade != null) {
                    Double numericValue = getNumericGradeValue(grade, evalMap);
                    if (numericValue != null) {
                        typeGrades.add(numericValue);
                    }
                }
            }
            
            if (typeGrades.isEmpty()) continue;
            
            double typeAverage = typeGrades.stream().mapToDouble(Double::doubleValue).sum() / typeGrades.size();
            
            GroupedAverageDTO groupedAvg = new GroupedAverageDTO();
            groupedAvg.setEvaluationTypeId(typeId);
            groupedAvg.setEvaluationTypeName(type.getNombre());
            groupedAvg.setAverage(typeAverage);
            groupedAvg.setEvaluationsCount(typeEvaluations.size());
            groupedAvg.setEvaluationIds(typeEvaluations.stream().map(Evaluation::getId).collect(Collectors.toList()));
            
            groupedAverages.add(groupedAvg);
        }
        
        // Calcular promedio final usando pesos
        Double finalAverage = null;
        if (!groupedAverages.isEmpty()) {
            // Calcular suma total de pesos
            double totalWeight = 0.0;
            double weightedSum = 0.0;
            
            for (GroupedAverageDTO groupedAvg : groupedAverages) {
                EvaluationType type = typeMap.get(groupedAvg.getEvaluationTypeId());
                double weight = (type != null && type.getWeight() != null) ? type.getWeight() : 100.0 / groupedAverages.size();
                totalWeight += weight;
                weightedSum += groupedAvg.getAverage() * weight;
            }
            
            if (totalWeight > 0) {
                finalAverage = weightedSum / totalWeight;
            } else {
                // Fallback a promedio simple si no hay pesos
                double sum = groupedAverages.stream()
                        .mapToDouble(GroupedAverageDTO::getAverage)
                        .sum();
                finalAverage = sum / groupedAverages.size();
            }
        }
        
        // Crear y retornar DTO
        StudentGroupedAveragesDTO result = new StudentGroupedAveragesDTO();
        result.setStudentId(student.getId());
        result.setFirstName(student.getFirstName());
        result.setLastName(student.getLastName());
        result.setGroupedAverages(groupedAverages);
        result.setFinalAverage(finalAverage);
        
        return result;
    }
    
    @Override
    public List<StudentGroupedAveragesDTO> getGroupedAveragesByCourse(Long courseId) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        // Obtener todos los estudiantes del curso
        List<Student> students = studentRepository.findByCourseId(courseId);
        
        // Calcular promedios agrupados para cada estudiante
        return students.stream()
                .map(student -> getGroupedAverages(student.getId(), courseId))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene el valor numérico de una nota, considerando mapeo de escalas categóricas
     */
    private Double getNumericGradeValue(Grade grade, java.util.Map<Long, Evaluation> evalMap) {
        // Si tiene valor numérico directo, usarlo
        if (grade.getGrade() != null) {
            return grade.getGrade();
        }
        
        // Si tiene valor categórico, buscar mapeo numérico
        if (grade.getGradeValue() != null && grade.getEvaluationId() != null) {
            Evaluation evaluation = evalMap.get(grade.getEvaluationId());
            if (evaluation != null && evaluation.getGradeScaleId() != null) {
                // Buscar la opción de escala que corresponde al gradeValue
                List<com.gestion.docente.backend.Gestion.Docente.Backend.model.GradeScaleOption> options = 
                    gradeScaleOptionRepository.findByGradeScaleIdOrderByOrderValueAsc(evaluation.getGradeScaleId());
                
                for (com.gestion.docente.backend.Gestion.Docente.Backend.model.GradeScaleOption option : options) {
                    if (option.getLabel().equalsIgnoreCase(grade.getGradeValue()) && 
                        option.getNumericValue() != null) {
                        return option.getNumericValue();
                    }
                }
            }
        }
        
        return null;
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


