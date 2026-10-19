package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository("personaRepository")
public interface IPersonaRepository extends JpaRepository<Persona, Serializable> {

    public abstract List<Persona> findByActivoTrue();
}
