package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.TipoPersona;
import com.unla.TPGrupal002Hito2Grupo07.entities.CategoriaCocina;
import com.unla.TPGrupal002Hito2Grupo07.services.IPersonaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/personas")
@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE', 'RESPONSABLE')") // Restringe el acceso solo al rol Responsable[cite: 14]
public class PersonaController {

    @Autowired
    private IPersonaService personaService;

    // Opciones del select de categoría; disponibles en todas las vistas de este controller
    @ModelAttribute("categoriasCocina")
    public CategoriaCocina[] categoriasCocina() {
        return CategoriaCocina.values();
    }

    // 1. LISTAR PERSONAS ACTIVAS
    @GetMapping("")
    public String index(Model model) {
        // Trae entidades que no estén eliminadas lógicamente[cite: 14]
        model.addAttribute("personas", personaService.getAllActivas());
        return "persona/index";
    }

    // 2. FORMULARIO DE ALTA
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("personaDTO", new PersonaDTO());
        model.addAttribute("tiposPersona", TipoPersona.values());
        return "persona/form";
    }

    // 3. FORMULARIO DE EDICIÓN
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") int id, Model model) {
        PersonaDTO dto = personaService.findById(id);
        if (dto == null) {
            return "redirect:/personas";
        }
        model.addAttribute("personaDTO", dto);
        model.addAttribute("tiposPersona", TipoPersona.values());
        return "persona/form";
    }

    // 4. GUARDAR (ALTA Y MODIFICACIÓN) CON VALIDACIONES DE CARGA
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("personaDTO") PersonaDTO dto,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        // Atrapa los errores de formato del DTO
        if (bindingResult.hasErrors()) {
            model.addAttribute("tiposPersona", TipoPersona.values());
            return "persona/form";
        }

        try {
            // Intenta guardar aplicando las validaciones de carga exigidas[cite: 14]
            PersonaDTO guardada = personaService.insertOrUpdate(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Persona guardada correctamente.");
            if (!guardada.isMailEnviado()) {
                redirectAttributes.addFlashAttribute("mensajeAviso",
                        "El empleado se dio de alta, pero no se pudo enviar el mail con sus credenciales.");
            }
        } catch (IllegalArgumentException e) {
            // Atrapa la excepción si falla la validación de DNI o Edad y recarga la vista
            model.addAttribute("errorNegocio", e.getMessage());
            model.addAttribute("tiposPersona", TipoPersona.values());
            return "persona/form";
        }

        return "redirect:/personas";
    }

    // 5. BAJA LÓGICA
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        // La eliminación de los empleados será de tipo lógica[cite: 14]
        if (personaService.remove(id)) {
            redirectAttributes.addFlashAttribute("mensajeExito", "Persona dada de baja lógicamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo dar de baja: la persona no existe o no tiene credencial.");
        }
        return "redirect:/personas";
    }
}