package com.unla.TPGrupal002Hito2Grupo07.dtos;

/** Roles que se pueden asignar a la credencial que se genera en el alta de un empleado. */
public enum RolUsuario {
    ADMIN("ROLE_ADMIN", "Administrador"),
    RESPONSABLE("ROLE_RESPONSABLE", "Responsable"),
    EMPLEADO("ROLE_EMPLEADO", "Empleado");

    private final String role;
    private final String descripcion;

    RolUsuario(String role, String descripcion) {
        this.role = role;
        this.descripcion = descripcion;
    }

    /** Valor que se guarda en user_role.role */
    public String getRole() {
        return role;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
