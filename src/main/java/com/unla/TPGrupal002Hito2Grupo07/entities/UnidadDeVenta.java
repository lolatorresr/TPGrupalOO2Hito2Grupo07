package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "unidad_de_venta")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter @NoArgsConstructor
public abstract class UnidadDeVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad_de_venta")
    protected int idUnidadDeVenta;

    @Column(name = "nombre_comercial", nullable = false)
    protected String nombreComercial;

    /** Código único de 10 caracteres: prefijo de tipo (FT- / PD-) + 7 alfanuméricos. */
    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    protected String codigo;

    /** Superficie ocupada en m². */
    @Column(name = "superficie", nullable = false)
    protected double superficie;

    /** Baja lógica. */
    @Column(name = "activo", nullable = false)
    protected boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    protected Festival festival;

    /** Responsable a cargo: es un miembro del staff. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    protected Persona responsable;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "unidad_plato",
            joinColumns = @JoinColumn(name = "unidad_id"),
            inverseJoinColumns = @JoinColumn(name = "plato_id"))
    protected Set<Plato> platos = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "unidad_staff",
            joinColumns = @JoinColumn(name = "unidad_id"),
            inverseJoinColumns = @JoinColumn(name = "persona_id"))
    protected Set<Persona> staff = new HashSet<>();

    protected UnidadDeVenta(String nombreComercial, String codigo, double superficie) {
        this.nombreComercial = nombreComercial;
        this.codigo = codigo;
        this.superficie = superficie;
    }

    /** Monto de canon a pagar por la unidad, según su tipo. */
    public abstract double calcularCanon();

    /** Nombre legible del tipo de unidad, para vistas y reportes. */
    public abstract String getTipo();
}
