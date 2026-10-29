package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.dtos.AsignacionDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.TipoPersona;
import com.unla.TPGrupal002Hito2Grupo07.entities.CategoriaCocina;

import java.util.List;

public interface IPersonaService {

    /** Empleados activos (con la credencial habilitada). */
    public List<PersonaDTO> getAllActivas();

    /** Empleados dados de baja lógicamente (se pueden reactivar). */
    public List<PersonaDTO> getAllInactivas();

    /**
     * Empleados activos filtrados por uno o varios atributos. Los parámetros vacíos o null
     * no filtran. Solo trae empleados no eliminados lógicamente (y sin responsables).
     */
    public List<PersonaDTO> filtrarActivas(String texto, String dni, TipoPersona tipo,
                                           CategoriaCocina categoria, String turno);


    /** Empleados activos de las unidades del responsable, filtrados por uno o varios atributos. */
    public List<PersonaDTO> filtrarActivasDelResponsable(int idResponsable, String texto, String dni,
                                                         TipoPersona tipo, CategoriaCocina categoria, String turno);

    /** Empleados dados de baja que trabajaban en unidades del responsable (se pueden reactivar). */
    public List<PersonaDTO> getInactivasDelResponsable(int idResponsable);

    /** true si la persona trabaja en alguna unidad a cargo del responsable. */
    public boolean esEmpleadoDelResponsable(int idPersona, int idResponsable);

    public PersonaDTO findById(int id);

    /** Alta o mod. Si el id es 0 crea, si no actualiza la persona existente. */
    public PersonaDTO insertOrUpdate(PersonaDTO dto);

    /** Baja : deshabilita la credencial de la persona. */
    public boolean remove(int id);

    /** Reactiva una persona dada de baja. */
    public boolean reactivar(int id);

    /** true si ya existe otra persona (distinta de idActual) con ese DNI. */
    public boolean existeDni(long dni, int idActual);

    /** true si ya existe otra persona (distinta de idActual) con ese email. */
    public boolean existeEmail(String email, int idActual);

    /** Persona a la que pertenece el usuario logueado (o null si el usuario no es una persona). */
    public PersonaDTO findByUsername(String username);

    /** Unidades de venta (y festivales) en las que trabaja la persona. */
    public List<AsignacionDTO> getAsignaciones(int idPersona);
}
