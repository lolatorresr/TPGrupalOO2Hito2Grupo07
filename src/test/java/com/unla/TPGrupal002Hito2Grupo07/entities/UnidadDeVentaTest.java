package com.unla.TPGrupal002Hito2Grupo07.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UnidadDeVentaTest {

	// Mismos valores que la fila de parametros_negocio en data.sql
	private final ParametrosNegocio parametros =
			new ParametrosNegocio(100000, 5000, 150000, 100000, 50000, 500, 2000, 10);

	@Test
	void foodTruckSinElectricidadPagaSoloSuperficie() {
		FoodTruck ft = new FoodTruck("Burger Truck", "FT-0000001", 10, "AB123CD", false);
		assertEquals(5000, ft.calcularCanon(parametros));
	}

	@Test
	void foodTruckConElectricidadSumaPlus() {
		FoodTruck ft = new FoodTruck("Burger Truck", "FT-0000001", 10, "AB123CD", true);
		assertEquals(7000, ft.calcularCanon(parametros));
	}

	@Test
	void puestoDesarmableDescuentaMinutosDeMontaje() {
		PuestoDesarmable pd = new PuestoDesarmable("Choripanes", "PD-0000001", 20, 2, 90);
		assertEquals(9100, pd.calcularCanon(parametros));
	}
}