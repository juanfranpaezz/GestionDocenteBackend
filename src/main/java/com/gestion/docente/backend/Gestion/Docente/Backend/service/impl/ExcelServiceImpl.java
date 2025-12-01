package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.GroupedAverageDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.dto.StudentGroupedAveragesDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Course;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Evaluation;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Grade;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Student;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.CourseRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.EvaluationRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.GradeRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.StudentRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.ExcelService;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.GradeService;

/**
 * Implementación del servicio para generar archivos Excel.
 * Genera archivos Excel con las notas de un curso.
 */
@Service
@Transactional
public class ExcelServiceImpl implements ExcelService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private GradeService gradeService;
    
    @Override
    public ByteArrayResource generateGradesExcel(Long courseId) {
        // 1. Validar ownership: el curso debe pertenecer al profesor autenticado
        SecurityUtils.validateNotAdmin();
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + courseId + " no existe"));
        
        if (!course.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene acceso a este curso");
        }
        
        // 2. Obtener datos necesarios
        List<Student> students = studentRepository.findByCourseId(courseId);
        List<Evaluation> evaluations = evaluationRepository.findByCourseId(courseId);
        List<StudentGroupedAveragesDTO> groupedAverages = gradeService.getGroupedAveragesByCourse(courseId);
        
        // 3. Crear mapa de estudiantes para acceso rápido
        Map<Long, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getId, s -> s));
        
        // 4. Crear mapa de notas por estudiante y evaluación
        Map<Long, Map<Long, Grade>> gradesMap = new HashMap<>();
        List<Grade> allGrades = gradeRepository.findByCourseId(courseId);
        for (Grade grade : allGrades) {
            gradesMap.computeIfAbsent(grade.getStudentId(), k -> new HashMap<>())
                    .put(grade.getEvaluationId(), grade);
        }
        
        // 5. Obtener tipos de evaluación únicos
        Set<String> evaluationTypeNames = new LinkedHashSet<>();
        for (StudentGroupedAveragesDTO grouped : groupedAverages) {
            if (grouped.getGroupedAverages() != null) {
                for (GroupedAverageDTO avg : grouped.getGroupedAverages()) {
                    if (avg.getEvaluationTypeName() != null) {
                        evaluationTypeNames.add(avg.getEvaluationTypeName());
                    }
                }
            }
        }
        List<String> typeNamesList = new ArrayList<>(evaluationTypeNames);
        
        // 6. Generar Excel
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Notas");
            
            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            
            CellStyle numberStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            numberStyle.setDataFormat(format.getFormat("0.00"));
            
            // Título del curso
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Planilla de Notas - " + course.getName());
            titleCell.setCellStyle(boldStyle);
            
            // Fila de información del curso
            Row infoRow = sheet.createRow(1);
            infoRow.createCell(0).setCellValue("Escuela: " + course.getSchool());
            if (course.getDescription() != null && !course.getDescription().isEmpty()) {
                infoRow.createCell(1).setCellValue("Descripción: " + course.getDescription());
            }
            
            // Encabezados
            int headerRowNum = 3;
            Row headerRow = sheet.createRow(headerRowNum);
            int colIndex = 0;
            
            // Columna de estudiante
            Cell studentHeader = headerRow.createCell(colIndex++);
            studentHeader.setCellValue("Estudiante");
            studentHeader.setCellStyle(headerStyle);
            
            // Columnas de evaluaciones
            for (Evaluation eval : evaluations) {
                Cell evalHeader = headerRow.createCell(colIndex++);
                evalHeader.setCellValue(eval.getNombre() != null ? eval.getNombre() : "Evaluación " + eval.getId());
                evalHeader.setCellStyle(headerStyle);
            }
            
            // Columnas de promedios por tipo
            for (String typeName : typeNamesList) {
                Cell typeHeader = headerRow.createCell(colIndex++);
                typeHeader.setCellValue("Prom. " + typeName);
                typeHeader.setCellStyle(headerStyle);
            }
            
            // Columna de promedio final
            Cell finalHeader = headerRow.createCell(colIndex);
            finalHeader.setCellValue("Prom. Final");
            finalHeader.setCellStyle(headerStyle);
            
            // Datos por estudiante
            int rowIndex = headerRowNum + 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowIndex++);
                colIndex = 0;
                
                // Nombre del estudiante
                String fullName = (student.getFirstName() != null ? student.getFirstName() : "") +
                        (student.getLastName() != null ? " " + student.getLastName() : "");
                row.createCell(colIndex++).setCellValue(fullName.trim().isEmpty() ? 
                        "Estudiante " + student.getId() : fullName.trim());
                
                // Notas por evaluación
                for (Evaluation eval : evaluations) {
                    Cell gradeCell = row.createCell(colIndex++);
                    Map<Long, Grade> studentGrades = gradesMap.get(student.getId());
                    if (studentGrades != null) {
                        Grade grade = studentGrades.get(eval.getId());
                        if (grade != null) {
                            if (grade.getGrade() != null) {
                                gradeCell.setCellValue(grade.getGrade());
                                gradeCell.setCellStyle(numberStyle);
                            } else if (grade.getGradeValue() != null) {
                                gradeCell.setCellValue(grade.getGradeValue());
                            } else {
                                gradeCell.setCellValue("");
                            }
                        } else {
                            gradeCell.setCellValue("");
                        }
                    } else {
                        gradeCell.setCellValue("");
                    }
                }
                
                // Promedios agrupados por tipo
                StudentGroupedAveragesDTO studentGrouped = groupedAverages.stream()
                        .filter(g -> g.getStudentId().equals(student.getId()))
                        .findFirst()
                        .orElse(null);
                
                for (String typeName : typeNamesList) {
                    Cell typeAvgCell = row.createCell(colIndex++);
                    if (studentGrouped != null && studentGrouped.getGroupedAverages() != null) {
                        Optional<GroupedAverageDTO> typeAvg = studentGrouped.getGroupedAverages().stream()
                                .filter(ga -> typeName.equals(ga.getEvaluationTypeName()))
                                .findFirst();
                        if (typeAvg.isPresent() && typeAvg.get().getAverage() != null) {
                            typeAvgCell.setCellValue(typeAvg.get().getAverage());
                            typeAvgCell.setCellStyle(numberStyle);
                        } else {
                            typeAvgCell.setCellValue("");
                        }
                    } else {
                        typeAvgCell.setCellValue("");
                    }
                }
                
                // Promedio final
                Cell finalAvgCell = row.createCell(colIndex);
                if (studentGrouped != null && studentGrouped.getFinalAverage() != null) {
                    finalAvgCell.setCellValue(studentGrouped.getFinalAverage());
                    CellStyle finalAvgStyle = workbook.createCellStyle();
                    finalAvgStyle.setDataFormat(format.getFormat("0.00"));
                    Font finalFont = workbook.createFont();
                    finalFont.setBold(true);
                    finalAvgStyle.setFont(finalFont);
                    finalAvgCell.setCellStyle(finalAvgStyle);
                } else {
                    finalAvgCell.setCellValue("");
                }
            }
            
            // Ajustar ancho de columnas
            for (int i = 0; i <= colIndex; i++) {
                sheet.autoSizeColumn(i);
                // Asegurar un ancho mínimo
                int currentWidth = sheet.getColumnWidth(i);
                if (currentWidth < 2000) {
                    sheet.setColumnWidth(i, 2000);
                }
            }
            
            // 7. Convertir a ByteArrayResource
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
            
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo Excel: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generateGradesFileName(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + courseId + " no existe"));
        
        // Limpiar nombre del curso para usar en nombre de archivo
        String courseName = course.getName()
                .replaceAll("[^a-zA-Z0-9\\s]", "") // Eliminar caracteres especiales
                .replaceAll("\\s+", "_") // Reemplazar espacios con guiones bajos
                .trim();
        
        // Agregar fecha actual
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return "Notas_" + courseName + "_" + dateStr + ".xlsx";
    }
}

