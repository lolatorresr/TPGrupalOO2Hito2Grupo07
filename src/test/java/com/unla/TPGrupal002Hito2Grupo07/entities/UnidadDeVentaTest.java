package com.unla.TPGrupal002Hito2Grupo07.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UnidadDeVentaTest {

	@Test
	void foodTruckSinElectricidadPagaSoloSuperficie() {
		FoodTruck ft = new FoodTruck("Burger Truck", "FT-0000001", 10, "AB123CD", false);
		assertEquals(5000, ft.calcularCanon());
	}

	@Test
	void foodTruckConElectricidadSumaPlus() {
		FoodTruck ft = new FoodTruck("Burger Truck", "FT-0000001", 10, "AB123CD", true);
		assertEquals(7000, ft.calcularCanon());
	}

	@Test
	void puestoDesarmableDescuentaMinutosDeMontaje() {
		PuestoDesarmable pd = new PuestoDesarmable("Choripanes", "PD-0000001", 20, 2, 90);
		assertEquals(9100, pd.calcularCanon());
	}
}
