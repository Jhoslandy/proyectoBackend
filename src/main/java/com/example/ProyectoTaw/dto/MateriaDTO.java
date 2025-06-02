package com.example.ProyectoTaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * DTO que representa los datos de una materia para transferencia entre capas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaDTO implements Serializable {

    /**
     * ID de la materia
     */
    private Long id;

    /**
     * Nombre de la materia
     */
    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombreMateria;

    /**
     * Código único de la materia
     */
    @NotBlank(message = "El código único es obligatorio")
    @Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres")
    private String codigoUnico;

    /**
     * Descripción de la materia
     */
    @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
    private String descripcion;

}
