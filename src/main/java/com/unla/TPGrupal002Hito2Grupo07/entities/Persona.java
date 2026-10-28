package com.unla.TPGrupal002Hito2Grupo07.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter @NoArgsConstructor
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int idPersona;

    @Column(name = "nombre", nullable = false, length = 50)
    protected String nombre;

    @Column(name = "apellido", nullable = false, length = 50)
    protected String apellido;

    @Column(name = "dni", nullable = false, unique = true)
    protected long dni;

    @Column(name = "fecha_nacimiento", nullable = false)
    protected LocalDate fechaNacimiento;

    @Column(name = "fecha_ingreso", nullable = false)
    protected LocalDate fechaIngreso;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    protected String email;


    // Relación 1 a 1 con el Usuario (Credenciales que maneja el estado de activación)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    protected User credencial;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    protected LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    protected LocalDateTime updatedAt;

    // Constructor con parámetros
    public Persona(String nombre, String apellido, long dni, LocalDate fechaNacimiento,
                   LocalDate fechaIngreso, String email, User credencial) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaIngreso = fechaIngreso;
        this.email = email;
        this.credencial = credencial;
    }

    // Método abstracto de negocio requerido por el diagrama
    public abstract double calcularSueldo(int diasFestival);
}