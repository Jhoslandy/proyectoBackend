package com.example.ProyectoTaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Entidad JPA que representa a un estudiante en el sistema universitario.
 * Solo incluye información personal y de matrícula.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "estudiante")
public class Estudiante {

    /**
     * CI del estudiante - ahora es la clave primaria.
     */
    @Id
    @Column(name = "ci", nullable = false, unique = true)
    @Min(value = 10000, message = "El CI debe tener al menos 5 dígitos")
    @Max(value = 9999999999L, message = "El CI no puede tener más de 10 dígitos")
    private Integer ci;

    /**
     * Nombre del estudiante.
     */
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    /**
     * Apellido del estudiante.
     */
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    /**
     * Email del estudiante.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Fecha de nacimiento del estudiante.
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Número de matrícula único del estudiante.
     */
    @Column(name = "nro_matricula", nullable = false, unique = true, length = 20)
    private String nroMatricula;

    // Nota: El campo 'usuario:id' se incluiría aquí cuando tengas la entidad Usuario.
}
