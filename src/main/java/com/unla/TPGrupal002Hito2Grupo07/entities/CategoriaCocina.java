package com.unla.TPGrupal002Hito2Grupo07.entities;

/** Categorías de un cocinero. Los montos de cada una están en ParametrosNegocio. */
public enum CategoriaCocina {
    COCINERO("Cocinero"),
    COCINERO_AYUDANTE("Cocinero ayudante"),
    LAVAPLATOS("Lavaplatos");

    private final String descripcion;

    CategoriaCocina(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
