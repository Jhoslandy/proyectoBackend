package com.example.ProyectoTaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsisteDTO {

    private Long idAsiste; // Nuevo campo para el ID autoincrementable (puede ser nulo en la creación)

    @NotBlank(message = "El C.I. del estudiante es obligatorio")
    private String estudianteCi; // Corresponde a estudiante.ci

    @NotNull(message = "El ID del curso es obligatorio")
    private Integer cursoIdCurso; // Corresponde a curso.idCurso

    @NotNull(message = "La fecha de asistencia es obligatoria")
    @PastOrPresent(message = "La fecha de asistencia no puede ser futura")
    private LocalDate fecha;

    @NotNull(message = "El estado de presencia es obligatorio")
    private Boolean presente;
}