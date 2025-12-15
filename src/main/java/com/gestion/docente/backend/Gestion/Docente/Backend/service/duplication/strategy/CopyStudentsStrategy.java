package com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.strategy;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Student;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.StudentRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.CourseCopyStrategy;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.duplication.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Estrategia para copiar Students (estudiantes) de un curso a otro.
 */
@Component
public class CopyStudentsStrategy implements CourseCopyStrategy {
    
    private final StudentRepository studentRepository;
    
    public CopyStudentsStrategy(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    @Override
    public void copy(Long originalCourseId, Long newCourseId, Map<String, Map<Long, Long>> idMappings) {
        List<Student> originalStudents = studentRepository.findByCourseId(originalCourseId);
        
        List<Student> newStudents = originalStudents.stream()
            .map(original -> {
                Student newStudent = new Student();
                newStudent.setFirstName(original.getFirstName());
                newStudent.setLastName(original.getLastName());
                newStudent.setEmail(original.getEmail());
                newStudent.setCel(original.getCel());
                newStudent.setCourseId(newCourseId);
                return newStudent;
            })
            .collect(Collectors.toList());
        
        List<Student> savedStudents = studentRepository.saveAll(newStudents);
        
        // Crear mapeo de IDs usando EntityMapper
        Map<Long, Long> mapping = EntityMapper.createIdMapping(
                originalStudents,
                savedStudents,
                Student::getId
        );
        
        idMappings.put("students", mapping);
    }
    
    @Override
    public String getStrategyName() {
        return "CopyStudents";
    }
}

