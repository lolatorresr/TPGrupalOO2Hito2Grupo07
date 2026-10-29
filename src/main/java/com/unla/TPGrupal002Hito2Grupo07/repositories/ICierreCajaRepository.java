package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.CierreCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository("cierreCajaRepository")
public interface ICierreCajaRepository extends JpaRepository<CierreCaja, Integer> {

    /** Cierre de una unidad en una fecha (como mucho uno, por la restricción única). */
    public abstract Optional<CierreCaja> findByUnidadVenta_IdUnidadDeVentaAndFecha(int idUnidad, LocalDate fecha);

    /** true si la caja de la unidad ya se cerró ese día. */
    public abstract boolean existsByUnidadVenta_IdUnidadDeVentaAndFecha(int idUnidad, LocalDate fecha);

    /** Historial de cierres de las unidades indicadas, del más reciente al más antiguo. */
    public abstract List<CierreCaja> findByUnidadVenta_IdUnidadDeVentaInOrderByFechaDesc(List<Integer> idsUnidades);
}