package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.TipoUnidad;
import com.unla.TPGrupal002Hito2Grupo07.dtos.UnidadDeVentaDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.entities.FoodTruck;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import com.unla.TPGrupal002Hito2Grupo07.entities.PuestoDesarmable;
import com.unla.TPGrupal002Hito2Grupo07.entities.UnidadDeVenta;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUnidadDeVentaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IUnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("unidadDeVentaService")
public class UnidadDeVentaService implements IUnidadDeVentaService {

    @Autowired
    @Qualifier("unidadDeVentaRepository")
    private IUnidadDeVentaRepository unidadRepository;

    @Autowired
    @Qualifier("festivalRepository")
    private IFestivalRepository festivalRepository;

    @Autowired
    @Qualifier("personaRepository")
    private IPersonaRepository personaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UnidadDeVentaDTO> getAllActivas() {
        return unidadRepository.findByActivoTrue().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadDeVentaDTO findById(int id) {
        return unidadRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    @Transactional
    public UnidadDeVentaDTO insertOrUpdate(UnidadDeVentaDTO dto) {
        UnidadDeVenta unidad;
        if (dto.getIdUnidadDeVenta() > 0) {
            unidad = unidadRepository.findById(dto.getIdUnidadDeVenta()).orElseThrow();
        } else {
            unidad = crearSegunTipo(dto.getTipo());
        }
        copiarDatos(dto, unidad);
        return toDTO(unidadRepository.save(unidad));
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
    public boolean existeCodigo(String codigo, int idActual) {
        return unidadRepository.findByCodigo(codigo)
                .map(u -> u.getIdUnidadDeVenta() != idActual)
                .orElse(false);
    }

    private boolean cambiarActivo(int id, boolean activo) {
        return unidadRepository.findById(id).map(u -> {
            u.setActivo(activo);
            unidadRepository.save(u);
            return true;
        }).orElse(false);
    }

    private UnidadDeVenta crearSegunTipo(TipoUnidad tipo) {
        return switch (tipo) {
            case FOOD_TRUCK -> new FoodTruck();
            case PUESTO_DESARMABLE -> new PuestoDesarmable();
        };
    }

    /** Copia los campos editables del DTO a la entidad (comunes y los del subtipo). */
    private void copiarDatos(UnidadDeVentaDTO dto, UnidadDeVenta unidad) {
        unidad.setNombreComercial(dto.getNombreComercial());
        unidad.setCodigo(dto.getCodigo());
        unidad.setSuperficie(dto.getSuperficie());

        Festival festival = dto.getFestivalId() > 0
                ? festivalRepository.findById(dto.getFestivalId()).orElse(null) : null;
        unidad.setFestival(festival);

        Persona responsable = dto.getResponsableId() > 0
                ? personaRepository.findById(dto.getResponsableId()).orElse(null) : null;
        unidad.setResponsable(responsable);

        if (unidad instanceof FoodTruck ft) {
            ft.setPatente(dto.getPatente());
            ft.setRequiereConexionElectrica(dto.isRequiereConexionElectrica());
        } else if (unidad instanceof PuestoDesarmable pd) {
            pd.setCantidadCarpas(dto.getCantidadCarpas());
            pd.setTiempoMontajeMinutos(dto.getTiempoMontajeMinutos());
        }
    }

    private UnidadDeVentaDTO toDTO(UnidadDeVenta unidad) {
        UnidadDeVentaDTO dto = new UnidadDeVentaDTO();
        dto.setIdUnidadDeVenta(unidad.getIdUnidadDeVenta());
        dto.setNombreComercial(unidad.getNombreComercial());
        dto.setCodigo(unidad.getCodigo());
        dto.setSuperficie(unidad.getSuperficie());
        dto.setActivo(unidad.isActivo());
        dto.setCanon(unidad.calcularCanon());
        dto.setTipoDescripcion(unidad.getTipo());
        if (unidad.getFestival() != null) {
            dto.setFestivalId(unidad.getFestival().getIdFestival());
            dto.setFestivalNombre(unidad.getFestival().getNombre());
        }
        if (unidad.getResponsable() != null) {
            dto.setResponsableId(unidad.getResponsable().getIdPersona());
            dto.setResponsableNombre(unidad.getResponsable().getApellido() + ", " + unidad.getResponsable().getNombre());
        }
        if (unidad instanceof FoodTruck ft) {
            dto.setTipo(TipoUnidad.FOOD_TRUCK);
            dto.setPatente(ft.getPatente());
            dto.setRequiereConexionElectrica(ft.isRequiereConexionElectrica());
        } else if (unidad instanceof PuestoDesarmable pd) {
            dto.setTipo(TipoUnidad.PUESTO_DESARMABLE);
            dto.setCantidadCarpas(pd.getCantidadCarpas());
            dto.setTiempoMontajeMinutos(pd.getTiempoMontajeMinutos());
        }
        return dto;
    }
}
