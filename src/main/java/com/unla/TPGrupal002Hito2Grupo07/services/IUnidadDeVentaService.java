package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.dtos.UnidadDeVentaDTO;

import java.util.List;

public interface IUnidadDeVentaService {

    /** Unidades activas (no eliminadas lógicamente). */
    public List<UnidadDeVentaDTO> getAllActivas();

    /** Unidades activas de un festival (vista del festival y filtro del listado). */
    public List<UnidadDeVentaDTO> getActivasPorFestival(int idFestival);

    /** Unidades activas en cuyo staff figura la persona (donde trabaja un empleado o responsable). */
    public List<UnidadDeVentaDTO> getActivasDeStaff(int idPersona);

    /** Unidades activas del responsable en festivales que no terminaron: donde puede dar de alta empleados. */
    public List<UnidadDeVentaDTO> getVigentesDelResponsable(int idResponsable);

    public UnidadDeVentaDTO findById(int id);

    /** Alta o modificación. Si el id es 0 crea, si no actualiza la unidad existente. */
    public UnidadDeVentaDTO insertOrUpdate(UnidadDeVentaDTO dto);

    /** Baja lógica. */
    public boolean remove(int id);

    /** Reactiva una unidad dada de baja lógicamente. */
    public boolean reactivar(int id);

    /** true si ya existe otra unidad (distinta de idActual) con ese código. */
    public boolean existeCodigo(String codigo, int idActual);

    /** Suma una persona al staff de la unidad. true si ambos existen. */
    public boolean agregarAlStaff(int idUnidad, int idPersona);
}