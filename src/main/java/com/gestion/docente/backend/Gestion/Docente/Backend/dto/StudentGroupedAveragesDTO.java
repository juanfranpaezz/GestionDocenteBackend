package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGroupedAveragesDTO {
    private Long studentId;
    private String firstName;
    private String lastName;
    private List<GroupedAverageDTO> groupedAverages; // Promedios por tipo de evaluación
    private Double finalAverage; // Promedio final (promedio de los promedios de cada grupo)
}

