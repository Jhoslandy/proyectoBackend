package com.example.ProyectoTaw.registro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String nombre;
    
    private String apellido;
    
    private boolean activo = true; // Por defecto true, puedes ajustar según tu lógica
    
    @ManyToMany(fetch = FetchType.EAGER) // Carga los roles de forma anticipada
    @JoinTable(
        name = "usuario_roles", // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "usuario_id"), // Columna que referencia al usuario
        inverseJoinColumns = @JoinColumn(name = "rol_id") // Columna que referencia al rol
    )
    private Set<Rol> roles = new HashSet<>();

    // Constructor específico para el registro de usuarios (sin ID y roles iniciales)
    public Usuario(String nombre, String apellido, String email, String username, String password, boolean activo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.username = username;
        this.password = password;
        this.activo = activo;
        this.roles = new HashSet<>(); // Inicializa la lista de roles vacía, se asignarán después
    }
}