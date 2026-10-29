package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.CierreCajaDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.ICierreCajaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/** Cierre de caja: el cajero cierra la caja de su unidad al finalizar el día. */
@Controller
@RequestMapping(ViewRouteHelper.CAJA_ROOT)
@PreAuthorize("hasRole('EMPLEADO')")
public class CajaController {

    private final ICierreCajaService cierreCajaService;

    public CajaController(ICierreCajaService cierreCajaService) {
        this.cierreCajaService = cierreCajaService;
    }

    /** Caja de hoy de cada unidad donde trabaja + historial de cierres. */
    @GetMapping("")
    public ModelAndView index(Authentication auth, RedirectAttributes redirectAttributes) {
        if (!cierreCajaService.esCajero(auth.getName())) {
            redirectAttributes.addFlashAttribute("mensajeError", "Solo los cajeros pueden cerrar la caja.");
            return new ModelAndView(new RedirectView(ViewRouteHelper.EMPLEADO_ROOT, true));
        }
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMPLEADO_CAJA);
        mAV.addObject("cajasDeHoy", cierreCajaService.resumenDeHoy(auth.getName()));
        mAV.addObject("historial", cierreCajaService.historial(auth.getName()));
        return mAV;
    }

    /** Cierra la caja de hoy de la unidad. */
    @PostMapping("/cerrar/{idUnidad}")
    public RedirectView cerrar(@PathVariable("idUnidad") int idUnidad, Authentication auth,
                               RedirectAttributes redirectAttributes) {
        try {
            CierreCajaDTO cierre = cierreCajaService.cerrar(auth.getName(), idUnidad);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Caja de " + cierre.getUnidadNombre() + " cerrada: " + cierre.getCantidadPedidos()
                            + " pedidos, $" + String.format("%,.2f", cierre.getMontoTotal()) + ".");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return new RedirectView(ViewRouteHelper.CAJA_ROOT, true);
    }
}