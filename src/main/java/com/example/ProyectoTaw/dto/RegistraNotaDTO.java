package com.example.ProyectoTaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO que representa los datos de un registro de nota para transferencia entre capas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistraNotaDTO implements Serializable {

    /** ID del registro de nota */
    private Long id;

    /** ID del estudiante al que pertenece la nota */
    @NotNull(message = "El ID del estudiante es obligatorio")
    private String estudianteId;

    /** ID del curso al que pertenece la nota */
    @NotNull(message = "El ID del curso es obligatorio")
    private Integer cursoId;

    /** Nombre de la evaluación */
    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    @Size(min = 3, max = 50, message = "La evaluación debe tener entre 3 y 50 caracteres")
    private String evaluacion;

    /** Nota obtenida */
    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota no puede ser menor a 0")
    @DecimalMax(value = "100.0", message = "La nota no puede ser mayor a 100")
    private Double nota;

    /** Fecha de la evaluación */
    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fecha;
}
