package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.TipoPersona;
import com.unla.TPGrupal002Hito2Grupo07.dtos.UnidadDeVentaDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.CategoriaCocina;
import com.unla.TPGrupal002Hito2Grupo07.services.IPersonaService;
import com.unla.TPGrupal002Hito2Grupo07.services.IUnidadDeVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * ABM de empleados del responsable. Cada responsable gestiona solo a los empleados que trabajan
 * en sus unidades de venta; el alta suma al empleado al staff de una de sus unidades vigentes.
 */
@Controller
@RequestMapping("/personas")
@PreAuthorize("hasRole('RESPONSABLE')") // Restringe el acceso solo al rol Responsable
public class PersonaController {

    // Tipos de empleado que puede dar de alta un responsable (no puede crear otros responsables)
    private static final TipoPersona[] TIPOS_EMPLEADO = {TipoPersona.CAJERO, TipoPersona.COCINERO};

    private static final String NO_ES_SU_EMPLEADO = "Ese empleado no trabaja en ninguna de tus unidades de venta.";

    @Autowired
    private IPersonaService personaService;

    @Autowired
    @Qualifier("unidadDeVentaService")
    private IUnidadDeVentaService unidadService;

    // Opciones del select de categoría; disponibles en todas las vistas de este controller
    @ModelAttribute("categoriasCocina")
    public CategoriaCocina[] categoriasCocina() {
        return CategoriaCocina.values();
    }

    // 1. LISTAR LOS EMPLEADOS ACTIVOS DEL RESPONSABLE, CON FILTROS POR UNO O VARIOS ATRIBUTOS
    @GetMapping("")
    public String index(@RequestParam(required = false) String texto,
                        @RequestParam(required = false) String dni,
                        @RequestParam(required = false) TipoPersona tipo,
                        @RequestParam(required = false) CategoriaCocina categoria,
                        @RequestParam(required = false) String turno,
                        Authentication authentication,
                        Model model) {
        int idResponsable = idResponsable(authentication);
        // Solo empleados de sus unidades y no eliminados lógicamente; los filtros vacíos no se aplican
        model.addAttribute("personas",
                personaService.filtrarActivasDelResponsable(idResponsable, texto, dni, tipo, categoria, turno));
        model.addAttribute("inactivas", personaService.getInactivasDelResponsable(idResponsable));

        // Para que el formulario conserve lo que eligió el usuario
        model.addAttribute("tiposPersona", TIPOS_EMPLEADO);
        model.addAttribute("fTexto", texto);
        model.addAttribute("fDni", dni);
        model.addAttribute("fTipo", tipo);
        model.addAttribute("fCategoria", categoria);
        model.addAttribute("fTurno", turno);
        boolean hayFiltros = (texto != null && !texto.isBlank()) || (dni != null && !dni.isBlank())
                || tipo != null || categoria != null || (turno != null && !turno.isBlank());
        model.addAttribute("hayFiltros", hayFiltros);
        return "persona/index";
    }

    // 2. FORMULARIO DE ALTA
    @GetMapping("/nuevo")
    public String nuevo(Authentication authentication, Model model) {
        PersonaDTO dto = new PersonaDTO();
        List<UnidadDeVentaDTO> unidades = cargarFormulario(authentication, model);
        // Si el responsable tiene una sola unidad vigente, ya queda elegida
        if (unidades.size() == 1) {
            dto.setIdUnidad(unidades.get(0).getIdUnidadDeVenta());
        }
        model.addAttribute("personaDTO", dto);
        return "persona/form";
    }

