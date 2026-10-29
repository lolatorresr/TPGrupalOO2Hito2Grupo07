package com.unla.TPGrupal002Hito2Grupo07.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
public class PedidoDTO {

    private int idPedido;

    @NotNull(message = "La fecha del pedido es obligatoria.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaTransaccion;

    @Positive(message = "Debe seleccionar la unidad de venta.")
    private int unidadId;

    /** Un renglón por cada plato que ofrece la unidad; se guardan solo los de cantidad mayor a 0. */
    @Valid
    private List<ItemPedidoDTO> items = new ArrayList<>();

    // Solo lectura, para listados
    private String unidadNombre;
    private String unidadCodigo;
    private int festivalId;
    private String festivalNombre;
    private int cantidadPlatos;
    private double total;

    /** Cantidad total de platos pedidos (suma de las cantidades). */
    public int getCantidadPlatos() {
        return items.stream().mapToInt(ItemPedidoDTO::getCantidad).sum();
    }

    /** true si hay al menos un plato con cantidad mayor a 0. */
    public boolean tienePlatos() {
        return getCantidadPlatos() > 0;
    }
}
