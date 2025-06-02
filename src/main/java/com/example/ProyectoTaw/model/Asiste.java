package com.example.ProyectoTaw.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asiste")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asiste implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID autoincrementable
    @Column(name = "id_asiste")
    private Long idAsiste; // Usamos Long para IDs autoincrementables

    @ManyToOne // Relación con Estudiante
    @JoinColumn(name = "estudiante_ci", referencedColumnName = "ci", nullable = false)
    private Estudiante estudiante;

    @ManyToOne // Relación con Curso
    @JoinColumn(name = "curso_id_curso", referencedColumnName = "id_curso", nullable = false)
    private Curso curso;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "presente", nullable = false)
    private Boolean presente; // Asumiendo 'presente' es un valor booleano

    // Opcional: Agregar una restricción de unicidad si la combinación estudiante_ci, curso_id_curso, fecha
    // debe ser única a nivel de base de datos.
    /*
    @Table(name = "asiste", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"estudiante_ci", "curso_id_curso", "fecha"})
    })
    */
}