package com.unla.TPGrupal002Hito2Grupo07.dtos;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Un renglón del pedido: un plato de la unidad y la cantidad pedida (0 = no se incluye). */
@Getter @Setter @NoArgsConstructor
public class ItemPedidoDTO {

    private int platoId;

    @PositiveOrZero(message = "La cantidad no puede ser negativa.")
    private int cantidad;

    // Solo lectura, para la vista
    private String platoNombre;
    private double precio;
    private double subTotal;

    public ItemPedidoDTO(int platoId, String platoNombre, double precio, int cantidad) {
        this.platoId = platoId;
        this.platoNombre = platoNombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.subTotal = precio * cantidad;
    }
}
