package com.example.ProyectoTaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "registra_nota")
public class RegistraNota {

    /**
     * ID de la nota registrada.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Estudiante al que pertenece la nota.
     */
    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    @NotNull(message = "El estudiante es obligatorio")
    private Estudiante estudiante;

    /**
     * Curso al que corresponde la nota.
     */
    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    @NotNull(message = "El curso es obligatorio")
    private Curso curso;

    /**
     * Nombre de la evaluación (parcial, final, etc.).
     */
    @Column(name = "evaluacion", nullable = false, length = 50)
    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    private String evaluacion;

    /**
     * Nota obtenida en la evaluación.
     */
    @Column(name = "nota", nullable = false)
    @DecimalMin(value = "0.0", message = "La nota no puede ser menor a 0")
    @DecimalMax(value = "100.0", message = "La nota no puede ser mayor a 100")
    private Double nota;

    /**
     * Fecha en la que se registró la nota.
     */
    @Column(name = "fecha", nullable = false)
    @NotNull(message = "La fecha de la evaluación es obligatoria")
    private LocalDate fecha;
}
