package com.gestion.docente.backend.Gestion.Docente.Backend.repository;

import com.gestion.docente.backend.Gestion.Docente.Backend.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByCourseId(Long courseId);
    
    List<Attendance> findByStudentId(Long studentId);
    
    List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId);
}

