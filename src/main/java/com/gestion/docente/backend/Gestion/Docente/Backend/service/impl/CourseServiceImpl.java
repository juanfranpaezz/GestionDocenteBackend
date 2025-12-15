package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.CourseDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.ProfessorRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.CourseService;
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
    
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final com.gestion.docente.backend.Gestion.Docente.Backend.repository.StudentRepository studentRepository;
    private final com.gestion.docente.backend.Gestion.Docente.Backend.repository.SubjectRepository subjectRepository;
    private final com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailService emailService;
    private final com.gestion.docente.backend.Gestion.Docente.Backend.service.CourseDuplicationService courseDuplicationService;
    
    // Inyección por constructor (Dependency Inversion Principle)
    public CourseServiceImpl(
            CourseRepository courseRepository,
            ProfessorRepository professorRepository,
            com.gestion.docente.backend.Gestion.Docente.Backend.repository.StudentRepository studentRepository,
            com.gestion.docente.backend.Gestion.Docente.Backend.repository.SubjectRepository subjectRepository,
            com.gestion.docente.backend.Gestion.Docente.Backend.service.EmailService emailService,
            com.gestion.docente.backend.Gestion.Docente.Backend.service.CourseDuplicationService courseDuplicationService) {
        this.courseRepository = courseRepository;
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.emailService = emailService;
        this.courseDuplicationService = courseDuplicationService;
    }
    
    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        // 1. Validar que el usuario NO sea administrador
        SecurityUtils.validateNotAdmin();
        
        // 2. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 3. Si se envía professorId en el DTO, validar que coincida con el del JWT
        // (por seguridad, para evitar que alguien intente crear cursos para otros profesores)
        if (courseDTO.getProfessorId() != null && !currentProfessorId.equals(courseDTO.getProfessorId())) {
            throw new IllegalArgumentException("No puede crear cursos para otros profesores");
        }
        
        // 4. Validar que el profesor exista
        if (!professorRepository.existsById(currentProfessorId)) {
            throw new IllegalArgumentException("El profesor autenticado no existe en la base de datos");
        }
        
        // 5. Asignar el professorId del JWT al DTO (ignorar el que venga en el DTO si existe)
        courseDTO.setProfessorId(currentProfessorId);
        
        // 6. Crear nueva entidad Course
        Course course = convertToEntity(courseDTO);
        
        // 7. Guardar en la base de datos
        Course savedCourse = courseRepository.save(course);
        
        // 8. Crear automáticamente una materia default sin nombre para el curso
        com.gestion.docente.backend.Gestion.Docente.Backend.model.Subject defaultSubject = 
            new com.gestion.docente.backend.Gestion.Docente.Backend.model.Subject();
        defaultSubject.setName(null); // Materia sin nombre, el profesor puede renombrarla después
        defaultSubject.setCourseId(savedCourse.getId());
        subjectRepository.save(defaultSubject);
        
        // 9. Convertir a DTO y retornar
        return convertToDTO(savedCourse);
    }
    
    @Override
    public List<CourseDTO> getAllCourses() {
        // Validar que el usuario NO sea administrador
        SecurityUtils.validateNotAdmin();
        
        // Obtener el profesor autenticado y filtrar solo sus cursos NO archivados
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        List<Course> courses = courseRepository.findByProfessorIdAndArchived(currentProfessorId, false);
        
        // Convertir a DTOs y retornar
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        // Validar que el usuario NO sea administrador
        SecurityUtils.validateNotAdmin();
        
        // Obtener el profesor autenticado y filtrar solo sus cursos
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        Page<Course> coursesPage = courseRepository.findByProfessorId(currentProfessorId, pageable);
        
        // Convertir a DTOs y retornar
        return coursesPage.map(this::convertToDTO);
    }
    
    @Override
    public CourseDTO getCourseById(Long id) {
        // 1. Validar que el usuario NO sea administrador
        SecurityUtils.validateNotAdmin();
        
        // 2. Obtener el profesor autenticado
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 3. Buscar curso por ID
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + id + " no existe"));
        
        // 4. Validar ownership: el curso debe pertenecer al profesor autenticado
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene acceso a este curso");
        }
        
        // 5. Convertir a DTO y retornar
        return convertToDTO(course);
    }
    
    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        // 1. Validar que el usuario NO sea administrador
        SecurityUtils.validateNotAdmin();
        
        // 2. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 3. Buscar el curso existente
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + id + " no existe"));
        
        // 4. Validar ownership: el curso debe pertenecer al profesor autenticado
        if (!existingCourse.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede actualizar cursos de otros profesores");
        }
        
        // 5. Validar que el professorId del DTO coincida con el del JWT (si se envía)
        if (courseDTO.getProfessorId() != null && !currentProfessorId.equals(courseDTO.getProfessorId())) {
            throw new IllegalArgumentException("No puede cambiar el profesor del curso");
        }
        
        // 6. Actualizar los campos del curso
        existingCourse.setName(courseDTO.getName());
        existingCourse.setSchool(courseDTO.getSchool());
        existingCourse.setDescription(courseDTO.getDescription());
        if (courseDTO.getApprovalGrade() != null) {
            existingCourse.setApprovalGrade(courseDTO.getApprovalGrade());
        }
        if (courseDTO.getQualificationGrade() != null) {
            existingCourse.setQualificationGrade(courseDTO.getQualificationGrade());
        }
        // El professorId no se cambia, se mantiene el original
        
        // 7. Guardar los cambios
        Course updatedCourse = courseRepository.save(existingCourse);
        
        // 8. Convertir a DTO y retornar
        return convertToDTO(updatedCourse);
    }
    
    @Override
    public void deleteCourse(Long id) {
        // 1. Validar que el usuario NO sea administrador
        SecurityUtils.validateNotAdmin();
        
        // 2. Obtener el profesor autenticado
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 3. Buscar curso por ID
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + id + " no existe"));
        
        // 4. Validar ownership: el curso debe pertenecer al profesor autenticado
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede eliminar cursos de otros profesores");
        }
        
        // 5. Eliminar curso (las relaciones se eliminan en cascada)
        courseRepository.deleteById(id);
    }
    
    @Override
    public CourseDTO archiveCourse(Long id) {
        validateCourseOwnership(id);
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + id + " no existe"));
        
        course.setArchived(true);
        course.setArchivedDate(java.time.LocalDateTime.now());
        
        Course saved = courseRepository.save(course);
        return convertToDTO(saved);
    }
    
    @Override
    public CourseDTO unarchiveCourse(Long id) {
        validateCourseOwnership(id);
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + id + " no existe"));
        
        course.setArchived(false);
        course.setArchivedDate(null);
        
        Course saved = courseRepository.save(course);
        return convertToDTO(saved);
    }
    
    @Override
    public List<CourseDTO> getArchivedCourses() {
        SecurityUtils.validateNotAdmin();
        
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        List<Course> courses = courseRepository.findByProfessorIdAndArchived(currentProfessorId, true);
        
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public CourseDTO duplicateCourse(Long id, com.gestion.docente.backend.Gestion.Docente.Backend.dto.DuplicateCourseDTO options) {
        validateCourseOwnership(id);
        
        Course original = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + id + " no existe"));
        
        // Delegar la duplicación al servicio especializado
        Course duplicatedCourse = courseDuplicationService.duplicateCourse(original, options);
        
        return convertToDTO(duplicatedCourse);
    }
    
    @Override
    public List<CourseDTO> searchCourses(String searchQuery, Boolean archived) {
        SecurityUtils.validateNotAdmin();
        
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        Boolean archivedValue = (archived != null) ? archived : false;
        
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return courseRepository.findByProfessorIdAndArchived(currentProfessorId, archivedValue).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        
        String query = searchQuery.trim().toLowerCase();
        List<Course> courses = courseRepository.findByProfessorIdAndArchived(currentProfessorId, archivedValue);
        
        return courses.stream()
                .filter(course -> 
                    course.getName().toLowerCase().contains(query) || 
                    course.getSchool().toLowerCase().contains(query)
                )
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
        dto.setArchived(course.getArchived());
        dto.setArchivedDate(course.getArchivedDate());
        dto.setApprovalGrade(course.getApprovalGrade());
        dto.setQualificationGrade(course.getQualificationGrade());
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
        course.setArchived(dto.getArchived() != null ? dto.getArchived() : false);
        course.setArchivedDate(dto.getArchivedDate());
        course.setApprovalGrade(dto.getApprovalGrade());
        course.setQualificationGrade(dto.getQualificationGrade());
        return course;
    }
    
    /**
     * Valida que un curso pertenezca al profesor autenticado.
     * 
     * @param courseId ID del curso a validar
     * @throws IllegalStateException si el usuario es administrador
     * @throws IllegalArgumentException si el curso no existe o no pertenece al profesor autenticado
     */
    private void validateCourseOwnership(Long courseId) {
        // Validar que el usuario NO sea administrador
        SecurityUtils.validateNotAdmin();
        
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + courseId + " no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene acceso a este curso");
        }
    }
    
    @Override
    public void sendPersonalizedMessageToAllStudents(Long courseId, com.gestion.docente.backend.Gestion.Docente.Backend.dto.SendPersonalizedMessageDTO messageDTO) {
        // 1. Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        // 2. Obtener el curso
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + courseId + " no existe"));
        
        // 3. Obtener información del profesor
        com.gestion.docente.backend.Gestion.Docente.Backend.model.Professor professor = 
            professorRepository.findById(course.getProfessorId())
                .orElseThrow(() -> new IllegalArgumentException("El profesor no existe"));
        String professorName = professor.getName() + 
            (professor.getLastname() != null ? " " + professor.getLastname() : "");
        String professorEmail = professor.getEmail();
        
        // 4. Obtener todos los estudiantes del curso
        List<com.gestion.docente.backend.Gestion.Docente.Backend.model.Student> students = 
            studentRepository.findByCourseId(courseId);
        
        // 5. Enviar email a cada estudiante
        int emailsSent = 0;
        int emailsFailed = 0;
        
        for (com.gestion.docente.backend.Gestion.Docente.Backend.model.Student student : students) {
            if (student.getEmail() != null && !student.getEmail().trim().isEmpty()) {
                try {
                    String studentName = student.getFirstName() + 
                            (student.getLastName() != null ? " " + student.getLastName() : "");
                    emailService.sendPersonalizedMessageToAllStudents(
                            student.getEmail(),
                            studentName,
                            course.getName(),
                            messageDTO.getSubject(),
                            messageDTO.getMessage(),
                            professorName,
                            professorEmail
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
        
        System.out.println("Envío de mensajes personalizados completado. Enviados: " + emailsSent + ", Fallidos: " + emailsFailed);
    }
}

