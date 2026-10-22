package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.RegistroResponsableDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.UnidadDeVentaDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.*;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUserRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUserRoleRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IRegistroService;
import com.unla.TPGrupal002Hito2Grupo07.services.IUnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("registroService")
public class RegistroService implements IRegistroService {

    private static final String ROL_RESPONSABLE = "ROLE_RESPONSABLE";

    @Autowired
    @Qualifier("personaRepository")
    private IPersonaRepository personaRepository;

    @Autowired
    @Qualifier("userRepository")
    private IUserRepository userRepository;

    @Autowired
    @Qualifier("userRoleRepository")
    private IUserRoleRepository userRoleRepository;

    @Autowired
    @Qualifier("festivalRepository")
    private IFestivalRepository festivalRepository;

    @Autowired
    @Qualifier("unidadDeVentaService")
    private IUnidadDeVentaService unidadService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UnidadDeVentaDTO registrar(RegistroResponsableDTO dto) {
        // 1. Usuario del responsable: la contraseña se guarda encriptada porque el login la compara con BCrypt
        User credencial = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), true);

        // 2. Persona responsable (la credencial se guarda en cascada)
        Responsable responsable = new Responsable(dto.getNombre(), dto.getApellido(), dto.getDni(),
                dto.getFechaNacimiento(), dto.getFechaIngreso(), dto.getEmail(), credencial);
        responsable = personaRepository.save(responsable);

        UserRole rol = new UserRole();
        rol.setUser(responsable.getCredencial());
        rol.setRole(ROL_RESPONSABLE);
        userRoleRepository.save(rol);

        // 3. Unidad de venta a cargo del responsable, reutilizando el alta del ABM
        UnidadDeVentaDTO unidad = new UnidadDeVentaDTO();
        unidad.setTipo(dto.getTipo());
        unidad.setNombreComercial(dto.getNombreComercial());
        unidad.setCodigo(dto.getCodigo());
        unidad.setSuperficie(dto.getSuperficie());
        unidad.setFestivalId(dto.getFestivalId());
        unidad.setResponsableId(responsable.getIdPersona());
        unidad.setPatente(dto.getPatente());
        unidad.setRequiereConexionElectrica(dto.isRequiereConexionElectrica());
        unidad.setCantidadCarpas(dto.getCantidadCarpas());
        unidad.setTiempoMontajeMinutos(dto.getTiempoMontajeMinutos());

        UnidadDeVentaDTO creada = unidadService.insertOrUpdate(unidad);

        // 4. El responsable es un miembro más del staff de su unidad
        unidadService.agregarAlStaff(creada.getIdUnidadDeVenta(), responsable.getIdPersona());
        return creada;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
