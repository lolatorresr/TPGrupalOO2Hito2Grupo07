package com.unla.TPGrupal002Hito2Grupo07.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Caja de una unidad en un día: sirve tanto para el resumen antes de cerrar
 * (cerrada = false) como para un cierre ya guardado (cerrada = true). No persiste.
 */
@Getter @Setter @NoArgsConstructor
public class CierreCajaDTO {
    private int idCierreCaja;
    private LocalDate fecha;
    private int unidadId;
    private String unidadNombre;
    private String unidadCodigo;
    private String festivalNombre;
    /** Quién cerró la caja (vacío si todavía no se cerro). */
    private String cajeroNombre;
    private long cantidadPedidos;
    private double montoTotal;
    /** true si la caja de ese dia ya esta cerrada. */
    private boolean cerrada;
}