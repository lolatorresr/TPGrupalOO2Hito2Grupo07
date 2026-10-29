package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.entities.ParametrosNegocio;

public interface IParametrosNegocioService {

    /** Parámetros vigentes del negocio (sueldos y canon), leídos de la base. */
    public ParametrosNegocio getParametros();
}