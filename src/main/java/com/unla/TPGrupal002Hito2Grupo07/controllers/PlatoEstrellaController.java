package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PlatoDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.UnidadDeVentaDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IPersonaService;
import com.unla.TPGrupal002Hito2Grupo07.services.IPlatoService;
import com.unla.TPGrupal002Hito2Grupo07.services.IUnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Plato estrella: el plato más pedido de una unidad en un festival (PlatoDTO).
 * El responsable solo puede consultar sus propias unidades; el administrador, todas.
 */
@Controller
@RequestMapping("/plato-estrella")
@PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE')") // Reporte del responsable
public class PlatoEstrellaController {

    @Autowired
    @Qualifier("platoService")
    private IPlatoService platoService;

    @Autowired
    private IFestivalRepository festivalRepository;

    @Autowired
    @Qualifier("unidadDeVentaService")
    private IUnidadDeVentaService unidadService;

    @Autowired
    @Qualifier("personaService")
    private IPersonaService personaService;

    @GetMapping("")
    public ModelAndView index(Authentication authentication) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PLATO_ESTRELLA_INDEX);
        mAV.addObject("festivales", festivalRepository.findAll());
        mAV.addObject("unidades", unidadesConsultables(authentication));
        return mAV;
    }

    @PostMapping("/buscar")
    public ModelAndView buscar(@RequestParam("idFestival") int idFestival,
                               @RequestParam("idUnidad") int idUnidad,
                               Authentication authentication) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PLATO_ESTRELLA_INDEX);
        List<UnidadDeVentaDTO> unidades = unidadesConsultables(authentication);
        mAV.addObject("festivales", festivalRepository.findAll());
        mAV.addObject("unidades", unidades);
        mAV.addObject("idFestivalSeleccionado", idFestival);
        mAV.addObject("idUnidadSeleccionada", idUnidad);

        // Un responsable solo consulta sus unidades (aunque mande otro id por la URL)
        if (unidades.stream().noneMatch(u -> u.getIdUnidadDeVenta() == idUnidad)) {
            mAV.addObject("error", "No puede consultar esa unidad de venta.");
            return mAV;
        }
        PlatoDTO platoEstrella = platoService.obtenerPlatoEstrella(idFestival, idUnidad);
        mAV.addObject("platoEstrella", platoEstrella);
        mAV.addObject("buscado", true);
        return mAV;
    }

    /** Unidades activas que puede consultar quien está logueado: todas para el administrador, las propias para el responsable. */
    private List<UnidadDeVentaDTO> unidadesConsultables(Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<UnidadDeVentaDTO> activas = unidadService.getAllActivas();
        if (esAdmin) {
            return activas;
        }
        PersonaDTO persona = personaService.findByUsername(authentication.getName());
        int idResponsable = persona != null ? persona.getIdPersona() : -1;
        return activas.stream().filter(u -> u.getResponsableId() == idResponsable).toList();
    }
}
