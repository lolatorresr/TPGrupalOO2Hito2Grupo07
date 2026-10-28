package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteMayoresCanonDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteRankingUnidadDTO;

import java.util.List;

/** Reportes del administrador sobre las unidades de venta de un festival. */
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
}
