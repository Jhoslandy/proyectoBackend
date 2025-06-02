package com.example.ProyectoTaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "materia")
public class Materia {

    /**
     * ID único de la materia.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la materia.
     */
    @Column(name = "nombre_materia", nullable = false, length = 100)
    @NotBlank(message = "El nombre de la materia es obligatorio")
    private String nombre;

    /**
     * Código único de la materia.
     */
    @Column(name = "codigo_unico", nullable = false, unique = true, length = 20)
    @NotBlank(message = "El código único es obligatorio")
    private String codigoUnico;

    /**
     * Descripción de la materia.
     */
    @Column(name = "descripcion", length = 300)
    private String descripcion;
}
