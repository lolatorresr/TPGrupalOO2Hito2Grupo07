package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.*; // o javax.persistence.* dependiendo de tu versión de Spring Boot
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia de herencia
@Getter @Setter @NoArgsConstructor // Anotaciones de Lombok
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int idPersona;

    @Column(nullable = false)
    protected String nombre;

    @Column(nullable = false)
    protected String apellido;

    @Column(nullable = false, unique = true)
    protected long dni;

    protected LocalDate fechaNacimiento;

    protected LocalDate fechaIngreso;

    @Column(nullable = false, unique = true)
    protected String email;

    protected boolean activo = true; // Por defecto activo para la baja lógica

    // Relación 1 a 1 con el Usuario (Credenciales)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    protected User user; // Usa la entidad User que ya tienes en tu proyecto
}