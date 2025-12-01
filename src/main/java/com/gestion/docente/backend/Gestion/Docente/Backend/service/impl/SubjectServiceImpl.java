package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.SubjectDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Subject;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.SubjectRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de materias.
 * Maneja la creación, consulta y gestión de materias.
 */
@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public List<SubjectDTO> getSubjectsByCourse(Long courseId) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Validar que el curso exista y pertenezca al profesor autenticado
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + courseId + " no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene acceso a este curso");
        }
        
        // 3. Obtener materias del curso
        List<Subject> subjects = subjectRepository.findByCourseId(courseId);
        
        // 4. Convertir a DTOs y retornar
        return subjects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public SubjectDTO getDefaultSubject(Long courseId) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Validar que el curso exista y pertenezca al profesor autenticado
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + courseId + " no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene acceso a este curso");
        }
        
        // 3. Buscar materia sin nombre (default) o la primera materia del curso
        Subject defaultSubject = subjectRepository.findFirstByCourseIdOrderByIdAsc(courseId);
        
        if (defaultSubject == null) {
            throw new IllegalArgumentException("No se encontró materia default para el curso");
        }
        
        // 4. Convertir a DTO y retornar
        return convertToDTO(defaultSubject);
    }
    
    @Override
    public SubjectDTO createSubject(SubjectDTO subjectDTO) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Validar que el curso exista y pertenezca al profesor autenticado
        Course course = courseRepository.findById(subjectDTO.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + subjectDTO.getCourseId() + " no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede agregar materias a cursos de otros profesores");
        }
        
        // 3. Validar que si se proporciona nombre, no esté vacío (permite null para materia default)
        String subjectName = subjectDTO.getName() != null ? subjectDTO.getName().trim() : null;
        if (subjectName != null && subjectName.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la materia no puede estar vacío. Use null para materia sin nombre.");
        }
        
        // 4. Si se proporciona nombre, validar que no exista otra materia con el mismo nombre en el curso
        if (subjectName != null && subjectRepository.existsByCourseIdAndName(subjectDTO.getCourseId(), subjectName)) {
            throw new IllegalArgumentException("Ya existe una materia con el nombre '" + subjectName + "' en este curso");
        }
        
        // 5. Convertir DTO a entidad
        Subject subject = convertToEntity(subjectDTO);
        
        // 6. Guardar en la base de datos
        Subject savedSubject = subjectRepository.save(subject);
        
        // 7. Convertir a DTO y retornar
        return convertToDTO(savedSubject);
    }
    
    @Override
    public SubjectDTO updateSubject(Long id, SubjectDTO subjectDTO) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Buscar la materia existente
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La materia con ID " + id + " no existe"));
        
        // 3. Validar que el curso de la materia pertenezca al profesor autenticado
        Course course = courseRepository.findById(existingSubject.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("El curso de la materia no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede actualizar materias de cursos de otros profesores");
        }
        
        // 4. Procesar el nombre (puede ser null, vacío, o con valor)
        String newName = subjectDTO.getName() != null ? subjectDTO.getName().trim() : null;
        if (newName != null && newName.isEmpty()) {
            newName = null; // Convertir cadena vacía a null
        }
        
        // 5. Si se está cambiando el nombre, validar que no exista otra materia con ese nombre en el curso
        String currentName = existingSubject.getName() != null ? existingSubject.getName() : null;
        if (newName != null && !newName.equals(currentName)) {
            if (subjectRepository.existsByCourseIdAndName(existingSubject.getCourseId(), newName)) {
                throw new IllegalArgumentException("Ya existe una materia con el nombre '" + newName + "' en este curso");
            }
        }
        
        // 6. Actualizar el nombre de la materia (puede ser null para materia sin nombre)
        existingSubject.setName(newName);
        
        // 6. Guardar los cambios
        Subject updatedSubject = subjectRepository.save(existingSubject);
        
        // 7. Convertir a DTO y retornar
        return convertToDTO(updatedSubject);
    }
    
    @Override
    public void deleteSubject(Long id) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Buscar la materia existente
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La materia con ID " + id + " no existe"));
        
        // 3. Validar que el curso de la materia pertenezca al profesor autenticado
        Course course = courseRepository.findById(subject.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("El curso de la materia no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede eliminar materias de cursos de otros profesores");
        }
        
        // 4. Eliminar materia (las relaciones se eliminan en cascada según la configuración de la entidad)
        subjectRepository.deleteById(id);
    }
    
    /**
     * Convierte una entidad Subject a SubjectDTO
     */
    private SubjectDTO convertToDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setCourseId(subject.getCourseId());
        return dto;
    }
    
    /**
     * Convierte un SubjectDTO a entidad Subject
     */
    private Subject convertToEntity(SubjectDTO dto) {
        Subject subject = new Subject();
        subject.setId(dto.getId()); // null para nuevas materias
        // Permitir null o cadena vacía (se convierte a null)
        String name = dto.getName() != null ? dto.getName().trim() : null;
        subject.setName(name != null && !name.isEmpty() ? name : null);
        subject.setCourseId(dto.getCourseId());
        return subject;
    }
}

