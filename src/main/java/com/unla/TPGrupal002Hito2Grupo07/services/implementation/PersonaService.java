package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.TipoPersona;
import com.unla.TPGrupal002Hito2Grupo07.entities.Cajero;
import com.unla.TPGrupal002Hito2Grupo07.entities.Cocinero;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUserRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service("personaService")
public class PersonaService implements IPersonaService {

    @Autowired
    @Qualifier("personaRepository")
    private IPersonaRepository personaRepository;

    @Autowired
    @Qualifier("userRepository")
    private IUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDTO> getAllActivas() {
        return personaRepository.findByCredencialEnabledTrue().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO findById(int id) {
        return personaRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    @Transactional
    public PersonaDTO insertOrUpdate(PersonaDTO dto) {

        // Validacion: Mayor de edad
        if (dto.getFechaNacimiento() != null) {
            int edad = Period.between(dto.getFechaNacimiento(), LocalDate.now()).getYears();
            if (edad < 18) {
                throw new IllegalArgumentException("El empleado debe ser mayor de 18 anios.");
            }
        }

        // validacion de carga: DNI no duplicado
        if (existeDni(dto.getDni(), dto.getIdPersona())) {
            throw new IllegalArgumentException("El DNI ingresado ya pertenece a otro empleado.");
        }

        // Validacion de carga extra: Email no duplicado
        if (existeEmail(dto.getEmail(), dto.getIdPersona())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }


        Persona persona;
        if (dto.getIdPersona() > 0) {
            persona = personaRepository.findById(dto.getIdPersona()).orElseThrow();
        } else {
            persona = crearSegunTipo(dto.getTipo());
        }
        copiarDatos(dto, persona);
        return toDTO(personaRepository.save(persona));
    }

    @Override
    @Transactional
    public boolean remove(int id) {
        return cambiarActivo(id, false);
    }

    @Override
    @Transactional
    public boolean reactivar(int id) {
        return cambiarActivo(id, true);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeDni(long dni, int idActual) {
        return personaRepository.findByDni(dni)
                .map(p -> p.getIdPersona() != idActual)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email, int idActual) {
        return personaRepository.findByEmail(email)
                .map(p -> p.getIdPersona() != idActual)
                .orElse(false);
    }

    // El estado de activación vive en la credencial (User.enabled); sin credencial no se puede cambiar
    private boolean cambiarActivo(int id, boolean activo) {
        return personaRepository.findById(id)
                .filter(p -> p.getCredencial() != null)
                .map(p -> {
                    p.getCredencial().setEnabled(activo);
                    personaRepository.save(p);
                    return true;
                }).orElse(false);
    }

    private Persona crearSegunTipo(TipoPersona tipo) {
        return switch (tipo) {
            case CAJERO -> new Cajero();
            case COCINERO -> new Cocinero();
        };
    }

    /** Copia los campos editables del DTO a la entidad (comunes y los del subtipo). */
    private void copiarDatos(PersonaDTO dto, Persona persona) {
        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setDni(dto.getDni());
        persona.setFechaNacimiento(dto.getFechaNacimiento());
        persona.setFechaIngreso(dto.getFechaIngreso());
        persona.setEmail(dto.getEmail());


        if (dto.getUserId() > 0) {
            persona.setCredencial(userRepository.findById(dto.getUserId()).orElse(null));
        }

        if (persona instanceof Cajero c) {
            c.setTurnoTrabajo(dto.getTurnoTrabajo());
            c.setRecaudacionTotal(dto.getRecaudacionTotal());
        } else if (persona instanceof Cocinero co) {
            co.setEspecialidad(dto.getEspecialidad());
            co.setCategoria(dto.getCategoria());
        }
    }

    private PersonaDTO toDTO(Persona persona) {
        PersonaDTO dto = new PersonaDTO();
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombre(persona.getNombre());
        dto.setApellido(persona.getApellido());
        dto.setDni(persona.getDni());
        dto.setFechaNacimiento(persona.getFechaNacimiento());
        dto.setFechaIngreso(persona.getFechaIngreso());
        dto.setEmail(persona.getEmail());
        if (persona.getCredencial() != null) {
            dto.setUserId(persona.getCredencial().getId());
            dto.setActivo(persona.getCredencial().isEnabled());
        } else {
            dto.setActivo(false);
        }
        if (persona instanceof Cajero c) {
            dto.setTipo(TipoPersona.CAJERO);
            dto.setTurnoTrabajo(c.getTurnoTrabajo());
            dto.setRecaudacionTotal(c.getRecaudacionTotal());
        } else if (persona instanceof Cocinero co) {
            dto.setTipo(TipoPersona.COCINERO);
            dto.setEspecialidad(co.getEspecialidad());
            dto.setCategoria(co.getCategoria());
        }
        if (dto.getTipo() != null) {
            dto.setTipoDescripcion(dto.getTipo().getDescripcion());
        }
        return dto;
    }
}