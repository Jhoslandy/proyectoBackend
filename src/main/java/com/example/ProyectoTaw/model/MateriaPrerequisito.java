package com.example.ProyectoTaw.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "materia_prerequisito")
public class MateriaPrerequisito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "materia_id", nullable = false)
    private Long materiaId;

    @Column(name = "prerequisito_id", nullable = false)
    private Long prerequisitoId;
}
