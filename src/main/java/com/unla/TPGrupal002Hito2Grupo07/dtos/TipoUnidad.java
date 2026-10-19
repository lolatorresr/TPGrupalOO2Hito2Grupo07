package com.unla.TPGrupal002Hito2Grupo07.dtos;

/** Tipos de unidad de venta que se pueden dar de alta desde el formulario. */
public enum TipoUnidad {
    FOOD_TRUCK("Food Truck", "FT-"),
    PUESTO_DESARMABLE("Puesto Desarmable", "PD-");

    private final String descripcion;
    private final String prefijoCodigo;

    TipoUnidad(String descripcion, String prefijoCodigo) {
        this.descripcion = descripcion;
        this.prefijoCodigo = prefijoCodigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Prefijo con el que debe empezar el código de una unidad de este tipo. */
    public String getPrefijoCodigo() {
        return prefijoCodigo;
    }
}
