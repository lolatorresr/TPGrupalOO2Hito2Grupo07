package com.unla.TPGrupal002Hito2Grupo07.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UnidadDeVentaDTO {

    /** Formato del código único: prefijo de tipo (FT- o PD-) + 7 caracteres alfanuméricos en mayúscula. */
    public static final String PATRON_CODIGO = "^(FT|PD)-[A-Z0-9]{7}$";
    public static final int LARGO_CODIGO = 10;

    private int idUnidadDeVenta;

    @NotNull(message = "Debe seleccionar el tipo de unidad.")
    private TipoUnidad tipo;

    @NotBlank(message = "El nombre comercial no puede estar vacío.")
    private String nombreComercial;

    @NotBlank(message = "El código es obligatorio.")
    @Size(min = LARGO_CODIGO, max = LARGO_CODIGO, message = "El código debe tener exactamente 10 caracteres.")
    @Pattern(regexp = PATRON_CODIGO, message = "El código debe ser FT- o PD- seguido de 7 letras mayúsculas o números (ej: FT-ABC1234).")
    private String codigo;

    @Positive(message = "La superficie debe ser mayor a 0 m².")
    private double superficie;

    private int festivalId;

    private int responsableId;

    // Food Truck
    private String patente;
    private boolean requiereConexionElectrica;

    // Puesto Desarmable
    @PositiveOrZero(message = "La cantidad de carpas no puede ser negativa.")
    private int cantidadCarpas;
    @PositiveOrZero(message = "El tiempo de montaje no puede ser negativo.")
    private int tiempoMontajeMinutos;

    // Solo lectura, para listados
    private String tipoDescripcion;
    private double canon;
    private boolean activo = true;
    private String festivalNombre;
    private String responsableNombre;
}
