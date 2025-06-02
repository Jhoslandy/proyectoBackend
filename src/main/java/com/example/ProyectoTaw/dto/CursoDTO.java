package com.example.ProyectoTaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*; // Importamos las anotaciones de validación
import java.time.DayOfWeek; // Para validar el día de la semana
import java.time.LocalTime; // Para validar la hora

@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
@Builder // Permite construir instancias del objeto usando el patrón Builder
public class CursoDTO {

    private Integer idCurso; // El ID suele ser generado por la base de datos, no es validado aquí

    @NotBlank(message = "El día es obligatorio") // Ensures the string is not null or empty
    @Pattern(regexp = "^(Lunes|Martes|Miércoles|Miercoles|Jueves|Viernes|Sábado|Sabado)$",
             message = "El día debe ser uno de: Lunes, Martes, Miércoles, Jueves, Viernes, Sábado.")
    private String dia; // Changed to String
    @NotNull(message = "El horario es obligatorio")
    private LocalTime horario; // Corresponde al campo 'horario' de la entidad

    @NotBlank(message = "El semestre es obligatorio")
    @Size(max = 50, message = "El semestre no puede tener más de 50 caracteres")
    private String semestre;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año debe ser posterior a 1900") // Ejemplo de validación para el año
    @Max(value = 2100, message = "El año no puede ser superior a 2100") // Ejemplo de validación para el año
    private Integer anio;
}