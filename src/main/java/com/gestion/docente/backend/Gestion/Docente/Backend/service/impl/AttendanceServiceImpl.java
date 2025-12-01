package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.AttendanceDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Attendance;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.AttendanceRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private com.gestion.docente.backend.Gestion.Docente.Backend.repository.StudentRepository studentRepository;
    
    @Override
    public List<AttendanceDTO> getAttendancesByCourse(Long courseId) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        List<Attendance> attendances = attendanceRepository.findByCourseId(courseId);
        return attendances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AttendanceDTO> getAttendancesByStudent(Long studentId) {
        // Obtener el profesor autenticado
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // Obtener todas las asistencias del estudiante
        List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
        
        // Filtrar solo las asistencias de cursos que pertenecen al profesor autenticado
        return attendances.stream()
                .filter(attendance -> {
                    Course course = courseRepository.findById(attendance.getCourseId())
                            .orElse(null);
                    return course != null && course.getProfessorId().equals(currentProfessorId);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public AttendanceDTO markAttendance(AttendanceDTO attendanceDTO) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(attendanceDTO.getCourseId());
        
        // Crear nueva entidad Attendance
        Attendance attendance = convertToEntity(attendanceDTO);
        
        // Guardar en la base de datos
        Attendance savedAttendance = attendanceRepository.save(attendance);
        
        // Convertir a DTO y retornar
        return convertToDTO(savedAttendance);
    }
    
    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO) {
        // 1. Buscar la asistencia existente
        Attendance existingAttendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La asistencia con ID " + id + " no existe"));
        
        // 2. Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(existingAttendance.getCourseId());
        
        // 3. Validar que el courseId del DTO coincida con el existente (no se puede cambiar)
        if (!existingAttendance.getCourseId().equals(attendanceDTO.getCourseId())) {
            throw new IllegalArgumentException("No se puede cambiar el curso de una asistencia");
        }
        
        // 4. Actualizar los campos
        existingAttendance.setDate(attendanceDTO.getDate());
        existingAttendance.setPresent(attendanceDTO.getPresent());
        existingAttendance.setStudentId(attendanceDTO.getStudentId());
        
        // 5. Guardar los cambios
        Attendance updatedAttendance = attendanceRepository.save(existingAttendance);
        
        // 6. Convertir a DTO y retornar
        return convertToDTO(updatedAttendance);
    }
    
    @Override
    public Double calculateAttendancePercentage(Long studentId, Long courseId) {
        // Validar ownership: el curso debe pertenecer al profesor autenticado
        validateCourseOwnership(courseId);
        
        // Obtener todas las asistencias del estudiante en el curso
        List<Attendance> attendances = attendanceRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        if (attendances.isEmpty()) {
            return 0.0;
        }
        
        // Contar las asistencias presentes
        long presentCount = attendances.stream()
                .filter(Attendance::getPresent)
                .count();
        
        // Calcular porcentaje
        return (double) presentCount / attendances.size() * 100.0;
    }
    
    @Override
    public List<com.gestion.docente.backend.Gestion.Docente.Backend.dto.AttendanceAverageDTO> getAttendanceAverages(Long courseId, Long subjectId) {
        validateCourseOwnership(courseId);
        
        List<com.gestion.docente.backend.Gestion.Docente.Backend.model.Student> students = 
            studentRepository.findByCourseId(courseId);
        
        return students.stream()
                .map(student -> {
                    // Filtrar por materia si se proporciona
                    List<Attendance> attendances;
                    if (subjectId != null) {
                        attendances = attendanceRepository.findByStudentIdAndCourseIdAndSubjectId(
                                student.getId(), courseId, subjectId);
                    } else {
                        attendances = attendanceRepository.findByStudentIdAndCourseId(
                                student.getId(), courseId);
                    }
                    
                    int totalDays = attendances.size();
                    int presentDays = (int) attendances.stream()
                            .filter(Attendance::getPresent)
                            .count();
                    int absentDays = totalDays - presentDays;
                    
                    Double percentage = null;
                    if (totalDays > 0) {
                        percentage = (double) presentDays / totalDays * 100.0;
                    }
                    
                    com.gestion.docente.backend.Gestion.Docente.Backend.dto.AttendanceAverageDTO dto = 
                        new com.gestion.docente.backend.Gestion.Docente.Backend.dto.AttendanceAverageDTO();
                    dto.setStudentId(student.getId());
                    dto.setFirstName(student.getFirstName());
                    dto.setLastName(student.getLastName());
                    dto.setAttendancePercentage(percentage);
                    dto.setTotalDays(totalDays);
                    dto.setPresentDays(presentDays);
                    dto.setAbsentDays(absentDays);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AttendanceDTO> saveAttendancesBulk(List<AttendanceDTO> attendancesDTO) {
        if (attendancesDTO == null || attendancesDTO.isEmpty()) {
            throw new IllegalArgumentException("La lista de asistencias no puede estar vacía");
        }
        
        // Validar ownership del primer curso (todos deben ser del mismo curso)
        Long courseId = attendancesDTO.get(0).getCourseId();
        validateCourseOwnership(courseId);
        
        // Validar que todos sean del mismo curso
        for (AttendanceDTO dto : attendancesDTO) {
            if (!courseId.equals(dto.getCourseId())) {
                throw new IllegalArgumentException("Todas las asistencias deben ser del mismo curso");
            }
        }
        
        List<Attendance> attendances = attendancesDTO.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        
        // Log para depuración
        System.out.println("📝 Guardando " + attendances.size() + " asistencias");
        attendances.forEach(att -> {
            System.out.println("  - Asistencia - studentId: " + att.getStudentId() + ", date: " + att.getDate() + ", subjectId: " + att.getSubjectId());
        });
        
        List<Attendance> saved = attendanceRepository.saveAll(attendances);
        attendanceRepository.flush(); // Forzar flush para asegurar persistencia
        
        System.out.println("✅ Asistencias guardadas - total: " + saved.size());
        saved.forEach(att -> {
            System.out.println("  - Guardada - id: " + att.getId() + ", studentId: " + att.getStudentId() + ", subjectId: " + att.getSubjectId());
        });
        
        return saved.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
    
    /**
     * Convierte una entidad Attendance a AttendanceDTO
     */
    private AttendanceDTO convertToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setDate(attendance.getDate());
        dto.setPresent(attendance.getPresent());
        dto.setCourseId(attendance.getCourseId());
        dto.setStudentId(attendance.getStudentId());
        dto.setSubjectId(attendance.getSubjectId()); // Agregar subjectId
        return dto;
    }
    
    /**
     * Convierte un AttendanceDTO a entidad Attendance
     */
    private Attendance convertToEntity(AttendanceDTO dto) {
        Attendance attendance = new Attendance();
        attendance.setId(dto.getId());
        attendance.setDate(dto.getDate());
        attendance.setPresent(dto.getPresent());
        attendance.setCourseId(dto.getCourseId());
        attendance.setStudentId(dto.getStudentId());
        attendance.setSubjectId(dto.getSubjectId()); // Agregar subjectId
        return attendance;
    }
}

