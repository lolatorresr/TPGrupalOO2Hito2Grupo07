package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Cierre de caja de una unidad de venta al finalizar el dia.
 * Lo hace un cajero que trabaja en la unidad; el monto es la suma de los pedidos del día.
 * Solo puede haber un cierre por unidad y por fecha.
 */
@Entity
@Table(name = "cierre_caja",
        uniqueConstraints = @UniqueConstraint(columnNames = {"unidad_venta_id", "fecha"}))
@Getter @Setter @NoArgsConstructor
public class CierreCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cierre_caja")
    private int idCierreCaja;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_venta_id", nullable = false)
    private UnidadDeVenta unidadVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cajero_id", nullable = false)
    private Cajero cajero;

    @Column(name = "cantidad_pedidos", nullable = false)
    private int cantidadPedidos;

    @Column(name = "monto_total", nullable = false)
    private double montoTotal;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public CierreCaja(LocalDate fecha, UnidadDeVenta unidadVenta, Festival festival, Cajero cajero,
                      int cantidadPedidos, double montoTotal) {
        this.fecha = fecha;
        this.unidadVenta = unidadVenta;
        this.festival = festival;
        this.cajero = cajero;
        this.cantidadPedidos = cantidadPedidos;
        this.montoTotal = montoTotal;
    }
}