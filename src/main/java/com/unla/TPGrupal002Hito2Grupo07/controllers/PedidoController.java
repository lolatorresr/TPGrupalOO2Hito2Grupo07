package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.dtos.ItemPedidoDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PedidoDTO;
import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.IPedidoService;
import com.unla.TPGrupal002Hito2Grupo07.services.IUnidadDeVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;

/**
 * Alta y listado de pedidos. Un pedido registra la fecha, el festival, la unidad
 * de venta y los platos con su cantidad. Según el enunciado, solo puede cargarse
 * con la fecha del día.
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

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PEDIDO_INDEX);
        mAV.addObject("pedidos", pedidoService.getAll());
        return mAV;
    }

    /** Paso 1: elegir la unidad. Paso 2 (con unidadId): aparecen los platos que ofrece esa unidad. */
    @GetMapping("/form")
    public ModelAndView form(@RequestParam(required = false, defaultValue = "0") int unidadId) {
        PedidoDTO dto = unidadId > 0 ? pedidoService.nuevoParaUnidad(unidadId) : new PedidoDTO();
        if (dto.getFechaTransaccion() == null) {
            dto.setFechaTransaccion(LocalDate.now());
        }
        return formView(dto);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid @ModelAttribute(PEDIDO_ATTR) PedidoDTO dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
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
            return formView(dto);
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

    private ModelAndView formView(PedidoDTO dto) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.PEDIDO_FORM);
        mAV.addObject(PEDIDO_ATTR, dto);
        mAV.addObject("unidades", unidadService.getAllActivas());
        mAV.addObject("hoy", LocalDate.now());
        return mAV;
    }
}
