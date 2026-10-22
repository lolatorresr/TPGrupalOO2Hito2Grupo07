package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    // Se guarda el nombre (COCINERO, LAVAPLATOS...) y no la posición del enum
    @Enumerated(EnumType.STRING)
    private CategoriaCocina categoria;

    public Cocinero(String nombre, String apellido, long dni, LocalDate fechaNacimiento,
                    LocalDate fechaIngreso, String email, User credencial,
                    String especialidad, CategoriaCocina categoria) {
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
