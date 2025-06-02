package com.example.ProyectoTaw.model;

import jakarta.persistence.*;
import java.io.Serializable;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Integer idCurso;

    @Column(name = "dia", length = 20)
    private String dia; // Now a String

    @Column(name = "horario", length = 13) // Max 13 for "HH:mm a HH:mm"
    private String horario; // Changed to String

    @Column(name = "semestre", length = 50)
    private String semestre;

    @Column(name = "anio")
    private Integer anio;
}