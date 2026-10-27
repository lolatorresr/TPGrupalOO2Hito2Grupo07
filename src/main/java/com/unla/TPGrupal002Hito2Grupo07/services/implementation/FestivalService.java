package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.unla.TPGrupal002Hito2Grupo07.dtos.FestivalDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.entities.UnidadDeVenta;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IFestivalService;

@Service("festivalService")
public class FestivalService implements IFestivalService {

    @Autowired
    @Qualifier("festivalRepository")
    private IFestivalRepository festivalRepository;

    @Override
    public List<FestivalDTO> getAll() {
        // Primero los que están en curso o por venir, después los que ya pasaron
        return festivalRepository.findAll().stream()
                .map(this::toDTO)
                .sorted(Comparator.comparing(FestivalDTO::isFinalizado)
                        .thenComparing(FestivalDTO::getFechaInicio))
                .toList();
    }

    @Override
    public List<FestivalDTO> getNoFinalizados() {
        return getAll().stream().filter(f -> !f.isFinalizado()).toList();
    }

    @Override
    public FestivalDTO findById(int id) {
        return festivalRepository.findById(id).map(this::toDTO).orElse(null);
    }

    private FestivalDTO toDTO(Festival festival) {
        FestivalDTO dto = new FestivalDTO();
        dto.setIdFestival(festival.getIdFestival());
        dto.setNombre(festival.getNombre());
        dto.setTemporada(festival.getTemporada());
        dto.setFechaInicio(festival.getFechaInicio());
        dto.setFechaFin(festival.getFechaFin());
        dto.setCantidadUnidades(festival.getUnidades().stream().filter(UnidadDeVenta::isActivo).count());
        return dto;
    }
}
