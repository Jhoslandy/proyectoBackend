package com.example.ProyectoTaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeDaDTO implements Serializable {

    private Long idSeDa; // Nuevo campo para el ID autoincrementable (puede ser nulo en la creación)

    @NotBlank(message = "El código único de la materia es obligatorio")
    @Size(min = 3, max = 20, message = "El código de materia debe tener entre 3 y 20 caracteres")
    private String materiaCodigoUnico; // Corresponde a materia.codigoUnico

    @NotNull(message = "El ID del curso es obligatorio")
    private Integer cursoIdCurso; // Corresponde a curso.idCurso
}