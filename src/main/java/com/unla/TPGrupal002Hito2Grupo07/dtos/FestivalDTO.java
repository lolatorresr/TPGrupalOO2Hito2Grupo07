package com.unla.TPGrupal002Hito2Grupo07.dtos;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Festival para las vistas: además de sus datos, informa el estado respecto
 * de la fecha de hoy (próximo / en curso / finalizado), la cantidad de días
 * que dura (dato base de la liquidación de sueldos) y cuántas unidades tiene.
 */
@Getter
@Setter
@NoArgsConstructor
public class FestivalDTO {

    public static final String PROXIMO = "Próximo";
    public static final String EN_CURSO = "En curso";
    public static final String FINALIZADO = "Finalizado";

    private int idFestival;
    private String nombre;
    private String temporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private long cantidadUnidades;

    public String getEstado() {
        LocalDate hoy = LocalDate.now();
        if (fechaInicio == null || fechaFin == null) {
            return "";
        }
        if (hoy.isBefore(fechaInicio)) {
            return PROXIMO;
        }
        return hoy.isAfter(fechaFin) ? FINALIZADO : EN_CURSO;
    }

    public boolean isFinalizado() {
        return FINALIZADO.equals(getEstado());
    }

    public boolean isEnCurso() {
        return EN_CURSO.equals(getEstado());
    }

    /** Días que dura el festival, contando el primero y el último. */
    public long getDiasDuracion() {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
    }
}
