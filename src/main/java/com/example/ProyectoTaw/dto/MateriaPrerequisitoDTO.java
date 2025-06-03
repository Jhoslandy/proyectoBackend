package com.example.ProyectoTaw.dto;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO que representa una relación de prerrequisito entre materias.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaPrerequisitoDTO implements Serializable {

    /**
     * ID de la relación (opcional en creación, útil en actualizaciones o eliminación)
     */
    private Long id;

    /**
     * ID de la materia que requiere el prerrequisito
     */
    @NotNull(message = "El ID de la materia es obligatorio")
    private Long materiaId;

    /**
     * ID de la materia que actúa como prerrequisito
     */
    @NotNull(message = "El ID del prerrequisito es obligatorio")
    private Long prerequisitoId;

    
}
