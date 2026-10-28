package com.unla.TPGrupal002Hito2Grupo07.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Fila del ranking de unidades de un festival ordenado por recaudación.
 * Es un DTO de reporte: no se persiste, se arma a partir de los pedidos.
 */
@Getter
@AllArgsConstructor
public class ReporteRankingUnidadDTO {
    private int posicion;
    private int idUnidad;
    private String nombreComercial;
    private String codigo;
    private String tipoUnidad;
    private String responsable;
    private long cantidadPedidos;
    private double recaudacion;
}
