package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PlatoDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.IPlatoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/platos")
public class PlatoController {

    @Autowired
    @Qualifier("platoService")
    private IPlatoService platoService;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PLATO_INDEX);
        mAV.addObject("platos", platoService.getAll());
        mAV.addObject("plato", new PlatoDTO());
        return mAV;
    }

    @PostMapping("/create")
    public ModelAndView create(@Valid @ModelAttribute("plato") PlatoDTO platoDTO,
                               BindingResult bindingResult){

        //validación precio> costo
        if(platoDTO.getPrecio() <= platoDTO.getCosto()){
            bindingResult.rejectValue("precio", "error.plato", "El precio de venta debe ser mayor al costo de producción.");
        }

        //errores de formato
        if(bindingResult.hasErrors()){
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.PLATO_INDEX);
            mAV.addObject("platos", platoService.getAll());
            mAV.addObject("plato", platoDTO);
            return mAV;
        }
        platoService.insertOrUpdate((platoDTO));
        return new ModelAndView(new RedirectView(ViewRouteHelper.PLATO_ROOT, true));
    }

    @PostMapping("/delete/{id}")
    public RedirectView delete(@PathVariable("id") int id){
        platoService.remove(id);
        return new RedirectView(ViewRouteHelper.PLATO_ROOT);
    }
}
