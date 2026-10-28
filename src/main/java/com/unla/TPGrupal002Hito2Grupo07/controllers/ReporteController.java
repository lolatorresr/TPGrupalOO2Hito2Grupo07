package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.FestivalDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteVentaDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.IFestivalService;
import com.unla.TPGrupal002Hito2Grupo07.services.IPersonaService;
import com.unla.TPGrupal002Hito2Grupo07.services.IReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Reportes sobre las unidades de venta de un festival. El administrador consulta sobre todas las
 * unidades; el responsable, sobre las que tiene a cargo.
 */
@Controller
@RequestMapping("/reportes")
@PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE')")
public class ReporteController {

    @Autowired
    @Qualifier("reporteService")
    private IReporteService reporteService;

    @Autowired
    @Qualifier("festivalService")
    private IFestivalService festivalService;

    @Autowired
    @Qualifier("personaService")
    private IPersonaService personaService;

    @GetMapping({"", "/"})
    public ModelAndView index() {
        return new ModelAndView(ViewRouteHelper.REPORTE_INDEX);
    }

    /** Ranking de unidades por recaudación. Sin festival elegido muestra solo el selector. */
    @GetMapping("/ranking")
    @PreAuthorize("hasRole('ADMIN')")
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

    /** Las "cantidad" unidades que más gastaron en canon en el festival (parámetro x del enunciado, por defecto 3). */
    @GetMapping("/canon")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView mayoresCanon(@RequestParam(required = false, defaultValue = "0") int festivalId,
                                     @RequestParam(required = false, defaultValue = "3") int cantidad) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.REPORTE_CANON);
        mAV.addObject("festivales", festivalService.getAll());
        mAV.addObject("festivalId", festivalId);
        // Validación de carga: la cantidad de unidades a mostrar tiene que ser al menos 1
        if (cantidad < 1) {
            mAV.addObject("errorCantidad", "La cantidad de unidades debe ser al menos 1.");
            cantidad = 1;
        }
        mAV.addObject("cantidad", cantidad);
        FestivalDTO festival = festivalService.findById(festivalId);
        if (festival != null) {
            mAV.addObject("festival", festival);
            mAV.addObject("unidades", reporteService.mayoresCanon(festivalId, cantidad));
        }
        return mAV;
    }

    /** Recaudación de un festival (ReporteVentaDTO): el responsable ve sus unidades, el administrador todas. */
    @GetMapping("/recaudacion")
    public ModelAndView recaudacion(@RequestParam(required = false, defaultValue = "0") int festivalId,
                                    Authentication authentication) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.REPORTE_RECAUDACION);
        mAV.addObject("festivales", festivalService.getAll());
        mAV.addObject("festivalId", festivalId);
        if (festivalService.findById(festivalId) != null) {
            ReporteVentaDTO reporte = reporteService.recaudacionFestival(festivalId, idResponsable(authentication));
            mAV.addObject("reporte", reporte);
        }
        return mAV;
    }

    /** Id de la persona logueada si es responsable; null para el administrador (ve todo). */
    private Integer idResponsable(Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (esAdmin) {
            return null;
        }
        PersonaDTO persona = personaService.findByUsername(authentication.getName());
        // Responsable sin persona asociada: no tiene unidades, el reporte sale vacío
        return persona != null ? persona.getIdPersona() : -1;
    }
}
