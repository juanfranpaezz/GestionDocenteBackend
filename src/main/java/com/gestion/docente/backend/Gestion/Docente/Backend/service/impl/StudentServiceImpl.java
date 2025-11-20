package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Student;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.StudentRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de estudiantes.
 * Maneja la creación, consulta y gestión de estudiantes.
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public List<StudentDTO> getStudentsByCourse(Long courseId) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Validar que el curso exista y pertenezca al profesor autenticado
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + courseId + " no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene acceso a este curso");
        }
        
        // 3. Obtener estudiantes del curso
        List<Student> students = studentRepository.findByCourseId(courseId);
        
        // 4. Convertir a DTOs y retornar
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public StudentDTO addStudentToCourse(StudentDTO studentDTO) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Validar que el curso exista y pertenezca al profesor autenticado
        Course course = courseRepository.findById(studentDTO.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + studentDTO.getCourseId() + " no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede agregar estudiantes a cursos de otros profesores");
        }
        
        // 3. Validar campos obligatorios
        if (studentDTO.getFirstName() == null || studentDTO.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estudiante es obligatorio");
        }
        
        // 4. Convertir DTO a entidad
        Student student = convertToEntity(studentDTO);
        
        // 5. Guardar en la base de datos
        Student savedStudent = studentRepository.save(student);
        
        // 6. Convertir a DTO y retornar
        return convertToDTO(savedStudent);
    }
    
    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Buscar el estudiante existente
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El estudiante con ID " + id + " no existe"));
        
        // 3. Validar que el curso del estudiante pertenezca al profesor autenticado
        Course course = courseRepository.findById(existingStudent.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("El curso del estudiante no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede actualizar estudiantes de cursos de otros profesores");
        }
        
        // 4. Si se está cambiando el courseId, validar que el nuevo curso también pertenezca al profesor
        if (studentDTO.getCourseId() != null && !studentDTO.getCourseId().equals(existingStudent.getCourseId())) {
            Course newCourse = courseRepository.findById(studentDTO.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("El nuevo curso no existe"));
            
            if (!newCourse.getProfessorId().equals(currentProfessorId)) {
                throw new IllegalArgumentException("No puede mover estudiantes a cursos de otros profesores");
            }
        }
        
        // 5. Actualizar los campos del estudiante
        if (studentDTO.getFirstName() != null) {
            existingStudent.setFirstName(studentDTO.getFirstName());
        }
        if (studentDTO.getLastName() != null) {
            existingStudent.setLastName(studentDTO.getLastName());
        }
        if (studentDTO.getCel() != null) {
            existingStudent.setCel(studentDTO.getCel());
        }
        if (studentDTO.getEmail() != null) {
            existingStudent.setEmail(studentDTO.getEmail());
        }
        if (studentDTO.getDocument() != null) {
            existingStudent.setDocument(studentDTO.getDocument());
        }
        if (studentDTO.getCourseId() != null) {
            existingStudent.setCourseId(studentDTO.getCourseId());
        }
        
        // 6. Guardar los cambios
        Student updatedStudent = studentRepository.save(existingStudent);
        
        // 7. Convertir a DTO y retornar
        return convertToDTO(updatedStudent);
    }
    
    @Override
    public void removeStudent(Long id) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Buscar el estudiante existente
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El estudiante con ID " + id + " no existe"));
        
        // 3. Validar que el curso del estudiante pertenezca al profesor autenticado
        Course course = courseRepository.findById(student.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("El curso del estudiante no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede eliminar estudiantes de cursos de otros profesores");
        }
        
        // 4. Eliminar estudiante (las relaciones se eliminan en cascada)
        studentRepository.deleteById(id);
    }
    
    /**
     * Convierte una entidad Student a StudentDTO
     */
    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setCel(student.getCel());
        dto.setEmail(student.getEmail());
        dto.setDocument(student.getDocument());
        dto.setCourseId(student.getCourseId());
        return dto;
    }
    
    /**
     * Convierte un StudentDTO a entidad Student
     */
    private Student convertToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setId(dto.getId()); // null para nuevos estudiantes
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setCel(dto.getCel());
        student.setEmail(dto.getEmail());
        student.setDocument(dto.getDocument());
        student.setCourseId(dto.getCourseId());
        return student;
    }
}

