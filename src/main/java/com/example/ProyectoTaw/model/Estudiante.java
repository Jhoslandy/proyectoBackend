package com.example.ProyectoTaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa a un estudiante en el sistema universitario.
 * Solo incluye información personal y de matrícula.
 */
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
@Builder // Genera un builder para la creación de objetos
@Entity // Anotación que indica que esta clase es una entidad JPA
@Table(name = "estudiante") // Nombre de la tabla en la base de datos
public class Estudiante {
    
    /**
     * Identificador único del estudiante.
     */
    @Id // Marca el campo como la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estrategia de generación de valores para la clave primaria
    private Long id;

    /**
     * Nombre del estudiante.
     */
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    /**
     * Apellido del estudiante.
     */
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    /**
     * CI del estudiante.
     */
    @Column(name = "ci", nullable = false, unique = true, length = 15)
    private String ci;

    /**
     * Email del estudiante.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Fecha de nacimiento del estudiante.
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate fechaNacimiento;

    /**
     * Número de matrícula único del estudiante.
     */
    @Column(name = "nro_matricula", nullable = false, unique = true, length = 20)
    private String nroMatricula;

    // Nota: El campo 'usuario:id' se incluiría aquí cuando tengas la entidad Usuario.
    // Por ahora, lo excluimos como lo pediste.
}