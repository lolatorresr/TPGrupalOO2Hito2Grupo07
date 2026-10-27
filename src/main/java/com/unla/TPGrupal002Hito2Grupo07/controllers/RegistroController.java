package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.RegistroResponsableDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.TipoUnidad;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.dtos.FestivalDTO;
import com.unla.TPGrupal002Hito2Grupo07.services.IFestivalService;
import com.unla.TPGrupal002Hito2Grupo07.services.IPersonaService;
import com.unla.TPGrupal002Hito2Grupo07.services.IRegistroService;
import com.unla.TPGrupal002Hito2Grupo07.services.IUnidadDeVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.time.Period;

/**
 * Registro público (sin iniciar sesión) de una unidad de venta junto con su responsable.
 * Al terminar, el responsable puede iniciar sesión con el usuario y contraseña que eligió.
 */
@Controller
@RequestMapping("/registro")
public class RegistroController {

    private static final String REGISTRO_ATTR = "registro";
    private static final int EDAD_MINIMA = 18;

    @Autowired
    @Qualifier("registroService")
    private IRegistroService registroService;

    @Autowired
    @Qualifier("unidadDeVentaService")
    private IUnidadDeVentaService unidadService;

    @Autowired
    @Qualifier("personaService")
    private IPersonaService personaService;

    @Autowired
    @Qualifier("festivalService")
    private IFestivalService festivalService;

    /** Se puede llegar desde un festival del inicio: viene preseleccionado en el formulario. */
    @GetMapping("")
    public ModelAndView form(@RequestParam(required = false, defaultValue = "0") int festivalId) {
        RegistroResponsableDTO dto = new RegistroResponsableDTO();
        dto.setFestivalId(festivalId);
        return formView(dto);
    }

    // Si alguien entra por URL a /registro/save (GET), lo mandamos al formulario en vez de dar error.
    @GetMapping("/save")
    public RedirectView saveGet() {
        return new RedirectView(ViewRouteHelper.REGISTRO_ROOT, true);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid @ModelAttribute(REGISTRO_ATTR) RegistroResponsableDTO dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        // --- Unidad: mismas validaciones de carga que el ABM ---
        if (dto.getCodigo() != null && unidadService.existeCodigo(dto.getCodigo(), 0)) {
            bindingResult.rejectValue("codigo", "error.registro", "Ya existe una unidad de venta con ese código.");
        }
        if (dto.getTipo() != null && dto.getCodigo() != null
                && !dto.getCodigo().startsWith(dto.getTipo().getPrefijoCodigo())) {
            bindingResult.rejectValue("codigo", "error.registro",
                    "El código de un " + dto.getTipo().getDescripcion() + " debe empezar con " + dto.getTipo().getPrefijoCodigo());
        }
        if (dto.getTipo() == TipoUnidad.FOOD_TRUCK && (dto.getPatente() == null || dto.getPatente().isBlank())) {
            bindingResult.rejectValue("patente", "error.registro", "La patente es obligatoria para un Food Truck.");
        }
        if (dto.getTipo() == TipoUnidad.PUESTO_DESARMABLE && dto.getCantidadCarpas() < 1) {
            bindingResult.rejectValue("cantidadCarpas", "error.registro", "Un Puesto Desarmable debe tener al menos una carpa.");
        }
        // Solo se puede registrar una unidad en un festival que no haya terminado
        FestivalDTO festival = festivalService.findById(dto.getFestivalId());
        if (festival == null) {
            bindingResult.rejectValue("festivalId", "error.registro", "Debe elegir un festival.");
        } else if (festival.isFinalizado()) {
            bindingResult.rejectValue("festivalId", "error.registro", "El festival " + festival.getNombre() + " ya finalizó.");
        }

        // --- Responsable: mayor de edad, DNI y email no repetidos ---
        if (dto.getFechaNacimiento() != null
                && Period.between(dto.getFechaNacimiento(), LocalDate.now()).getYears() < EDAD_MINIMA) {
            bindingResult.rejectValue("fechaNacimiento", "error.registro", "El responsable debe ser mayor de 18 años.");
        }
        if (dto.getDni() > 0 && personaService.existeDni(dto.getDni(), 0)) {
            bindingResult.rejectValue("dni", "error.registro", "El DNI ingresado ya pertenece a otra persona.");
        }
        if (dto.getEmail() != null && personaService.existeEmail(dto.getEmail(), 0)) {
            bindingResult.rejectValue("email", "error.registro", "El correo electrónico ya está registrado.");
        }

        // --- Credenciales: usuario único y contraseñas coincidentes ---
        if (dto.getUsername() != null && registroService.existeUsername(dto.getUsername())) {
            bindingResult.rejectValue("username", "error.registro", "Ese nombre de usuario ya está en uso.");
        }
        if (dto.getPassword() != null && dto.getConfirmarPassword() != null
                && !dto.getPassword().equals(dto.getConfirmarPassword())) {
            bindingResult.rejectValue("confirmarPassword", "error.registro", "Las contraseñas no coinciden.");
        }

        if (bindingResult.hasErrors()) {
            return formView(dto);
        }
        registroService.registrar(dto);
        // El aviso viaja como atributo flash: sobrevive al redirect y no ensucia la URL del login
        redirectAttributes.addFlashAttribute("registrado", true);
        return new ModelAndView(new RedirectView("/login", true));
    }

    private ModelAndView formView(RegistroResponsableDTO dto) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.REGISTRO_FORM);
        mAV.addObject(REGISTRO_ATTR, dto);
        mAV.addObject("tipos", TipoUnidad.values());
        mAV.addObject("festivales", festivalService.getNoFinalizados());
        mAV.addObject("festivalElegido", festivalService.findById(dto.getFestivalId()));
        return mAV;
    }
}
