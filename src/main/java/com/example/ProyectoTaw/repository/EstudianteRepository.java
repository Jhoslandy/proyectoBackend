package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;

@Repository // Indica que esta interfaz es un componente de repositorio de Spring
public interface EstudianteRepository extends JpaRepository<Estudiante, String> { 
    // JpaRepository<[Clase de Entidad], [Tipo de la Clave Primaria]>
    // Aquí usamos 'String' porque 'ci' (la clave primaria) es de tipo String en tu entidad Estudiante.

    // --- Métodos de Verificación de Existencia ---
    // Verifica si ya existe un estudiante con el email dado.
    Boolean existsByEmail(String email);
    
    // Verifica si ya existe un estudiante con la CI dada.
    // Ajustado a 'String' para coincidir con el tipo de 'ci' en la entidad.
    Boolean existsByCi(String ci); 
    
    // Busca un estudiante por su email.
    Optional<Estudiante> findByEmail(String email);
    
    // Busca un estudiante por su CI.
    // Ajustado a 'String' para coincidir con el tipo de 'ci' en la entidad.
    Optional<Estudiante> findByCi(String ci); 

    // --- Métodos de Búsqueda Personalizada (READ) ---
    // Busca estudiantes cuyo nombre o apellido contengan la cadena dada (ignorando mayúsculas/minúsculas).
    List<Estudiante> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    // --- Métodos de Transacción y Bloqueo ---
    // Bloquea el registro del estudiante para escritura (bloqueo pesimista).
    // Esto es útil en entornos de alta concurrencia para evitar problemas de datos sucios.
    // Aquí el método original findById(Integer ci) se ajusta a findById(String ci)
    // para usar la CI como clave primaria de tipo String.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Estudiante> findById(String ci); 
}