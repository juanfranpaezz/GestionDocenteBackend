package com.gestion.docente.backend.Gestion.Docente.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "course_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long courseId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // MONDAY, TUESDAY, etc.
    
    @Column(nullable = false)
    private LocalTime startTime; // ej: 10:00
    
    @Column(nullable = false)
    private LocalTime endTime; // ej: 12:00
    
    @Column(nullable = true)
    private Long subjectId; // Relación con Subject (nullable para compatibilidad)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId", insertable = false, updatable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subjectId", insertable = false, updatable = false)
    private Subject subject;
}

