package com.unla.TPGrupal002Hito2Grupo07.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * Recaudación de un festival: total de los pedidos y detalle por unidad de venta.
 * Es un DTO de reporte: no se persiste, se arma a partir de los pedidos del festival.
 * Para el responsable solo incluye sus unidades; para el administrador, todas.
 */
@Getter
@AllArgsConstructor
public class ReporteVentaDTO {
    private int idFestival;
    private String festivalNombre;
    private String temporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cantidadUnidades;
    private long cantidadPedidos;
    private double recaudacionTotal;
    /** Recaudación de cada unidad incluida en el reporte, de mayor a menor */
    private List<ReporteRankingUnidadDTO> detalle;
}
