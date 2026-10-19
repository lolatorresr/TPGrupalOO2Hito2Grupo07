package com.unla.TPGrupal002Hito2Grupo07.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "festival")
@Getter @Setter @NoArgsConstructor
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_festival")
    private int idFestival;

    @Column(name ="nombre", nullable = false)
    private String nombre;

    @Column(name = "temporada")
    private String temporada;

    @Column(name = "fechaInicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fechaFin", nullable = false)
    private LocalDate fechaFin;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "festival")
    private Set<UnidadDeVenta> unidades = new HashSet<>();

    public Festival(String nombre, String temporada, LocalDate fechaInicio, LocalDate fechaFin) {
        this.nombre = nombre;
        this.temporada = temporada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}
