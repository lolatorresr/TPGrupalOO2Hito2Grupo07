package com.unla.TPGrupal002Hito2Grupo07.services;
import java.util.List;

import com.unla.TPGrupal002Hito2Grupo07.entities.Degree;
import com.unla.TPGrupal002Hito2Grupo07.dtos.DegreeDTO;


@SuppressWarnings("unused")
public interface IDegreeService {

	List<Degree> getAll();

	void insertOrUpdate(DegreeDTO degreeDTO);

	boolean remove(int id);
}

