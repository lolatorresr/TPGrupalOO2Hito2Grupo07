package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Responsable a cargo de una unidad de venta. Es un miembro más del staff:
 * se registra junto con su unidad desde la página de inicio y obtiene
 * un usuario con rol ROLE_RESPONSABLE.
 */
@Entity
@Table(name = "responsable")
@Getter @Setter @NoArgsConstructor
public class Responsable extends Persona {

    public Responsable(String nombre, String apellido, long dni, LocalDate fechaNacimiento,
                       LocalDate fechaIngreso, String email, User credencial) {
        super(nombre, apellido, dni, fechaNacimiento, fechaIngreso, email, credencial);
    }

    /** Sueldo base * factor del festival (no tiene plus). */
    @Override
    public double calcularSueldo(int diasFestival, ParametrosNegocio parametros) {
        return parametros.getSueldoBase() * factorFestival(diasFestival);
    }}
