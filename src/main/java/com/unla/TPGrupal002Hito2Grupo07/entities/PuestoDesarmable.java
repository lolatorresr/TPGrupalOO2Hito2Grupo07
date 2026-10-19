package com.unla.TPGrupal002Hito2Grupo07.entities;

import com.unla.TPGrupal002Hito2Grupo07.helpers.CostosFestival;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "puesto_desarmable")
@Getter @Setter @NoArgsConstructor
public class PuestoDesarmable extends UnidadDeVenta {

    public static final String TIPO = "Puesto Desarmable";

    @Column(name = "cantidad_carpas", nullable = false)
    private int cantidadCarpas;

    @Column(name = "tiempo_montaje_minutos", nullable = false)
    private int tiempoMontajeMinutos;

    public PuestoDesarmable(String nombreComercial, String codigo, double superficie,
                            int cantidadCarpas, int tiempoMontajeMinutos) {
        super(nombreComercial, codigo, superficie);
        this.cantidadCarpas = cantidadCarpas;
        this.tiempoMontajeMinutos = tiempoMontajeMinutos;
    }

    /** (superficie * $500) - (tiempo de montaje * $10). */
    @Override
    public double calcularCanon() {
        return superficie * CostosFestival.COSTO_POR_M2
                - tiempoMontajeMinutos * CostosFestival.DESCUENTO_POR_MINUTO_MONTAJE;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
