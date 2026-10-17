package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cocinero")
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Persona {

    private String especialidad;

    private String categoria;
}