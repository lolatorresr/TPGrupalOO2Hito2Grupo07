package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.ILiquidacionService;
import com.unla.TPGrupal002Hito2Grupo07.services.IParametrosNegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service("liquidacionService")
public class LiquidacionService implements ILiquidacionService {

    @Autowired
    @Qualifier("personaRepository")
    private IPersonaRepository personaRepository;

    @Autowired
    @Qualifier("festivalRepository")
    private IFestivalRepository festivalRepository;

    @Autowired
    @Qualifier("parametrosNegocioService")
    private IParametrosNegocioService parametrosService;

    @Override
    public Persona obtenerPersona(int idPersona){
        return personaRepository.findById(idPersona).orElse(null);
    }

    @Override
    public Festival obtenerFestival(int idFestival){
        return festivalRepository.findById(idFestival).orElse(null);
    }

    @Override
    public double calcularSueldoTotal(int idPersona, int idFestival){
        Persona persona = obtenerPersona(idPersona);
        Festival festival = obtenerFestival(idFestival);

        if (persona == null || festival == null) return 0.0;

        long diasFestival = ChronoUnit.DAYS.between(festival.getFechaInicio(), festival.getFechaFin());

        // Cada tipo de persona calcula su sueldo (polimorfismo) con los valores guardados en la base
        return persona.calcularSueldo((int) diasFestival, parametrosService.getParametros());
    }
}