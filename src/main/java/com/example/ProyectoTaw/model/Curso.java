package com.example.ProyectoTaw.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.DayOfWeek; // Para representar el día de la semana
import java.time.LocalTime; // Para representar la hora
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generar el ID
    @Column(name = "id_curso")
    private Integer idCurso;

   @Column(name = "dia", length = 20) // Specify a reasonable length for the string
    // @Enumerated(EnumType.STRING) // REMOVE THIS LINE
    private String dia; // Changed to String

    @Column(name = "horario")
    private LocalTime horario; // Utilizar LocalTime para representar la hora

    @Column(name = "semestre", length = 50)
    private String semestre;

    @Column(name = "anio")
    private Integer anio;
}