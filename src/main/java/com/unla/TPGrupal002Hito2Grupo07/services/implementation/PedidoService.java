package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.ItemPedidoDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PedidoDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.ItemPlato;
import com.unla.TPGrupal002Hito2Grupo07.entities.Pedido;
import com.unla.TPGrupal002Hito2Grupo07.entities.Plato;
import com.unla.TPGrupal002Hito2Grupo07.entities.UnidadDeVenta;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPedidoRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPlatoRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUnidadDeVentaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service("pedidoService")
public class PedidoService implements IPedidoService {

    @Autowired
    @Qualifier("pedidoRepository")
    private IPedidoRepository pedidoRepository;

    @Autowired
    @Qualifier("unidadDeVentaRepository")
    private IUnidadDeVentaRepository unidadRepository;

    @Autowired
    @Qualifier("platoRepository")
    private IPlatoRepository platoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> getAll() {
        return pedidoRepository.findAllByOrderByFechaTransaccionDescIdPedidoDesc()
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> getDeUnidades(Collection<Integer> idsUnidades) {
        return getAll().stream().filter(p -> idsUnidades.contains(p.getUnidadId())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDTO findById(int id) {
        return pedidoRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDTO nuevoParaUnidad(int idUnidad) {
        PedidoDTO dto = new PedidoDTO();
        dto.setFechaTransaccion(LocalDate.now());
        dto.setUnidadId(idUnidad);
        unidadRepository.findById(idUnidad).ifPresent(unidad -> {
            dto.setUnidadNombre(unidad.getNombreComercial());
            dto.setUnidadCodigo(unidad.getCodigo());
            if (unidad.getFestival() != null) {
                dto.setFestivalNombre(unidad.getFestival().getNombre());
            }
            unidad.getPlatos().stream()
                    .sorted(Comparator.comparing(Plato::getNombre))
                    .forEach(p -> dto.getItems().add(new ItemPedidoDTO(p.getIdPlato(), p.getNombre(), p.getPrecio(), 0)));
        });
        return dto;
    }

    @Override
    @Transactional
    public PedidoDTO insert(PedidoDTO dto) {
        UnidadDeVenta unidad = unidadRepository.findById(dto.getUnidadId()).orElseThrow();
        // El festival del pedido es el de la unidad que lo vende
        Pedido pedido = new Pedido(dto.getFechaTransaccion(), unidad.getFestival(), unidad);
        for (ItemPedidoDTO item : dto.getItems()) {
            if (item.getCantidad() <= 0) {
                continue;
            }
            Plato plato = platoRepository.findById(item.getPlatoId()).orElseThrow();
            // ItemPlato calcula el subtotal con el precio actual del plato
            pedido.getItemPlatos().add(new ItemPlato(plato, item.getCantidad()));
        }
        return toDTO(pedidoRepository.save(pedido));
    }

    private PedidoDTO toDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaTransaccion(pedido.getFechaTransaccion());
        if (pedido.getUnidadVenta() != null) {
            dto.setUnidadId(pedido.getUnidadVenta().getIdUnidadDeVenta());
            dto.setUnidadNombre(pedido.getUnidadVenta().getNombreComercial());
            dto.setUnidadCodigo(pedido.getUnidadVenta().getCodigo());
        }
        if (pedido.getFestival() != null) {
            dto.setFestivalId(pedido.getFestival().getIdFestival());
            dto.setFestivalNombre(pedido.getFestival().getNombre());
        }
        double total = 0;
        for (ItemPlato item : pedido.getItemPlatos()) {
            ItemPedidoDTO i = new ItemPedidoDTO(item.getPlato().getIdPlato(), item.getPlato().getNombre(),
                    item.getPlato().getPrecio(), item.getCantidad());
            i.setSubTotal(item.getSubTotal());
            dto.getItems().add(i);
            total += item.getSubTotal();
        }
        dto.getItems().sort(Comparator.comparing(ItemPedidoDTO::getPlatoNombre));
        dto.setTotal(total);
        return dto;
    }
}
