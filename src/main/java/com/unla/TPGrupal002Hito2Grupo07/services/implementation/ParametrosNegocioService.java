package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.entities.ParametrosNegocio;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IParametrosNegocioRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IParametrosNegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("parametrosNegocioService")
public class ParametrosNegocioService implements IParametrosNegocioService {

    @Autowired
    @Qualifier("parametrosNegocioRepository")
    private IParametrosNegocioRepository parametrosRepository;

    @Override
    @Transactional(readOnly = true)
    public ParametrosNegocio getParametros() {
        return parametrosRepository.findFirstByOrderByIdParametroAsc()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay parámetros de negocio cargados (tabla parametros_negocio, ver data.sql)."));
    }
}