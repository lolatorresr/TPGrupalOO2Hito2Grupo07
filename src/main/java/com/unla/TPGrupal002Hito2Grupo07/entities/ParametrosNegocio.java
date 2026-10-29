package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Valores económicos del negocio (sueldos y canon). Se guardan en la base (tabla parametros_negocio)
 * para no tener montos fijos en el código: si cambian, se actualiza la fila y no se recompila.
 */
@Entity
@Table(name = "parametros_negocio")
@Getter @Setter @NoArgsConstructor
public class ParametrosNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private int idParametro;

    // ----- Liquidación de sueldos -----
    @Column(name = "sueldo_base", nullable = false)
    private double sueldoBase;

    /** Monto por cada año de antigüedad (cajeros). */
    @Column(name = "valor_antiguedad", nullable = false)
    private double valorAntiguedad;

    @Column(name = "plus_cocinero", nullable = false)
    private double plusCocinero;

    @Column(name = "plus_ayudante", nullable = false)
    private double plusAyudante;

    @Column(name = "plus_lavaplatos", nullable = false)
    private double plusLavaplatos;

    // ----- Canon de las unidades de venta -----
    @Column(name = "costo_m2", nullable = false)
    private double costoM2;

    /** Plus fijo de un Food Truck que requiere conexión eléctrica. */
    @Column(name = "plus_electricidad", nullable = false)
    private double plusElectricidad;

    /** Descuento por cada minuto de montaje de un Puesto Desarmable. */
    @Column(name = "descuento_montaje", nullable = false)
    private double descuentoMontaje;

    public ParametrosNegocio(double sueldoBase, double valorAntiguedad, double plusCocinero,
                             double plusAyudante, double plusLavaplatos, double costoM2,
                             double plusElectricidad, double descuentoMontaje) {
        this.sueldoBase = sueldoBase;
        this.valorAntiguedad = valorAntiguedad;
        this.plusCocinero = plusCocinero;
        this.plusAyudante = plusAyudante;
        this.plusLavaplatos = plusLavaplatos;
        this.costoM2 = costoM2;
        this.plusElectricidad = plusElectricidad;
        this.descuentoMontaje = descuentoMontaje;
    }

    /** Plus que corresponde a cada categoría de cocina (0 si no tiene categoría). */
    public double getPlusCategoria(CategoriaCocina categoria) {
        if (categoria == null) {
            return 0;
        }
        return switch (categoria) {
            case COCINERO -> plusCocinero;
            case COCINERO_AYUDANTE -> plusAyudante;
            case LAVAPLATOS -> plusLavaplatos;
        };
    }
}