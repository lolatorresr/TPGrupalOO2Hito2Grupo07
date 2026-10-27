package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PedidoDTO;

import java.util.Collection;
import java.util.List;

public interface IPedidoService {

    /** Todos los pedidos, del más reciente al más antiguo. */
    public List<PedidoDTO> getAll();

    /** Pedidos de las unidades indicadas (los que puede ver un empleado o responsable), del más reciente al más antiguo. */
    public List<PedidoDTO> getDeUnidades(Collection<Integer> idsUnidades);

    public PedidoDTO findById(int id);

    /**
     * Arma un pedido vacío para la unidad indicada: fecha de hoy y un renglón
     * con cantidad 0 por cada plato que ofrece la unidad.
     */
    public PedidoDTO nuevoParaUnidad(int idUnidad);

    /** Alta del pedido con sus ítems (solo los de cantidad mayor a 0). */
    public PedidoDTO insert(PedidoDTO dto);
}
