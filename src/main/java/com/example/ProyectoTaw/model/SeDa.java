package com.example.ProyectoTaw.model;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "se_da", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"materia_codigo_unico", "curso_id_curso"}) // Añadir restricción de unicidad aquí
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeDa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID autoincrementable
    @Column(name = "id_se_da") // Nombre de columna para el nuevo ID
    private Long idSeDa; // Usamos Long para IDs autoincrementables

    @ManyToOne // Relación con Materia
    // La relación se basa en el 'codigoUnico' de Materia, no en su 'id' autoincrementable.
    @JoinColumn(name = "materia_codigo_unico", referencedColumnName = "codigo_unico", nullable = false)
    private Materia materia;

    @ManyToOne // Relación con Curso
    @JoinColumn(name = "curso_id_curso", referencedColumnName = "id_curso", nullable = false)
    private Curso curso;

    // No hay atributos adicionales en la relación 'se_da' según el diagrama ER.
}