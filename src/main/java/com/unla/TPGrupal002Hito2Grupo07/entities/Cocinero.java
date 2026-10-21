package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cocinero")
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Persona {

    private String especialidad;

    private String categoria;

    public Cocinero(String nombre, String apellido, long dni, LocalDate fechaNacimiento,
                    LocalDate fechaIngreso, String email, User credencial,
                    String especialidad, String categoria) {
        super(nombre, apellido, dni, fechaNacimiento, fechaIngreso, email, credencial);
        this.especialidad = especialidad;
        this.categoria = categoria;
    }

    // TODO: calcular con los valores de la clase de parámetros (pendiente)
    @Override
    public double calcularSueldo(int diasFestival) {
        return 0;
    }
}
