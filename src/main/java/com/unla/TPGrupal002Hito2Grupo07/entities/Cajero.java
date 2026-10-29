package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "cajero")
@Getter @Setter @NoArgsConstructor
public class Cajero extends Persona {

    private String turnoTrabajo;


    public Cajero(String nombre, String apellido, long dni, LocalDate fechaNacimiento,
                  LocalDate fechaIngreso, String email, User credencial,
                  String turnoTrabajo) {
        super(nombre, apellido, dni, fechaNacimiento, fechaIngreso, email, credencial);
        this.turnoTrabajo = turnoTrabajo;
    }

    /** (Sueldo base + años de antigüedad * valor por año) * factor del festival. */
    @Override
    public double calcularSueldo(int diasFestival, ParametrosNegocio parametros) {
        int aniosAntiguedad = Period.between(fechaIngreso, LocalDate.now()).getYears();
        double sueldoParcial = parametros.getSueldoBase() + aniosAntiguedad * parametros.getValorAntiguedad();
        return sueldoParcial * factorFestival(diasFestival);
    }
}