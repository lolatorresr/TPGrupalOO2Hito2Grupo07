package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;

public interface ILiquidacionService {

    public double calcularSueldoTotal(int idPersona, int idFestival);

    public Persona obtenerPersona(int idPersona);

    public Festival obtenerFestival(int idFestival);
}
