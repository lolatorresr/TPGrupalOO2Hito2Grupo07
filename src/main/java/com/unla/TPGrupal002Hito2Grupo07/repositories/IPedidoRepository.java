package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Serializable> {

    public abstract List<Pedido> findAllByOrderByFechaTransaccionDescIdPedidoDesc();

    public abstract List<Pedido> findByUnidadVenta_IdUnidadDeVentaOrderByFechaTransaccionDesc(int idUnidad);
}
