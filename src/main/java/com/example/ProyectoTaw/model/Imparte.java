package com.example.ProyectoTaw.model;

import jakarta.persistence.*;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imparte", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"materia_codigo_unico", "docente_ci_docente"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Imparte implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imparte")
    private Long idImparte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_codigo_unico", referencedColumnName = "codigo_unico", nullable = false)
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_ci_docente", referencedColumnName = "ci_docente", nullable = false)
    private Docente docente;
}
