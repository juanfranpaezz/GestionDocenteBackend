package com.gestion.docente.backend.Gestion.Docente.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupedAverageDTO {
    private Long evaluationTypeId;
    private String evaluationTypeName;
    private Double average; // Promedio del grupo de evaluaciones de este tipo
    private Integer evaluationsCount; // Cantidad de evaluaciones de este tipo con notas
    private List<Long> evaluationIds; // IDs de las evaluaciones incluidas en este promedio
}

