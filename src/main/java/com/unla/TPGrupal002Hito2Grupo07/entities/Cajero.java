package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cajero")
@Getter @Setter @NoArgsConstructor
public class Cajero extends Persona {

    private String turnoTrabajo;

    private double recaudacionTotal;
}