package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.dtos.CierreCajaDTO;

import java.time.LocalDate;
import java.util.List;

public interface ICierreCajaService {

    /** true si el usuario logueado es un cajero (el único empleado que cierra caja). */
    public boolean esCajero(String username);

    /**
     * Caja de hoy de cada unidad donde trabaja el cajero, en festivales en curso:
     * pedidos y recaudación del día, y si ya se cerró.
     */
    public List<CierreCajaDTO> resumenDeHoy(String username);

    /** Cierra la caja de hoy de la unidad. Lanza IllegalArgumentException si no se puede. */
    public CierreCajaDTO cerrar(String username, int idUnidad);

    /** Cierres anteriores de las unidades donde trabaja el cajero, del más reciente al más antiguo. */
    public List<CierreCajaDTO> historial(String username);

    /** true si la caja de la unidad ya está cerrada en esa fecha (para no cargar más pedidos). */
    public boolean estaCerrada(int idUnidad, LocalDate fecha);
}