package com.unla.TPGrupal002Hito2Grupo07.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class PlatoDTO {

    private int idPlato;

    @NotBlank(message = "El nombre del plato no puede estar vacío.")
    private String nombre;

    @NotNull(message = "El precio es obligatorio.")
    @Positive(message = "El precio de venta debe ser mayor a 0.")
    private double precio;

    @NotNull(message = "El costo es obligatorio.")
    @PositiveOrZero(message = "El costo de producción no puede ser negativo.")
    private double costo;
    private int cantidad;

    public PlatoDTO(int idPlato, String nombre, double precio, double costo, int cantidad){
        this.idPlato = idPlato;
        this.nombre = nombre;
        this.precio = precio;
        this.costo = costo;
        this.cantidad = cantidad;
    }

    public PlatoDTO(String nombre, double precio, double costo){
        this.nombre = nombre;
        this.precio = precio;
        this.costo = costo;
    }

}
