package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.FestivalDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.ItemPedidoDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PedidoDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.UnidadDeVentaDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.IFestivalService;
import com.unla.TPGrupal002Hito2Grupo07.services.IPedidoService;
import com.unla.TPGrupal002Hito2Grupo07.services.IPersonaService;
import com.unla.TPGrupal002Hito2Grupo07.services.IUnidadDeVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;

/**
 * Alta y listado de pedidos. Un pedido registra la fecha, el festival, la unidad
 * de venta y los platos con su cantidad. Según el enunciado, solo puede cargarse
 * con la fecha del día.
 * El administrador ve y carga pedidos de cualquier unidad; los empleados y
 * responsables solo de las unidades en cuyo staff figuran, dentro de un festival
 * en curso.
 */
@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private static final String PEDIDO_ATTR = "pedido";

    @Autowired
    @Qualifier("pedidoService")
    private IPedidoService pedidoService;

    @Autowired
    @Qualifier("unidadDeVentaService")
    private IUnidadDeVentaService unidadService;

    @Autowired
    @Qualifier("personaService")
    private IPersonaService personaService;

    @Autowired
    @Qualifier("festivalService")
    private IFestivalService festivalService;

    @GetMapping("/")
    public ModelAndView index(Authentication auth) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PEDIDO_INDEX);
        if (esAdmin(auth)) {
            mAV.addObject("pedidos", pedidoService.getAll());
        } else {
            // Empleado o responsable: solo los pedidos de las unidades donde trabaja
            List<Integer> misUnidades = unidadesDeStaff(auth).stream().map(UnidadDeVentaDTO::getIdUnidadDeVenta).toList();
            mAV.addObject("pedidos", pedidoService.getDeUnidades(misUnidades));
            mAV.addObject("soloMisUnidades", true);
        }
        return mAV;
    }

    /** Paso 1: elegir la unidad. Paso 2 (con unidadId): aparecen los platos que ofrece esa unidad. */
    @GetMapping("/form")
    public ModelAndView form(@RequestParam(required = false, defaultValue = "0") int unidadId, Authentication auth) {
        List<UnidadDeVentaDTO> unidades = unidadesParaPedir(auth);
        // Si el usuario trabaja en una sola unidad, queda elegida directamente
        if (unidadId == 0 && unidades.size() == 1) {
            unidadId = unidades.get(0).getIdUnidadDeVenta();
        }
        PedidoDTO dto = unidadId > 0 ? pedidoService.nuevoParaUnidad(unidadId) : new PedidoDTO();
        if (dto.getFechaTransaccion() == null) {
            dto.setFechaTransaccion(LocalDate.now());
        }
        return formView(dto, unidades);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid @ModelAttribute(PEDIDO_ATTR) PedidoDTO dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Authentication auth) {
        List<UnidadDeVentaDTO> unidades = unidadesParaPedir(auth);
        // Validación de carga 0: la unidad tiene que ser una en la que el usuario puede cargar pedidos
        // (donde trabaja, si es empleado o responsable) y su festival tiene que estar en curso
        UnidadDeVentaDTO unidad = unidades.stream()
                .filter(u -> u.getIdUnidadDeVenta() == dto.getUnidadId()).findFirst().orElse(null);
        if (unidad == null) {
            bindingResult.reject("error.pedido", "No puede cargar pedidos para esa unidad de venta.");
        } else {
            FestivalDTO festival = festivalService.findById(unidad.getFestivalId());
            if (festival == null || !festival.isEnCurso()) {
                bindingResult.reject("error.pedido", "Solo se pueden cargar pedidos en un festival en curso.");
            }
        }
        // Validación de carga 1: un pedido solo puede tener la fecha del día en que se carga
        if (dto.getFechaTransaccion() != null && !dto.getFechaTransaccion().equals(LocalDate.now())) {
            bindingResult.rejectValue("fechaTransaccion", "error.pedido",
                    "El pedido solo puede cargarse con la fecha del día (" + LocalDate.now() + ").");
        }
        // Validación de carga 2: tiene que pedirse al menos un plato
        if (!dto.tienePlatos()) {
            bindingResult.reject("error.pedido", "Debe indicar la cantidad de al menos un plato.");
        }
        if (bindingResult.hasErrors()) {
            // Se recargan nombre y precio de los platos, que no viajan en el POST
            completarDatosDePlatos(dto);
            return formView(dto, unidades);
        }
        PedidoDTO guardado = pedidoService.insert(dto);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Pedido #" + guardado.getIdPedido() + " registrado correctamente (" + guardado.getCantidadPlatos() + " platos).");
        return new ModelAndView(new RedirectView(ViewRouteHelper.PEDIDO_ROOT, true));
    }

    private void completarDatosDePlatos(PedidoDTO dto) {
        if (dto.getUnidadId() <= 0) {
            return;
        }
        PedidoDTO base = pedidoService.nuevoParaUnidad(dto.getUnidadId());
        dto.setUnidadNombre(base.getUnidadNombre());
        dto.setUnidadCodigo(base.getUnidadCodigo());
        dto.setFestivalNombre(base.getFestivalNombre());
        for (ItemPedidoDTO item : dto.getItems()) {
            base.getItems().stream()
                    .filter(b -> b.getPlatoId() == item.getPlatoId())
                    .findFirst()
                    .ifPresent(b -> {
                        item.setPlatoNombre(b.getPlatoNombre());
                        item.setPrecio(b.getPrecio());
                        item.setSubTotal(b.getPrecio() * item.getCantidad());
                    });
        }
    }

    private ModelAndView formView(PedidoDTO dto, List<UnidadDeVentaDTO> unidades) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PEDIDO_FORM);
        mAV.addObject(PEDIDO_ATTR, dto);
        mAV.addObject("unidades", unidades);
        mAV.addObject("hoy", LocalDate.now());
        return mAV;
    }

    private boolean esAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /** Unidades activas en cuyo staff figura la persona logueada (vacío si la credencial no tiene persona). */
    private List<UnidadDeVentaDTO> unidadesDeStaff(Authentication auth) {
        PersonaDTO persona = auth != null ? personaService.findByUsername(auth.getName()) : null;
        return persona != null ? unidadService.getActivasDeStaff(persona.getIdPersona()) : List.of();
    }

    /** Unidades en las que el usuario puede cargar un pedido: todas si es admin, si no las suyas en festivales en curso. */
    private List<UnidadDeVentaDTO> unidadesParaPedir(Authentication auth) {
        if (esAdmin(auth)) {
            return unidadService.getAllActivas();
        }
        return unidadesDeStaff(auth).stream().filter(u -> {
            FestivalDTO festival = festivalService.findById(u.getFestivalId());
            return festival != null && festival.isEnCurso();
        }).toList();
    }
}
