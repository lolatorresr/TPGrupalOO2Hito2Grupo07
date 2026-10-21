package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cajero")
@Getter @Setter @NoArgsConstructor
public class Cajero extends Persona {

    private String turnoTrabajo;

    private double recaudacionTotal;

    public Cajero(String nombre, String apellido, long dni, LocalDate fechaNacimiento,
                  LocalDate fechaIngreso, String email, User credencial,
                  String turnoTrabajo, double recaudacionTotal) {
        super(nombre, apellido, dni, fechaNacimiento, fechaIngreso, email, credencial);
        this.turnoTrabajo = turnoTrabajo;
        this.recaudacionTotal = recaudacionTotal;
    }

    // TODO: calcular con los valores de la clase de parámetros (pendiente)
    @Override
    public double calcularSueldo(int diasFestival) {
        return 0;
    }
}
