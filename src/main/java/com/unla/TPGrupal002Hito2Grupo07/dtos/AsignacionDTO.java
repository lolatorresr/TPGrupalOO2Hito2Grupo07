package com.unla.TPGrupal002Hito2Grupo07.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/** Unidad de venta y festival en la que trabaja un empleado, solo lectura */
@Getter
@AllArgsConstructor
public class AsignacionDTO {
    private String nombreUnidad;
    private String codigoUnidad;
    private String tipoUnidad;
    private String festivalNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    /** true si hoy esta dentro de las fechas del festival */
    private boolean enCurso;
}