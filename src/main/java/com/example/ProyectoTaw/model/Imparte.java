package com.example.ProyectoTaw.model;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imparte", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"materia_codigo_unico", "ci_docente"}) // Añadir restricción de unicidad aquí
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Imparte implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID autoincrementable
    @Column(name = "id_imparte") // Nombre de columna para el nuevo ID
    private Long idImparte; // Usamos Long para IDs autoincrementables

    @ManyToOne // Relación con Materia
    // La relación se basa en el 'codigoUnico' de Materia, no en su 'id' autoincrementable.
    @JoinColumn(name = "materia_codigo_unico", referencedColumnName = "codigo_unico", nullable = false)
    private Materia materia;

    @ManyToOne // Relación con Docente
    @JoinColumn(name = "docente_ci_docente", referencedColumnName = "ci_docente", nullable = false)
    private Docente Docente;

    // No hay atributos adicionales en la relación 'imparte' según el diagrama ER.
}