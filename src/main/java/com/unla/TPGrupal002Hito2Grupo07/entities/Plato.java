package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "plato")
@Getter @Setter @NoArgsConstructor
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plato")
    private int idPlato;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "precio", nullable = false)
    private double precio;

    @Column(name = "costo", nullable = false)
    private double costo;

    public Plato(String nombre, double precio, double costo){
        this.nombre = nombre;
        this.precio = precio;
        this.costo = costo;
    }
}
