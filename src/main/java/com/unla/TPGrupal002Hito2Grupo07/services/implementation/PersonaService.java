package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.AsignacionDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonaDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.TipoPersona;
import com.unla.TPGrupal002Hito2Grupo07.entities.*;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUnidadDeVentaRepository;
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
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
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

    @Autowired
    @Qualifier("unidadDeVentaRepository")
    private IUnidadDeVentaRepository unidadRepository;

    @Autowired
    @Qualifier("userRepository")
    private IUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDTO> getAllActivas() {
        return personaRepository.findEmpleadosActivos().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDTO> getAllInactivas() {
        return personaRepository.findEmpleadosInactivos()
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO findById(int id) {
        return personaRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO findByUsername(String username) {
        return personaRepository.findByCredencial_Username(username).map(this::toDTO).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDTO> getAsignaciones(int idPersona) {
        LocalDate hoy = LocalDate.now();
        return unidadRepository.findActivasByStaff(idPersona).stream()
                .map(u -> {
                    Festival f = u.getFestival();
                    boolean enCurso = f != null
                            && !hoy.isBefore(f.getFechaInicio()) && !hoy.isAfter(f.getFechaFin());
                    return new AsignacionDTO(u.getNombreComercial(), u.getCodigo(), u.getTipo(),
                            f != null ? f.getNombre() : "Sin festival",
                            f != null ? f.getFechaInicio() : null,
                            f != null ? f.getFechaFin() : null,
                            enCurso);
                })
                // Los festivales más recientes primero
                .sorted(Comparator.comparing(AsignacionDTO::getFechaInicio,
                        Comparator.nullsLast(Comparator.<LocalDate>reverseOrder())))
                .toList();
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

        // Validación de carga: DNI no duplicado.
        // Si el DNI es de un empleado dado de baja, no se crea otro: se lo debe reactivar.
        personaRepository.findByDni(dto.getDni())
                .filter(p -> p.getIdPersona() != dto.getIdPersona())
                .ifPresent(p -> {
                    boolean dadoDeBaja = p.getCredencial() != null && !p.getCredencial().isEnabled();
                    throw new IllegalArgumentException(dadoDeBaja
                            ? "Ese DNI pertenece a " + p.getApellido() + ", " + p.getNombre()
                            + ", que está dado de baja. Reactivalo desde la lista de empleados dados de baja."
                            : "El DNI ingresado ya pertenece a otro empleado.");
                });

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
            // Si cambia el email, cambia también el usuario con el que inicia sesión
            User credencial = persona.getCredencial();
            if (credencial != null && !credencial.getUsername().equals(dto.getEmail())) {
                userRepository.findByUsername(dto.getEmail())
                        .filter(otro -> otro.getId() != credencial.getId())
                        .ifPresent(otro -> {
                            throw new IllegalArgumentException("Ya existe un usuario con ese email.");
                        });
                credencial.setUsername(dto.getEmail());
            }
        } else {
            // Validación de carga: el email será el usuario, no puede estar tomado por otra cuenta
            if (userRepository.findByUsername(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Ya existe un usuario con ese email.");
            }
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
            case RESPONSABLE -> new Responsable();
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
        } else if (persona instanceof Responsable) {
            dto.setTipo(TipoPersona.RESPONSABLE);
        }
        if (dto.getTipo() != null) {
            dto.setTipoDescripcion(dto.getTipo().getDescripcion());
        }
        return dto;
    }
}