package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.*;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.*;
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

    @Autowired
    @Qualifier("cierreCajaService")
    private ICierreCajaService cierreCajaService;

    @GetMapping("/")
    public ModelAndView index(@RequestParam(required = false) Integer festivalId, Authentication auth) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PEDIDO_INDEX);
        List<PedidoDTO> pedidos;
        List<FestivalDTO> festivales;
        if (esAdmin(auth)) {
            pedidos = pedidoService.getAll();
            festivales = festivalService.getAll();
        } else {
            // Empleado o responsable: solo los pedidos de las unidades donde trabaja
            List<UnidadDeVentaDTO> misUnidades = unidadesDeStaff(auth);
            pedidos = pedidoService.getDeUnidades(misUnidades.stream().map(UnidadDeVentaDTO::getIdUnidadDeVenta).toList());
            List<Integer> misFestivales = misUnidades.stream().map(UnidadDeVentaDTO::getFestivalId).distinct().toList();
            festivales = festivalService.getAll().stream().filter(f -> misFestivales.contains(f.getIdFestival())).toList();
            mAV.addObject("soloMisUnidades", true);
        }
        // Filtro por festival: por defecto el festival en curso (los pedidos actuales); 0 = todos, para ver los anteriores
        if (festivalId == null) {
            festivalId = festivales.stream().filter(FestivalDTO::isEnCurso).map(FestivalDTO::getIdFestival).findFirst().orElse(0);
        }
        if (festivalId != 0) {
            int id = festivalId;
            pedidos = pedidos.stream().filter(p -> p.getFestivalId() == id).toList();
        }
        mAV.addObject("pedidos", pedidos);
        mAV.addObject("festivales", festivales);
        mAV.addObject("festivalId", festivalId);
        // Para mostrar u ocultar el botón "+ Nuevo Pedido"
        mAV.addObject("puedeCargar", puedeCargarPedidos(auth));
        return mAV;
    }

    /** Paso 1: elegir la unidad. Paso 2 (con unidadId): aparecen los platos que ofrece esa unidad. */
    @GetMapping("/form")
    public ModelAndView form(@RequestParam(required = false, defaultValue = "0") int unidadId,
                             Authentication auth, RedirectAttributes redirectAttributes) {
        // Validación de carga: entre los empleados, solo los cajeros cargan pedidos
        if (!puedeCargarPedidos(auth)) {
            redirectAttributes.addFlashAttribute("mensajeError", "Solo los cajeros pueden cargar pedidos.");
            return new ModelAndView(new RedirectView(ViewRouteHelper.PEDIDO_ROOT, true));
        }
        List<UnidadDeVentaDTO> unidades = unidadesParaPedir(auth);
        // Si el usuario trabaja en una sola unidad, queda elegida directamente
        if (unidadId == 0 && unidades.size() == 1) {
            unidadId = unidades.get(0).getIdUnidadDeVenta();
        }
        PedidoDTO dto = unidadId > 0 ? pedidoService.nuevoParaUnidad(unidadId) : new PedidoDTO();
        if (dto.getFechaTransaccion() == null) {
            dto.setFechaTransaccion(LocalDate.now());
        }
        ModelAndView mAV = formView(dto, unidades);
        // Si no queda ninguna unidad, aclarar si es porque la caja de hoy ya fue cerrada
        if (unidades.isEmpty()) {
            mAV.addObject("cajaCerrada", unidadesConCajaCerradaHoy(auth));
        }
        return mAV;
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
            // Validación de carga: la caja de hoy de la unidad no puede estar cerrada
            if (cierreCajaService.estaCerrada(unidad.getIdUnidadDeVenta(), LocalDate.now())) {
                bindingResult.reject("error.pedido", "La caja de hoy de esta unidad ya fue cerrada: no se pueden cargar más pedidos.");
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
        mAV.addObject("cajaCerrada", List.of());
        mAV.addObject("hoy", LocalDate.now());
        return mAV;
    }

    private boolean esAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /**
     * Quién puede cargar pedidos: el administrador, el responsable y, entre los empleados,
     * solo los cajeros. Los cocineros pueden ver los pedidos de su unidad pero no cargarlos.
     */
    private boolean puedeCargarPedidos(Authentication auth) {
        if (auth == null) {
            return false;
        }
        boolean adminOResponsable = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_RESPONSABLE".equals(a.getAuthority()));
        if (adminOResponsable) {
            return true;
        }
        PersonaDTO persona = personaService.findByUsername(auth.getName());
        return persona != null && persona.getTipo() == TipoPersona.CAJERO;
    }

    /** Unidades activas en cuyo staff figura la persona logueada (vacío si la credencial no tiene persona). */
    private List<UnidadDeVentaDTO> unidadesDeStaff(Authentication auth) {
        PersonaDTO persona = auth != null ? personaService.findByUsername(auth.getName()) : null;
        return persona != null ? unidadService.getActivasDeStaff(persona.getIdPersona()) : List.of();
    }

    /** Unidades en las que el usuario puede cargar un pedido: todas si es admin, si no las suyas en festivales en curso. */
    /** Unidades vigentes del usuario cuya caja de hoy ya fue cerrada (no admiten más pedidos). */
    private List<String> unidadesConCajaCerradaHoy(Authentication auth) {
        if (!puedeCargarPedidos(auth) || esAdmin(auth)) {
            return List.of();
        }
        LocalDate hoy = LocalDate.now();
        return unidadesDeStaff(auth).stream().filter(u -> {
            FestivalDTO festival = festivalService.findById(u.getFestivalId());
            return festival != null && festival.isEnCurso()
                    && cierreCajaService.estaCerrada(u.getIdUnidadDeVenta(), hoy);
        }).map(UnidadDeVentaDTO::getNombreComercial).toList();
    }

    private List<UnidadDeVentaDTO> unidadesParaPedir(Authentication auth) {
        if (!puedeCargarPedidos(auth)) {
            return List.of();
        }
        if (esAdmin(auth)) {
            return unidadService.getAllActivas();
        }
        LocalDate hoy = LocalDate.now();
        return unidadesDeStaff(auth).stream().filter(u -> {
            FestivalDTO festival = festivalService.findById(u.getFestivalId());
            return festival != null && festival.isEnCurso()
                    // Con la caja de hoy cerrada ya no se cargan pedidos en esa unidad
                    && !cierreCajaService.estaCerrada(u.getIdUnidadDeVenta(), hoy);
        }).toList();
    }
}
