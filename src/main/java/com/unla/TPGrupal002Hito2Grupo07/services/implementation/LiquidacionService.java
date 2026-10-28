package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.entities.Cajero;
import com.unla.TPGrupal002Hito2Grupo07.entities.Cocinero;
import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.ILiquidacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Service("liquidacionService")
public class LiquidacionService implements ILiquidacionService {

    @Autowired
    @Qualifier("personaRepository")
    private IPersonaRepository personaRepository;

    @Autowired
    @Qualifier("festivalRepository")
    private IFestivalRepository festivalRepository;

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

        double sueldoBase = 100000.0;
        double sueldoParcial = 0.0;

        if (persona instanceof Cocinero){
            Cocinero cocinero = (Cocinero) persona;

            String cat = (cocinero.getCategoria() != null) ? cocinero.getCategoria().getDescripcion() : "";
            double plusCategoria = obtenerPlusCocina(cat);
            sueldoParcial = sueldoBase + plusCategoria;
        } else if (persona instanceof Cajero){
            int aniosAntiguedad = Period.between(persona.getFechaIngreso(), LocalDate.now()).getYears();
            double plusAntiguedad = aniosAntiguedad * 5000.0;
            sueldoParcial = sueldoBase + plusAntiguedad;
        } else {
            sueldoParcial = sueldoBase;
        }

        long diasFestival = ChronoUnit.DAYS.between(festival.getFechaInicio(), festival.getFechaFin());
        if(diasFestival <=0) diasFestival = 1;

        double factorFestival = ((int)(diasFestival/15) + 1) * 0.5;

        return sueldoParcial * factorFestival;
    }

    private double obtenerPlusCocina(String categoria){
        if(categoria == null) return 0.0;
        switch(categoria.toUpperCase()){
            case "COCINERO": return 150000.0;
            case "COCINERO_AYUDANTE":
            case "AYUDANTE": return 100000.0;
            case "LAVAPLATOS":
            case "BACHERO": return 50000.0;
            default: return 0.0;
        }
    }
}
