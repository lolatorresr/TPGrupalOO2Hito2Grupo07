package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.Pedido;
import com.unla.TPGrupal002Hito2Grupo07.entities.Plato;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {

    @Query("SELECT ip.plato FROM Pedido p JOIN p.ItemPlatos ip " +
            "WHERE p.festival.idFestival = :idFestival " +
            "AND p.unidadVenta.idUnidadDeVenta = :idUnidad "+
            "GROUP BY ip.plato "+
            "ORDER BY SUM(ip.cantidad) DESC")
    List<Plato> findPlatoEstrella(@Param("idFestival") int idFestival,
                                  @Param("idUnidad") int idUnidad,
                                    Pageable pageable);
}
