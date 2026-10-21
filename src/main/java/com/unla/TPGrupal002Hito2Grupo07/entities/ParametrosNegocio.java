package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parametros_negocio")
@Getter @Setter @NoArgsConstructor
public class ParametrosNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idParametro;

    private double sueldoBase;
    private double valorAntiguedad;
    private double plusCocinero;
    private double plusAyudante;
    private double plusLavaplatos;
    private double costoM2;
    private double plusElectricidad;
    private double descuentoMontaje;

    // Constructor con los parámetros para facilitar la inicialización
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
}