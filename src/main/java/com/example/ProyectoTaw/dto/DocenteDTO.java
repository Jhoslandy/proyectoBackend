package com.example.ProyectoTaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; // Usamos LocalDate para manejar fechas sin hora

import jakarta.validation.constraints.*; // Importamos las anotaciones de validación

@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
@Builder // Permite construir instancias del objeto usando el patrón Builder
public class DocenteDTO {

    @NotBlank(message = "La C.I. es obligatoria")
    @Size(min = 5, max = 10, message = "La C.I. debe tener entre 5 y 10 caracteres")
    private String ci; // Corresponde al campo 'ci' de la entidad

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email no es válido")
    @Size(max = 100, message = "El email no puede tener más de 100 caracteres")
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate fechaNac; // Corresponde al campo 'fechaNac' de la entidad

    @NotBlank(message = "El nombre del departamento es obligatorio")
    @Size(min = 2, max = 100, message = "El departamento debe tener entre 2 y 100 caracteres")
    private String departamento;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 1, max = 4, message = "El nombre debe tener entre 1 y 5 caracteres")
    private String nroEmpleado;
}