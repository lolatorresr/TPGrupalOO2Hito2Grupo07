package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.ParametrosNegocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository("parametrosNegocioRepository")
public interface IParametrosNegocioRepository extends JpaRepository<ParametrosNegocio, Serializable> {

    /** La fila vigente (hay una sola, cargada por data.sql). */
    public abstract Optional<ParametrosNegocio> findFirstByOrderByIdParametroAsc();
}