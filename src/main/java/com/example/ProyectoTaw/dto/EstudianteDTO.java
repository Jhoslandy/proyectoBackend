package com.example.ProyectoTaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

/**
 * DTO que representa los datos de un estudiante para transferencia entre capas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteDTO implements Serializable {

    /** CI del estudiante (clave primaria) */
    @NotNull(message = "El CI es obligatorio")
    @Min(value = 10000, message = "El CI debe tener al menos 5 dígitos")
    @Max(value = 9999999999L, message = "El CI no puede tener más de 10 dígitos")
    private Integer ci;

    /** Nombre del estudiante */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    /** Apellido del estudiante */
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 3, max = 50, message = "El apellido debe tener entre 3 y 50 caracteres")
    private String apellido;

    /** Email del estudiante */
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email no es válido")
    @Size(max = 100, message = "El email no puede tener más de 100 caracteres")
    private String email;

    /** Fecha de nacimiento del estudiante */
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate fechaNacimiento;

    /** Número de matrícula único del estudiante */
    @NotBlank(message = "El número de matrícula es obligatorio")
    @Size(min = 5, max = 20, message = "El número de matrícula debe tener entre 5 y 20 caracteres")
    private String nroMatricula;

    // Nota: El campo 'usuario:id' se incluiría aquí cuando tengas la entidad Usuario.
}
