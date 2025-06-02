package com.example.ProyectoTaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*; // Import for validation annotations

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoDTO {

    private Integer idCurso;

    @NotBlank(message = "El día es obligatorio")
    @Pattern(regexp = "^(Lunes|Martes|Miércoles|Miercoles|Jueves|Viernes|Sábado|Sabado)$",
             message = "El día debe ser uno de: Lunes, Martes, Miércoles, Jueves, Viernes, Sábado.")
    private String dia; // Changed to String

    @NotBlank(message = "El horario es obligatorio")
    // Regex for "HH:mm a HH:mm" format. Allows 0-23 for hours and 0-59 for minutes.
    // Handles optional leading zero for hours (e.g., "9:00" or "09:00")
    @Pattern(regexp = "^(0?[0-9]|1[0-9]|2[0-3]):[0-5][0-9] a (0?[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
             message = "El horario debe tener un formato válido (Ej: 10:00 a 12:00).")
    private String horario; // Changed to String

    @NotBlank(message = "El semestre es obligatorio")
    @Size(max = 50, message = "El semestre no puede tener más de 50 caracteres")
    private String semestre;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año debe ser posterior a 1900")
    @Max(value = 2100, message = "El año no puede ser superior a 2100")
    private Integer anio;
}