    // 3. FORMULARIO DE EDICION (solo de sus empleados)
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") int id, Authentication authentication,
                         Model model, RedirectAttributes redirectAttributes) {
        PersonaDTO dto = personaService.findById(id);
        if (dto == null || !personaService.esEmpleadoDelResponsable(id, idResponsable(authentication))) {
            redirectAttributes.addFlashAttribute("mensajeError", NO_ES_SU_EMPLEADO);
            return "redirect:/personas";
        }
        model.addAttribute("personaDTO", dto);
        cargarFormulario(authentication, model);
        return "persona/form";
    }

    // 4. GUARDAR (ALTA Y MODIFICACION) CON VALIDACIONES DE CARGA
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("personaDTO") PersonaDTO dto,
                          BindingResult bindingResult,
                          Authentication authentication,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        int idResponsable = idResponsable(authentication);

        // Modificación: solo de un empleado suyo (evita editar a otro cambiando el id del formulario)
        if (dto.getIdPersona() > 0 && !personaService.esEmpleadoDelResponsable(dto.getIdPersona(), idResponsable)) {
            redirectAttributes.addFlashAttribute("mensajeError", NO_ES_SU_EMPLEADO);
            return "redirect:/personas";
        }

        // Atrapa los errores de formato del DTO
        List<UnidadDeVentaDTO> unidades = cargarFormulario(authentication, model);
        if (bindingResult.hasErrors()) {
            return "persona/form";
        }

        // Validación de carga: en el alta, la unidad tiene que ser una de sus unidades vigentes
        if (dto.getIdPersona() == 0 && unidades.stream().noneMatch(u -> u.getIdUnidadDeVenta() == dto.getIdUnidad())) {
            model.addAttribute("errorNegocio",
                    "Seleccioná una de tus unidades de venta de un festival vigente.");
            return "persona/form";
        }

        try {
            // Intenta guardar aplicando las validaciones de carga
            PersonaDTO guardada = personaService.insertOrUpdate(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Persona guardada correctamente.");
            if (!guardada.isMailEnviado()) {
                redirectAttributes.addFlashAttribute("mensajeAviso",
                        "El empleado se dio de alta, pero no se pudo enviar el mail con sus credenciales.");
            }
        } catch (IllegalArgumentException e) {
            // Atrapa la excepción si falla una validación de carga y recarga la vista
            model.addAttribute("errorNegocio", e.getMessage());
            return "persona/form";
        }

        return "redirect:/personas";
    }

    // 5. BAJA LOGICA (solo de sus empleados)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id, Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (!personaService.esEmpleadoDelResponsable(id, idResponsable(authentication))) {
            redirectAttributes.addFlashAttribute("mensajeError", NO_ES_SU_EMPLEADO);
        } else if (personaService.remove(id)) {
            // La eliminación de los empleados será de tipo lógica
            redirectAttributes.addFlashAttribute("mensajeExito", "Persona dada de baja lógicamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo dar de baja: la persona no existe o no tiene credencial.");
        }
        return "redirect:/personas";
    }

    // 6. REACTIVACION: método de actualización específico para un empleado dado de baja (solo de sus empleados)
    @PostMapping("/reactivar/{id}")
    public String reactivar(@PathVariable("id") int id, Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        if (!personaService.esEmpleadoDelResponsable(id, idResponsable(authentication))) {
            redirectAttributes.addFlashAttribute("mensajeError", NO_ES_SU_EMPLEADO);
        } else if (personaService.reactivar(id)) {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Empleado reactivado: ya puede volver a iniciar sesión con su usuario.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo reactivar: la persona no existe o no tiene credencial.");
        }
        return "redirect:/personas";
    }

    /** Id de la persona del responsable logueado (0 si el usuario no tiene persona asociada). */
    private int idResponsable(Authentication authentication) {
        PersonaDTO responsable = personaService.findByUsername(authentication.getName());
        return responsable != null ? responsable.getIdPersona() : 0;
    }

    /** Datos que necesita el formulario: tipos de empleado y unidades vigentes del responsable. */
    private List<UnidadDeVentaDTO> cargarFormulario(Authentication authentication, Model model) {
        List<UnidadDeVentaDTO> unidades = unidadService.getVigentesDelResponsable(idResponsable(authentication));
        model.addAttribute("tiposPersona", TIPOS_EMPLEADO);
        model.addAttribute("unidadesResponsable", unidades);
        return unidades;
    }
}