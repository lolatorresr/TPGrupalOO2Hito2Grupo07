package com.unla.TPGrupal002Hito2Grupo07.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.unla.TPGrupal002Hito2Grupo07.entities.CategoriaCocina;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class PersonaDTO {

    private int idPersona;

    @NotNull(message = "Debe seleccionar el tipo de empleado.")
    private TipoPersona tipo;

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

    // Solo el id del usuario asociado; no se expone la entidad User (password, roles)
    private int userId;

    // Cajero
    private String turnoTrabajo;

    // Cocinero
    private String especialidad;
    private CategoriaCocina categoria;

    // Solo lectura, para listados
    private String tipoDescripcion;
    private boolean activo = true;
    // false si el alta se guardó pero no se pudo enviar el mail con las credenciales
    private boolean mailEnviado = true;
}
