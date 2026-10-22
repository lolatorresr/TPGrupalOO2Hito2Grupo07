package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("personaRepository")
public interface IPersonaRepository extends JpaRepository<Persona, Serializable> {

    public abstract List<Persona> findByCredencialEnabledTrue();

    public abstract Optional<Persona> findByDni(long dni);

    public abstract Optional<Persona> findByEmail(String email);
}
