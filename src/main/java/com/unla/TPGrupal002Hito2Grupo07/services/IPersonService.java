package com.unla.TPGrupal002Hito2Grupo07.services;
import java.util.List;
import java.util.Optional;

import com.unla.TPGrupal002Hito2Grupo07.entities.Person;
import com.unla.TPGrupal002Hito2Grupo07.dtos.PersonDTO;

public interface IPersonService {

	List<Person> getAll();

	Optional<Person> findById(int id);

	Person findByName(String name);

	Person insertOrUpdate(Person person);

	boolean remove(int id);

	List<PersonDTO> findByDegreeName(String degreeName);
}

