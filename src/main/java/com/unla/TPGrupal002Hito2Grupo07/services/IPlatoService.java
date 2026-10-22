package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PlatoDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.Plato;

import java.util.List;

public interface IPlatoService {
    public List<Plato> getAll();

    public PlatoDTO insertOrUpdate(PlatoDTO platoDTO);

    public PlatoDTO findById(int id);

    public boolean remove(int id);

    public PlatoDTO obtenerPlatoEstrella(int idFestival, int idUnidad);
}
