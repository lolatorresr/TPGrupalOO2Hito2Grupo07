package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.FestivalDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.IFestivalService;
import com.unla.TPGrupal002Hito2Grupo07.services.IReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/** Reportes del administrador: se elige un festival y se consulta el reporte sobre sus unidades. */
@Controller
@RequestMapping("/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    @Autowired
    @Qualifier("reporteService")
    private IReporteService reporteService;

    @Autowired
    @Qualifier("festivalService")
    private IFestivalService festivalService;

    @GetMapping({"", "/"})
    public ModelAndView index() {
        return new ModelAndView(ViewRouteHelper.REPORTE_INDEX);
    }

    /** Ranking de unidades por recaudación. Sin festival elegido muestra solo el selector. */
    @GetMapping("/ranking")
    public ModelAndView ranking(@RequestParam(required = false, defaultValue = "0") int festivalId) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.REPORTE_RANKING);
        mAV.addObject("festivales", festivalService.getAll());
        mAV.addObject("festivalId", festivalId);
        FestivalDTO festival = festivalService.findById(festivalId);
        if (festival != null) {
            mAV.addObject("festival", festival);
            mAV.addObject("ranking", reporteService.rankingPorRecaudacion(festivalId));
        }
        return mAV;
    }
}
