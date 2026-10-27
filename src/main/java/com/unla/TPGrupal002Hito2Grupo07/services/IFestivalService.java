package com.unla.TPGrupal002Hito2Grupo07.services;

import java.util.List;

import com.unla.TPGrupal002Hito2Grupo07.dtos.FestivalDTO;

public interface IFestivalService {

    public List<FestivalDTO> getAll();

    /** Festivales en los que todavía se puede registrar una unidad (no finalizados). */
    public List<FestivalDTO> getNoFinalizados();

    public FestivalDTO findById(int id);
}
