package com.unla.TPGrupal002Hito2Grupo07.entities;

import com.unla.TPGrupal002Hito2Grupo07.helpers.CostosFestival;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "food_truck")
@Getter @Setter @NoArgsConstructor
public class FoodTruck extends UnidadDeVenta {

    public static final String TIPO = "Food Truck";

    @Column(name = "patente", nullable = false)
    private String patente;

    @Column(name = "requiere_conexion_electrica", nullable = false)
    private boolean requiereConexionElectrica;

    public FoodTruck(String nombreComercial, String codigo, double superficie,
                     String patente, boolean requiereConexionElectrica) {
        super(nombreComercial, codigo, superficie);
        this.patente = patente;
        this.requiereConexionElectrica = requiereConexionElectrica;
    }

    /** (superficie * $500) + $2000 si requiere conexión eléctrica. */
    @Override
    public double calcularCanon() {
        double canon = superficie * CostosFestival.COSTO_POR_M2;
        if (requiereConexionElectrica) {
            canon += CostosFestival.PLUS_ELECTRICIDAD;
        }
        return canon;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
