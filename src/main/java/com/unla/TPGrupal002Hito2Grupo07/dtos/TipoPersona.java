package com.unla.TPGrupal002Hito2Grupo07.dtos;

/** Tipos de empleado que se pueden dar de alta desde el formulario. */
public enum TipoPersona {
    CAJERO("Cajero"),
    COCINERO("Cocinero");

    private final String descripcion;

    TipoPersona(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
