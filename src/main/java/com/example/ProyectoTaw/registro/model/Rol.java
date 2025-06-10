package com.example.ProyectoTaw.registro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true) // <-- AÑADIDO: unique = true
    private NombreRol nombre;
    
    public enum NombreRol {
        ROL_ESTUDIANTE,
        ROL_DOCENTE,
        ROL_ADMIN
    }
}