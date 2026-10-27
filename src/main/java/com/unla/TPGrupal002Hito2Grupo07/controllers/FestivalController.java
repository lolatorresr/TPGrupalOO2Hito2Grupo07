package com.unla.TPGrupal002Hito2Grupo07.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.unla.TPGrupal002Hito2Grupo07.dtos.FestivalDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.IFestivalService;
import com.unla.TPGrupal002Hito2Grupo07.services.IUnidadDeVentaService;

/**
 * Página de un festival: sus datos y las unidades de venta que participan.
 * Es pública: desde acá se entra a registrar una unidad para ese festival.
 */
@Controller
@RequestMapping("/festivales")
public class FestivalController {

    @Autowired
    @Qualifier("festivalService")
    private IFestivalService festivalService;

    @Autowired
    @Qualifier("unidadDeVentaService")
    private IUnidadDeVentaService unidadService;

    @GetMapping({"", "/"})
    public RedirectView index() {
        // Los festivales se listan en la página de inicio
        return new RedirectView(ViewRouteHelper.ROUTE, true);
    }

    @GetMapping("/{id}")
    public ModelAndView detalle(@PathVariable("id") int id) {
        FestivalDTO festival = festivalService.findById(id);
        if (festival == null) {
            return new ModelAndView(new RedirectView(ViewRouteHelper.ROUTE, true));
        }
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.FESTIVAL_DETALLE);
        mAV.addObject("festival", festival);
        mAV.addObject("unidades", unidadService.getActivasPorFestival(id));
        return mAV;
    }
}
