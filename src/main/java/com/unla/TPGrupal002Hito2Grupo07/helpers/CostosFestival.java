package com.unla.TPGrupal002Hito2Grupo07.helpers;

/**
 * Parámetros económicos del festival (enunciado Hito 2).
 * Centralizados acá para no "hardcodear" valores en los métodos de negocio.
 */
public final class CostosFestival {

	/** Costo del canon por m² de superficie ocupada. */
	public static final double COSTO_POR_M2 = 500;

	/** Plus fijo que paga un Food Truck si requiere conexión eléctrica. */
	public static final double PLUS_ELECTRICIDAD = 2000;

	/** Descuento por cada minuto de montaje de un Puesto Desarmable. */
	public static final double DESCUENTO_POR_MINUTO_MONTAJE = 10;

	private CostosFestival() {
	}
}
