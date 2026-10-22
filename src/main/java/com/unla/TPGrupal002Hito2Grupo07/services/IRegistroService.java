package com.unla.TPGrupal002Hito2Grupo07.services;

import com.unla.TPGrupal002Hito2Grupo07.dtos.RegistroResponsableDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.UnidadDeVentaDTO;

public interface IRegistroService {

    /**
     * Registra en una sola transacción al responsable (con su usuario de rol
     * ROLE_RESPONSABLE) y la unidad de venta que queda a su cargo.
     * Devuelve la unidad creada.
     */
    public UnidadDeVentaDTO registrar(RegistroResponsableDTO dto);

    /** true si ya existe un usuario con ese nombre de usuario. */
    public boolean existeUsername(String username);
}
