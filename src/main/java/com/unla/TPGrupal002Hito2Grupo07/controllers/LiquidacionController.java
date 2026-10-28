package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.ILiquidacionService;
import com.unla.TPGrupal002Hito2Grupo07.services.implementation.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Liquidación de haberes: el empleado ve su sueldo en un festival y descarga el recibo en PDF.
 * Requiere sesión. El empleado solo puede ver su propia liquidación; el administrador, la de cualquiera.
 */
@Controller
@RequestMapping(ViewRouteHelper.LIQUIDACION_ROOT)
@PreAuthorize("isAuthenticated()")
public class LiquidacionController {

    @Autowired
    @Qualifier("liquidacionService")
    private ILiquidacionService liquidacionService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    @Qualifier("personaRepository")
    private IPersonaRepository personaRepository;

    @GetMapping("")
    public ModelAndView verLiquidacion(@RequestParam(name = "idPersona", required = false) Integer idPersona,
                                       @RequestParam(name = "idFestival") int idFestival,
                                       Authentication authentication) {
        Persona persona = personaPermitida(idPersona, authentication);
        Festival festival = liquidacionService.obtenerFestival(idFestival);
        if (persona == null) {
            return new ModelAndView("redirect:/index");
        }
        if (festival == null) {
            return new ModelAndView("redirect:" + ViewRouteHelper.EMPLEADO_ROOT);
        }

        ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMPLEADO_LIQUIDACION);
        mAV.addObject("persona", persona);
        mAV.addObject("festival", festival);
        mAV.addObject("sueldoTotal", liquidacionService.calcularSueldoTotal(persona.getIdPersona(), idFestival));
        return mAV;
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf(@RequestParam(name = "idPersona", required = false) Integer idPersona,
                                               @RequestParam(name = "idFestival") int idFestival,
                                               Authentication authentication) {
        Persona persona = personaPermitida(idPersona, authentication);
        Festival festival = liquidacionService.obtenerFestival(idFestival);
        if (persona == null || festival == null) {
            return ResponseEntity.notFound().build();
        }

        double sueldoTotal = liquidacionService.calcularSueldoTotal(persona.getIdPersona(), idFestival);
        byte[] pdfBytes = pdfGeneratorService.generarReciboSueldoPdf(persona, festival, sueldoTotal);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "Recibo_Sueldo_" + persona.getApellido() + "_" + festival.getIdFestival() + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Persona cuya liquidación se puede ver: la del usuario logueado, salvo que sea administrador
     * y pida otra por parámetro. Así un empleado no puede ver el sueldo de otro cambiando la URL.
     */
    private Persona personaPermitida(Integer idPersona, Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (esAdmin && idPersona != null) {
            return liquidacionService.obtenerPersona(idPersona);
        }
        return personaRepository.findByCredencial_Username(authentication.getName()).orElse(null);
    }
}
