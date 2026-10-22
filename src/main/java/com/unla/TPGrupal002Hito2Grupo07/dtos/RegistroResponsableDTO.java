package com.unla.TPGrupal002Hito2Grupo07.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Datos del formulario público de registro: una unidad de venta nueva
 * junto con la persona responsable y las credenciales de su usuario.
 */
@Getter @Setter @NoArgsConstructor
public class RegistroResponsableDTO {

    public static final int LARGO_MINIMO_PASSWORD = 6;

    // ---------- Unidad de venta ----------
    @NotNull(message = "Debe seleccionar el tipo de unidad.")
    private TipoUnidad tipo;

    @NotBlank(message = "El nombre comercial no puede estar vacío.")
    private String nombreComercial;

    @NotBlank(message = "El código es obligatorio.")
    @Size(min = UnidadDeVentaDTO.LARGO_CODIGO, max = UnidadDeVentaDTO.LARGO_CODIGO,
            message = "El código debe tener exactamente 10 caracteres.")
    @Pattern(regexp = UnidadDeVentaDTO.PATRON_CODIGO,
            message = "El código debe ser FT- o PD- seguido de 7 letras mayúsculas o números (ej: FT-ABC1234).")
    private String codigo;

    @Positive(message = "La superficie debe ser mayor a 0 m².")
    private double superficie;

    @Positive(message = "Debe seleccionar el festival en el que va a participar.")
    private int festivalId;

    // Food Truck
    private String patente;
    private boolean requiereConexionElectrica;

    // Puesto Desarmable
    @PositiveOrZero(message = "La cantidad de carpas no puede ser negativa.")
    private int cantidadCarpas;
    @PositiveOrZero(message = "El tiempo de montaje no puede ser negativo.")
    private int tiempoMontajeMinutos;

    // ---------- Responsable ----------
    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío.")
    private String apellido;

    @Positive(message = "El DNI debe ser mayor a 0.")
    private long dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotNull(message = "La fecha de ingreso es obligatoria.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaIngreso;

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El email no tiene un formato válido.")
    private String email;

    // ---------- Credenciales (obligatorias según el enunciado) ----------
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 4, max = 45, message = "El usuario debe tener entre 4 y 45 caracteres.")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = LARGO_MINIMO_PASSWORD, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    @NotBlank(message = "Debe repetir la contraseña.")
    private String confirmarPassword;
}
