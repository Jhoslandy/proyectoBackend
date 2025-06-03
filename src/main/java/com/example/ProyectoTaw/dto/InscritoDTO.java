package com.example.ProyectoTaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscritoDTO implements Serializable {

    private Long idInscrito;

    @NotBlank(message = "La C.I. del estudiante es obligatoria")
    @Size(min = 5, max = 10, message = "La C.I. del estudiante debe tener entre 5 y 10 caracteres")
    private String estudianteCi;

    @NotBlank(message = "El código único de la materia es obligatorio")
    @Size(min = 3, max = 20, message = "El código de materia debe tener entre 3 y 20 caracteres")
    private String materiaCodigoUnico;

    @NotNull(message = "La fecha de inscripción es obligatoria")
    @PastOrPresent(message = "La fecha de inscripción no puede ser futura")
    private LocalDate fechaInscripcion;
}