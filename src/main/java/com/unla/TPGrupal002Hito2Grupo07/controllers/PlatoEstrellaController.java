package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PlatoDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUnidadDeVentaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IPlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/plato-estrella")
public class PlatoEstrellaController {

    @Autowired
    @Qualifier("platoService")
    private IPlatoService platoService;

    @Autowired
    private IFestivalRepository festivalRepository;

    @Autowired
    private IUnidadDeVentaRepository unidadDeVentaRepository;

    @GetMapping("")
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PLATO_ESTRELLA_INDEX);
        mAV.addObject("festivales", festivalRepository.findAll());
        mAV.addObject("unidades", unidadDeVentaRepository.findAll());
        return mAV;
    }

    @PostMapping("/buscar")
    public ModelAndView buscar(@RequestParam("idFestival") int idFestival,
                               @RequestParam("idUnidad") int idUnidad){
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PLATO_ESTRELLA_INDEX);
        mAV.addObject("festivales", festivalRepository.findAll());
        mAV.addObject("unidades", unidadDeVentaRepository.findAll());

        PlatoDTO platoEstrella = platoService.obtenerPlatoEstrella(idFestival, idUnidad);
        mAV.addObject("platoEstrella", platoEstrella);
        mAV.addObject("idFestivalSeleccionado", idFestival);
        mAV.addObject("idUnidadSeleccionada", idUnidad);
        mAV.addObject("buscado", true);

        return mAV;
    }

}
