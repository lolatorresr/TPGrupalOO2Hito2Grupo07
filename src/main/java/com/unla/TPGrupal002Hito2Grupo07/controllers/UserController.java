package com.unla.TPGrupal002Hito2Grupo07.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;


@Controller
public class UserController {

	@GetMapping("/login")
	public String login(Model model,
						@RequestParam(required=false) String error,
						@RequestParam(required=false) String logout) {
		model.addAttribute("error", error);
		model.addAttribute("logout", logout);
		return ViewRouteHelper.USER_LOGIN;
	}

	@GetMapping("/loginsuccess")
	public String loginCheck(Authentication authentication) {
		// El empleado va a su pagina, pero admin y responsable al inicio general
		boolean esEmpleado = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));
		return esEmpleado ? "redirect:/empleado" : "redirect:/index";
	}

	@GetMapping("/logout")
	public String logout(Model model) {
		return ViewRouteHelper.USER_LOGOUT;
	}
}
