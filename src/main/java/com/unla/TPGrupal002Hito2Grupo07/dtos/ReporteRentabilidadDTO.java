package com.unla.TPGrupal002Hito2Grupo07.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * Rentabilidad neta de una unidad de venta en un festival:
 * ventas (pedidos) - costo de los platos vendidos - sueldos del staff - canon.
 * Es un DTO de reporte: no se persiste. Si el período es menor al festival,
 * los sueldos y el canon se toman en proporción a los días del período.
 */
@Getter
@AllArgsConstructor
public class ReporteRentabilidadDTO {
    private String festivalNombre;
    private String nombreComercial;
    private String codigo;
    private String tipoUnidad;
    /** Período considerado (todo el festival o el rango pedido) */
    private LocalDate desde;
    private LocalDate hasta;
    private long diasFestival;
    private long diasPeriodo;
    private long cantidadPedidos;
    private double ventas;
    private double costoPlatos;
    private double sueldos;
    private double canon;
    private double rentabilidad;
    private List<ReporteSueldoDTO> detalleSueldos;

    /** true si el período no abarca todo el festival (sueldos y canon prorrateados) */
    public boolean isProrrateado() {
        return diasPeriodo < diasFestival;
    }
}
