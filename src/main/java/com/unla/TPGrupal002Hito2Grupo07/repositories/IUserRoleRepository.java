package com.unla.TPGrupal002Hito2Grupo07.repositories;

import com.unla.TPGrupal002Hito2Grupo07.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("userRoleRepository")
public interface IUserRoleRepository extends JpaRepository<UserRole, Serializable> {
}
