package com.unla.TPGrupal002Hito2Grupo07.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.unla.TPGrupal002Hito2Grupo07.helpers.ViewRouteHelper;
import com.unla.TPGrupal002Hito2Grupo07.services.IFestivalService;

/**
 * Página de inicio. Es pública: muestra los festivales y permite registrarse
 * como responsable o iniciar sesión. Con sesión iniciada muestra los accesos
 * a cada sección según el rol del usuario (datos de sesión: ver SesionAdvice).
 */
@Controller
@RequestMapping("/")
public class HomeController {

	@Autowired
	@Qualifier("festivalService")
	private IFestivalService festivalService;

	@GetMapping("/index")
	public ModelAndView index(Authentication authentication) {
		// El inicio general (festivales, registro de unidades) no es para el empleado: su inicio es su página
		if (authentication != null && authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"))) {
			return new ModelAndView("redirect:" + ViewRouteHelper.EMPLEADO_ROOT);
		}
		ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.INDEX);
		modelAndView.addObject("festivales", festivalService.getAll());
		return modelAndView;
	}

	//GET Example: SERVER/hello?name=someName
	@GetMapping("/hello")
	public ModelAndView helloParams1(@RequestParam(required=false, defaultValue="null") String name) {
		ModelAndView mV = new ModelAndView(ViewRouteHelper.HELLO);
		mV.addObject("name", name);
		return mV;
	}

	//GET Example: SERVER/hello/someName
	@GetMapping("/hello/{name}")
	public ModelAndView helloParams2(@PathVariable String name) {
		ModelAndView mV = new ModelAndView(ViewRouteHelper.HELLO);
		mV.addObject("name", name);
		return mV;
	}

	@GetMapping("/")
	public RedirectView redirectToHomeIndex() {
		return new RedirectView(ViewRouteHelper.ROUTE);
	}
}
