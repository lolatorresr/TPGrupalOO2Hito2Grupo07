package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteMayoresCanonDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteRankingUnidadDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteVentaDTO;

import java.util.List;

/** Reportes del administrador y del responsable sobre las unidades de venta de un festival. */
public interface IReporteService {

    /**
     * Ranking de las unidades activas de un festival, ordenado de mayor a menor
     * recaudación (suma de los pedidos del festival). Las unidades sin pedidos
     * aparecen al final con recaudación 0.
     */
    public List<ReporteRankingUnidadDTO> rankingPorRecaudacion(int idFestival);

    /**
     * Las "cantidad" unidades activas del festival que más gastaron en canon,
     * de mayor a menor, con nombre comercial, código, tipo y canon.
     */
    public List<ReporteMayoresCanonDTO> mayoresCanon(int idFestival, int cantidad);

    /**
     * Recaudación de un festival (ReporteVentaDTO): total de los pedidos y detalle por unidad.
     * Con idResponsable se limita a las unidades activas a cargo de ese responsable;
     * con null (administrador) abarca todas las unidades activas del festival.
     */
    public ReporteVentaDTO recaudacionFestival(int idFestival, Integer idResponsable);
}
