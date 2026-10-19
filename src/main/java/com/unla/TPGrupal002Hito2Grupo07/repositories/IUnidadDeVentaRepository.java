package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.UnidadDeVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("unidadDeVentaRepository")
public interface IUnidadDeVentaRepository extends JpaRepository<UnidadDeVenta, Serializable> {

    public abstract Optional<UnidadDeVenta> findByCodigo(String codigo);

    public abstract boolean existsByCodigo(String codigo);

    /** Unidades no eliminadas lógicamente. */
    public abstract List<UnidadDeVenta> findByActivoTrue();

    public abstract List<UnidadDeVenta> findByFestival_IdFestivalAndActivoTrue(int idFestival);
}
