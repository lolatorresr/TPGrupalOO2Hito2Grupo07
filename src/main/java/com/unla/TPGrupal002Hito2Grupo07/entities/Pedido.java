package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pedido")
@Getter @Setter @NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private int idPedido;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDate fechaTransaccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_venta_id", nullable = false)
    private UnidadDeVenta unidadVenta;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private Set<ItemPlato> ItemPlatos = new HashSet<>();

    public Pedido(LocalDate fechaTransaccion, Festival festival, UnidadDeVenta unidadVenta){
        this.fechaTransaccion = fechaTransaccion;
        this.festival = festival;
        this.unidadVenta = unidadVenta;
    }
}
