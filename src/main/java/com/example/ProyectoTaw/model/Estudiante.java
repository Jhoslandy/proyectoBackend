package com.example.ProyectoTaw.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor; // Importa la anotación @AllArgsConstructor de Lombok
import lombok.Builder; // Importa la anotación @Builder de Lombok
import lombok.Data; 
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "estudiante") 
@Data 
@NoArgsConstructor 
@AllArgsConstructor // Genera un constructor con todos los argumentos
@Builder // Permite construir instancias del objeto usando el patrón Builder
public class Estudiante implements Serializable {

    private static final long serialVersionUID = 1L; 

    @Id 
    @Column(name = "ci", length = 20) 
    private String ci; 

    @Column(name = "nombre", length = 100) 
    private String nombre;

    @Column(name = "apellido", length = 100)
    private String apellido;

    @Email
    @NotBlank
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "fecha_nac") 
    @Temporal(TemporalType.DATE) 
    @Basic(optional = false)
    private LocalDate fechaNac; 
}