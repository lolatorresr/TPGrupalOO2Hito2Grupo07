package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.TipoPersona;
import com.unla.TPGrupal002Hito2Grupo07.entities.Cajero;
import com.unla.TPGrupal002Hito2Grupo07.entities.Cocinero;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import com.unla.TPGrupal002Hito2Grupo07.entities.User;
import com.unla.TPGrupal002Hito2Grupo07.entities.UserRole;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUserRoleRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IPersonaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service("personaService")
public class PersonaService implements IPersonaService {

    // El alta de empleados siempre genera una credencial con este rol; no se elige desde el formulario
    private static final String ROL_EMPLEADO = "ROLE_EMPLEADO";

    private static final Logger logger = LoggerFactory.getLogger(PersonaService.class);

    @Autowired
    @Qualifier("personaRepository")
    private IPersonaRepository personaRepository;

    @Autowired
    @Qualifier("userRoleRepository")
    private IUserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

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

        // Validacion de carga: un cocinero debe tener categoría y especialidad
        if (dto.getTipo() == TipoPersona.COCINERO) {
            if (dto.getCategoria() == null) {
                throw new IllegalArgumentException("Debe seleccionar la categoría del cocinero.");
            }
            if (dto.getEspecialidad() == null || dto.getEspecialidad().isBlank()) {
                throw new IllegalArgumentException("La especialidad del cocinero es obligatoria.");
            }
        }


        Persona persona;
        String passwordGenerada = null;
        if (dto.getIdPersona() > 0) {
            persona = personaRepository.findById(dto.getIdPersona()).orElseThrow();
        } else {
            persona = crearSegunTipo(dto.getTipo());
            // En el alta se genera la credencial: el usuario es el email y la contraseña es aleatoria.
            // Se guarda encriptada porque el login la compara con BCrypt.
            passwordGenerada = UUID.randomUUID().toString().substring(0, 8);
            persona.setCredencial(new User(dto.getEmail(), passwordEncoder.encode(passwordGenerada), true));
        }
        copiarDatos(dto, persona);
        Persona personaGuardada = personaRepository.save(persona);
        PersonaDTO resultado = toDTO(personaGuardada);

        if (passwordGenerada != null) {
            UserRole userRole = new UserRole();
            userRole.setUser(personaGuardada.getCredencial());
            userRole.setRole(ROL_EMPLEADO);
            userRoleRepository.save(userRole);

            resultado.setMailEnviado(enviarCredenciales(dto.getEmail(), passwordGenerada));
        }
        return resultado;
    }

    // Si falla el envío (sin conexión, credenciales de Gmail mal configuradas) el alta se guarda igual
    private boolean enviarCredenciales(String email, String password) {
        try {
            emailService.enviarCredenciales(email, email, password);
            return true;
        } catch (MailException e) {
            logger.error("No se pudo enviar el mail de credenciales a {}", email, e);
            return false;
        }
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

        if (persona instanceof Cajero c) {
            c.setTurnoTrabajo(dto.getTurnoTrabajo());
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