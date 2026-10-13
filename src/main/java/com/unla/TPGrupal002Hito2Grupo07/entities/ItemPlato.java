package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_plato")
@Getter @Setter @NoArgsConstructor
public class ItemPlato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_plato")
    private int idItemPlato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plato_id", nullable = false)
    private Plato plato;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "sub_total")
    private double subTotal;

    public ItemPlato(Plato plato, int cantidad){
        this.plato = plato;
        this.cantidad = cantidad;
        if(plato != null){
            this.subTotal = plato.getPrecio() * cantidad;
        }
    }
}
