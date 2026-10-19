package com.unla.TPGrupal002Hito2Grupo07.dtos;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class UnidadDeVentaDTOTest {

	private static Validator validator;

	@BeforeAll
	static void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	private static boolean codigoTieneError(String codigo) {
		UnidadDeVentaDTO dto = new UnidadDeVentaDTO();
		dto.setNombreComercial("Burger Truck");
		dto.setSuperficie(10);
		dto.setTipo(TipoUnidad.FOOD_TRUCK);
		dto.setPatente("AB123CD");
		dto.setCodigo(codigo);
		Set<ConstraintViolation<UnidadDeVentaDTO>> errores = validator.validate(dto);
		return errores.stream().anyMatch(e -> e.getPropertyPath().toString().equals("codigo"));
	}

	@Test
	void aceptaCodigoConPrefijoDeTipoYSieteAlfanumericos() {
		assertFalse(codigoTieneError("FT-ABC1234"));
		assertFalse(codigoTieneError("PD-0000001"));
	}

	@Test
	void rechazaCodigoQueNoTieneDiezCaracteres() {
		assertTrue(codigoTieneError("FT-ABC"));
		assertTrue(codigoTieneError("FT-ABC12345"));
	}

	@Test
	void rechazaCodigoSinPrefijoDeTipoValido() {
		assertTrue(codigoTieneError("XX-ABC1234"));
		assertTrue(codigoTieneError("ft-abc1234"));
	}

	@Test
	void rechazaCodigoVacio() {
		assertTrue(codigoTieneError(""));
		assertTrue(codigoTieneError(null));
	}
}
