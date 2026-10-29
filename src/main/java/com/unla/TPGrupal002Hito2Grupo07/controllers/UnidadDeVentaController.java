package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.TipoUnidad;
import com.unla.TPGrupal002Hito2Grupo07.dtos.UnidadDeVentaDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IUnidadDeVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;

@Controller
@RequestMapping("/unidades")
@PreAuthorize("hasRole('ADMIN')") // Gestión de unidades: solo el administrador
public class UnidadDeVentaController {

    private static final String UNIDAD_ATTR = "unidad";

    @Autowired
    @Qualifier("unidadDeVentaService")
    private IUnidadDeVentaService unidadService;

    @Autowired
    @Qualifier("festivalRepository")
    private IFestivalRepository festivalRepository;

    @Autowired
    @Qualifier("personaRepository")
    private IPersonaRepository personaRepository;

    /** Listado de unidades activas, opcionalmente de un solo festival (?festivalId=). */
    @GetMapping("/")
    public ModelAndView index(@RequestParam(required = false, defaultValue = "0") int festivalId) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UNIDAD_INDEX);
        mAV.addObject("unidades", festivalId > 0
                ? unidadService.getActivasPorFestival(festivalId)
                : unidadService.getAllActivas());
        mAV.addObject("festivales", festivalRepository.findAll());
        mAV.addObject("festivalId", festivalId);
        return mAV;
    }

    @GetMapping("/form")
    public ModelAndView form() {
        return formView(new UnidadDeVentaDTO());
    }

    @GetMapping("/form/{id}")
    public ModelAndView form(@PathVariable("id") int id) {
        UnidadDeVentaDTO dto = unidadService.findById(id);
        if (dto == null) {
            return new ModelAndView(new RedirectView(ViewRouteHelper.UNIDAD_ROOT, true));
        }
        return formView(dto);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid @ModelAttribute(UNIDAD_ATTR) UnidadDeVentaDTO dto,
                             BindingResult bindingResult) {

        // Validación de carga 1: el código debe ser único (sin contar la propia unidad al modificar)
        if (dto.getCodigo() != null && unidadService.existeCodigo(dto.getCodigo(), dto.getIdUnidadDeVenta())) {
            bindingResult.rejectValue("codigo", "error.unidad", "Ya existe una unidad de venta con ese código.");
        }

        // Validación de carga 2: el prefijo del código debe coincidir con el tipo de unidad elegido
        if (dto.getTipo() != null && dto.getCodigo() != null
                && !dto.getCodigo().startsWith(dto.getTipo().getPrefijoCodigo())) {
            bindingResult.rejectValue("codigo", "error.unidad",
                    "El código de un " + dto.getTipo().getDescripcion() + " debe empezar con " + dto.getTipo().getPrefijoCodigo());
        }

        // Campos obligatorios según el tipo
        if (dto.getTipo() == TipoUnidad.FOOD_TRUCK && (dto.getPatente() == null || dto.getPatente().isBlank())) {
            bindingResult.rejectValue("patente", "error.unidad", "La patente es obligatoria para un Food Truck.");
        }
        if (dto.getTipo() == TipoUnidad.PUESTO_DESARMABLE && dto.getCantidadCarpas() < 1) {
            bindingResult.rejectValue("cantidadCarpas", "error.unidad", "Un Puesto Desarmable debe tener al menos una carpa.");
        }

        // Validación de carga 3: no se dan de alta unidades en un festival que ya finalizó (igual que en el registro)
        Festival festival = festivalRepository.findById(dto.getFestivalId()).orElse(null);
        if (dto.getIdUnidadDeVenta() == 0 && festival != null && festival.getFechaFin().isBefore(LocalDate.now())) {
            bindingResult.rejectValue("festivalId", "error.unidad", "El festival " + festival.getNombre() + " ya finalizó.");
        }

        if (bindingResult.hasErrors()) {
            return formView(dto);
        }
        unidadService.insertOrUpdate(dto);
        return new ModelAndView(new RedirectView(ViewRouteHelper.UNIDAD_ROOT, true));
    }

    @PostMapping("/delete/{id}")
    public RedirectView delete(@PathVariable("id") int id) {
        unidadService.remove(id);
        return new RedirectView(ViewRouteHelper.UNIDAD_ROOT);
    }

    private ModelAndView formView(UnidadDeVentaDTO dto) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UNIDAD_FORM);
        mAV.addObject(UNIDAD_ATTR, dto);
        mAV.addObject("tipos", TipoUnidad.values());
        mAV.addObject("festivales", festivalRepository.findAll());
        mAV.addObject("responsables", personaRepository.findByCredencialEnabledTrue());
        return mAV;
    }
}
