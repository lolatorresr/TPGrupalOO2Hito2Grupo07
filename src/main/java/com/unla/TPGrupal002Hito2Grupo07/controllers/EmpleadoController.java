package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.AsignacionDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.ILiquidacionService;
import com.unla.TPGrupal002Hito2Grupo07.services.IPersonaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(ViewRouteHelper.EMPLEADO_ROOT)
@PreAuthorize("hasRole('EMPLEADO')") // Solo los empleados ven su página
public class EmpleadoController {

    private final IPersonaService personaService;
    private final ILiquidacionService liquidacionService;

    public EmpleadoController(IPersonaService personaService, ILiquidacionService liquidacionService) {
        this.personaService = personaService;
        this.liquidacionService = liquidacionService;
    }

    @GetMapping("")
    public String inicio(Authentication authentication, Model model) {
        // El usuario logueado es el email del empleado
        PersonaDTO empleado = personaService.findByUsername(authentication.getName());
        if (empleado == null) {
            // Usuario con rol empleado pero sin persona asociada
            return "redirect:/logout";
        }
        List<AsignacionDTO> asignaciones = personaService.getAsignaciones(empleado.getIdPersona());

        model.addAttribute("empleado", empleado);
        model.addAttribute("asignaciones", asignaciones);
        // Las asignaciones de festivales en curso hoy (para "Hoy trabajás en...")
        model.addAttribute("actuales", asignaciones.stream().filter(AsignacionDTO::isEnCurso).toList());
        // Sueldo del empleado en cada festival en el que trabajó (un festival puede tener varias unidades)
        Map<Integer, Double> sueldos = new LinkedHashMap<>();
        for (AsignacionDTO a : asignaciones) {
            if (a.getIdFestival() != 0 && !sueldos.containsKey(a.getIdFestival())) {
                sueldos.put(a.getIdFestival(), liquidacionService.calcularSueldoTotal(empleado.getIdPersona(), a.getIdFestival()));
            }
        }
        model.addAttribute("sueldos", sueldos);
        return ViewRouteHelper.EMPLEADO_INICIO;
    }
}