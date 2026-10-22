package com.unla.TPGrupal002Hito2Grupo07.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Agrega a todas las vistas los datos de la sesión actual, para que la barra
 * de navegación del layout pueda mostrar el usuario y los accesos según el rol
 * sin que cada controlador tenga que cargarlos.
 */
@ControllerAdvice
public class SesionAdvice {

	private Authentication autenticacion() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean logueado = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
		return logueado ? auth : null;
	}

	@ModelAttribute("logueado")
	public boolean logueado() {
		return autenticacion() != null;
	}

	@ModelAttribute("username")
	public String username() {
		Authentication auth = autenticacion();
		return auth != null ? auth.getName() : null;
	}

	@ModelAttribute("roles")
	public List<String> roles() {
		Authentication auth = autenticacion();
		return auth != null ? auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList() : List.of();
	}

	/** Nombre legible del rol para mostrar en pantalla (ROLE_RESPONSABLE -> Responsable). */
	@ModelAttribute("rolNombre")
	public String rolNombre() {
		return roles().stream().map(r -> switch (r) {
			case "ROLE_ADMIN" -> "Administrador";
			case "ROLE_RESPONSABLE" -> "Responsable";
			case "ROLE_EMPLEADO" -> "Empleado";
			default -> r.replace("ROLE_", "");
		}).collect(Collectors.joining(", "));
	}
}
