package com.unla.TPGrupal002Hito2Grupo07.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Sueldo de un empleado del staff de una unidad en un festival, dentro del reporte de rentabilidad. */
@Getter
@AllArgsConstructor
public class ReporteSueldoDTO {
    private String empleado;
    private String puesto;
    private double sueldo;
}
