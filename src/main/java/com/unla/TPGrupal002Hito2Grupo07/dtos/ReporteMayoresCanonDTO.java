package com.unla.TPGrupal002Hito2Grupo07.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Unidad de venta con su canon, para el reporte de las "x" unidades que más
 * gastaron en canon en un festival. Es un DTO de reporte: no se persiste.
 */
@Getter
@AllArgsConstructor
public class ReporteMayoresCanonDTO {
    private int posicion;
    private String nombreComercial;
    private String codigo;
    private String tipoUnidad;
    private double canon;
}
