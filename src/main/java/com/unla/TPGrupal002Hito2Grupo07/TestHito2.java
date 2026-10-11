package com.unla.TPGrupal002Hito2Grupo07;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestHito2 {

	public static void main(String[] args) {
		BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
		System.out.println(pe.encode("user"));
	}
}
