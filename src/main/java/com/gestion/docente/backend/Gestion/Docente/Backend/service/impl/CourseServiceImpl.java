package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CourseDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.ProfessorRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de cursos.
 * Maneja la creación, consulta y gestión de cursos.
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        // 1. Validar que el profesor exista
        if (!professorRepository.existsById(courseDTO.getProfessorId())) {
            throw new IllegalArgumentException("El profesor con ID " + courseDTO.getProfessorId() + " no existe");
        }
        
        // 2. Crear nueva entidad Course
        Course course = convertToEntity(courseDTO);
        
        // 3. Guardar en la base de datos
        Course savedCourse = courseRepository.save(course);
        
        // 4. Convertir a DTO y retornar
        return convertToDTO(savedCourse);
    }
    
    @Override
    public List<CourseDTO> getAllCourses() {
        // Obtener todos los cursos
        List<Course> courses = courseRepository.findAll();
        
        // Convertir a DTOs y retornar
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        // Obtener cursos paginados
        Page<Course> coursesPage = courseRepository.findAll(pageable);
        
        // Convertir a DTOs y retornar
        return coursesPage.map(this::convertToDTO);
    }
    
    @Override
    public List<CourseDTO> getCoursesByProfessor(Long professorId) {
        // 1. Validar que el profesor exista
        if (!professorRepository.existsById(professorId)) {
            throw new IllegalArgumentException("El profesor con ID " + professorId + " no existe");
        }
        
        // 2. Buscar cursos por profesor
        List<Course> courses = courseRepository.findByProfessorId(professorId);
        
        // 3. Convertir a DTOs y retornar
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<CourseDTO> getCoursesByProfessor(Long professorId, Pageable pageable) {
        // 1. Validar que el profesor exista
        if (!professorRepository.existsById(professorId)) {
            throw new IllegalArgumentException("El profesor con ID " + professorId + " no existe");
        }
        
        // 2. Buscar cursos por profesor con paginación
        Page<Course> coursesPage = courseRepository.findByProfessorId(professorId, pageable);
        
        // 3. Convertir a DTOs y retornar
        return coursesPage.map(this::convertToDTO);
    }
    
    @Override
    public CourseDTO getCourseById(Long id) {
        // 1. Buscar curso por ID
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + id + " no existe"));
        
        // 2. Convertir a DTO y retornar
        return convertToDTO(course);
    }
    
    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        // TODO: Implementar actualización de curso
        throw new UnsupportedOperationException("updateCourse aún no implementado.");
    }
    
    @Override
    public void deleteCourse(Long id) {
        // 1. Validar que el curso exista
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("El curso con ID " + id + " no existe");
        }
        
        // 2. Eliminar curso (las relaciones se eliminan en cascada)
        courseRepository.deleteById(id);
    }
    
    /**
     * Convierte una entidad Course a CourseDTO
     */
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setSchool(course.getSchool());
        dto.setDescription(course.getDescription());
        dto.setProfessorId(course.getProfessorId());
        return dto;
    }
    
    /**
     * Convierte un CourseDTO a entidad Course
     */
    private Course convertToEntity(CourseDTO dto) {
        Course course = new Course();
        course.setId(dto.getId()); // null para nuevos cursos
        course.setName(dto.getName());
        course.setSchool(dto.getSchool());
        course.setDescription(dto.getDescription());
        course.setProfessorId(dto.getProfessorId());
        return course;
    }
}

