package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("personaRepository")
public interface IPersonaRepository extends JpaRepository<Persona, Serializable> {

    /** Personas activas (cualquier tipo). Lo usa el formulario de unidades para elegir responsable. */
    public abstract List<Persona> findByCredencialEnabledTrue();

    /** Empleados activos (cajeros y cocineros): el responsable no figura entre sus empleados. */
    @Query("SELECT p FROM Persona p WHERE p.credencial.enabled = true AND TYPE(p) <> Responsable")
    public abstract List<Persona> findEmpleadosActivos();

    /** Empleados dados de baja (con credencial deshabilitada), sin incluir responsables. */
    @Query("SELECT p FROM Persona p WHERE p.credencial IS NOT NULL AND p.credencial.enabled = false AND TYPE(p) <> Responsable")
    public abstract List<Persona> findEmpleadosInactivos();
    public abstract Optional<Persona> findByDni(long dni);

    public abstract Optional<Persona> findByEmail(String email);

    /** Persona de la credencial con ese username (el empleado logueado). */
    public abstract Optional<Persona> findByCredencial_Username(String username);
}
