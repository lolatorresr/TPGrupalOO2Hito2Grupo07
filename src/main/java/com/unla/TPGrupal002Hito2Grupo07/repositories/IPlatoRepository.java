package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("platoRepository")
public interface IPlatoRepository extends JpaRepository<Plato, Serializable> {

    public abstract Plato findByNombre(String nombre);
}
