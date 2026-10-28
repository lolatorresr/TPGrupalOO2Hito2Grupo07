package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.UnidadDeVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("unidadDeVentaRepository")
public interface IUnidadDeVentaRepository extends JpaRepository<UnidadDeVenta, Serializable> {

    /** Unidades no eliminadas lógicamente. */
    public abstract List<UnidadDeVenta> findByActivoTrue();

    public abstract List<UnidadDeVenta> findByFestival_IdFestivalAndActivoTrue(int idFestival);

    /** Unidades activas de un festival a cargo de un responsable (reportes del responsable). */
    public abstract List<UnidadDeVenta> findByFestival_IdFestivalAndResponsable_IdPersonaAndActivoTrue(int idFestival, int idResponsable);

    /** Unidades activas en cuyo staff figura la persona, con su festival ya cargado. */
    @Query("SELECT DISTINCT u FROM UnidadDeVenta u JOIN u.staff s LEFT JOIN FETCH u.festival "
            + "WHERE s.idPersona = :idPersona AND u.activo = true")
    public abstract List<UnidadDeVenta> findActivasByStaff(@Param("idPersona") int idPersona);

    public abstract Optional<UnidadDeVenta> findByCodigo(String codigo);
}
