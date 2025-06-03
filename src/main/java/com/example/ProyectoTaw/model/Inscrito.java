package com.example.ProyectoTaw.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inscrito", uniqueConstraints = {
    // Restricción: un estudiante no puede inscribirse en la misma materia en la misma fecha DOS VECES.
    @UniqueConstraint(columnNames = {"estudiante_ci", "materia_codigo_unico", "fecha_inscripcion"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscrito implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscrito")
    private Long idInscrito;

    @ManyToOne
    @JoinColumn(name = "estudiante_ci", referencedColumnName = "ci", nullable = false)
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "materia_codigo_unico", referencedColumnName = "codigo_unico", nullable = false)
    private Materia materia;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

}