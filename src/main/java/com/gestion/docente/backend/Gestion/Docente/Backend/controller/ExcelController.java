package com.gestion.docente.backend.Gestion.Docente.Backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.docente.backend.Gestion.Docente.Backend.service.ExcelService;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    
    @Autowired
    private ExcelService excelService;
    
    /**
     * GET /api/excel/courses/{courseId}/grades
     * Genera y descarga un archivo Excel con las notas de un curso.
     * El curso debe pertenecer al profesor autenticado (obtenido del JWT).
     * 
     * El archivo Excel incluye:
     * - Información del curso
     * - Lista de estudiantes
     * - Notas por evaluación
     * - Promedios agrupados por tipo de evaluación
     * - Promedio final por estudiante
     */
    @GetMapping("/courses/{courseId}/grades")
    public ResponseEntity<?> generateGradesExcel(@PathVariable Long courseId) {
        try {
            ByteArrayResource resource = excelService.generateGradesExcel(courseId);
            
            String fileName = excelService.generateGradesFileName(courseId);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al generar el archivo Excel: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

