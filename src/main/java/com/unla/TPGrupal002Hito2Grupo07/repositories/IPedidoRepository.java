package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.Pedido;
import com.unla.TPGrupal002Hito2Grupo07.entities.Plato;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

import java.util.List;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {

    // ---------- Alta de pedido (Lucía) ----------
    public abstract List<Pedido> findAllByOrderByFechaTransaccionDescIdPedidoDesc();

    public abstract List<Pedido> findByUnidadVenta_IdUnidadDeVentaOrderByFechaTransaccionDesc(int idUnidad);

    // ---------- Ranking por recaudación (Lucía) ----------
    /** Por unidad del festival: id de la unidad, recaudación (suma de subtotales) y cantidad de pedidos. */
    @Query("SELECT p.unidadVenta.idUnidadDeVenta, SUM(ip.subTotal), COUNT(DISTINCT p.idPedido) "
            + "FROM Pedido p JOIN p.ItemPlatos ip "
            + "WHERE p.festival.idFestival = :idFestival "
            + "GROUP BY p.unidadVenta.idUnidadDeVenta "
            + "ORDER BY SUM(ip.subTotal) DESC")
    List<Object[]> recaudacionPorUnidad(@Param("idFestival") int idFestival);

    // ---------- Rentabilidad neta (Lucía) ----------
    /** Ventas, costo de los platos vendidos y cantidad de pedidos de una unidad en un festival entre dos fechas. */
    @Query("SELECT COALESCE(SUM(ip.subTotal), 0), COALESCE(SUM(ip.cantidad * ip.plato.costo), 0), COUNT(DISTINCT p.idPedido) "
            + "FROM Pedido p JOIN p.ItemPlatos ip "
            + "WHERE p.festival.idFestival = :idFestival AND p.unidadVenta.idUnidadDeVenta = :idUnidad "
            + "AND p.fechaTransaccion BETWEEN :desde AND :hasta")
    List<Object[]> ventasYCostos(@Param("idFestival") int idFestival, @Param("idUnidad") int idUnidad,
                                 @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    // ---------- Plato estrella (Lola) ----------
    @Query("SELECT ip.plato FROM Pedido p JOIN p.ItemPlatos ip " +
            "WHERE p.festival.idFestival = :idFestival " +
            "AND p.unidadVenta.idUnidadDeVenta = :idUnidad " +
            "GROUP BY ip.plato " +
            "ORDER BY SUM(ip.cantidad) DESC")
    List<Plato> findPlatoEstrella(@Param("idFestival") int idFestival,
                                  @Param("idUnidad") int idUnidad,
                                  Pageable pageable);


    // ---------- Cierre de caja (Juan Pablo) ----------
    /** Recaudación del día de una unidad: suma de los subtotales de sus pedidos de esa fecha (0 si no hubo). */
    @Query("SELECT COALESCE(SUM(ip.subTotal), 0) FROM Pedido p JOIN p.ItemPlatos ip "
            + "WHERE p.unidadVenta.idUnidadDeVenta = :idUnidad AND p.fechaTransaccion = :fecha")
    double recaudacionDelDia(@Param("idUnidad") int idUnidad, @Param("fecha") LocalDate fecha);

    /** Cantidad de pedidos del día de una unidad. */
    @Query("SELECT COUNT(p) FROM Pedido p "
            + "WHERE p.unidadVenta.idUnidadDeVenta = :idUnidad AND p.fechaTransaccion = :fecha")
    long cantidadPedidosDelDia(@Param("idUnidad") int idUnidad, @Param("fecha") LocalDate fecha);

    /** Pedidos del día de una unidad, para mostrar el detalle antes de cerrar la caja. */
    public abstract List<Pedido> findByUnidadVenta_IdUnidadDeVentaAndFechaTransaccionOrderByIdPedidoAsc(int idUnidad, LocalDate fecha);

    /** Cantidad total vendida de un plato por una unidad en un festival (para el plato estrella). */
    @Query("SELECT COALESCE(SUM(ip.cantidad), 0) FROM Pedido p JOIN p.ItemPlatos ip "
            + "WHERE p.festival.idFestival = :idFestival AND p.unidadVenta.idUnidadDeVenta = :idUnidad "
            + "AND ip.plato.idPlato = :idPlato")
    long cantidadVendida(@Param("idFestival") int idFestival, @Param("idUnidad") int idUnidad, @Param("idPlato") int idPlato);
}
