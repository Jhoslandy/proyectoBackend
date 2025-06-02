package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;

@Repository // Indica que esta interfaz es un componente de repositorio de Spring

public interface DocenteRepository extends JpaRepository<Docente, String> { 
    // JpaRepository<[Clase de Entidad], [Tipo de la Clave Primaria]>
    // Aquí usamos 'String' porque 'ci' (la clave primaria) es de tipo String en tu entidad Docente.

    // --- Métodos de Verificación de Existencia ---
    // Verifica si ya existe un Docente con el email dado.
    Boolean existsByEmail(String email);
    
    // Verifica si ya existe un docente con la CI dada.
    // Ajustado a 'String' para coincidir con el tipo de 'ci' en la entidad.
    Boolean existsByCi(String ci); 
    
    // Busca un docente por su email.
    Optional<Docente> findByEmail(String email);
    
    // Busca un docente por su CI.
    // Ajustado a 'String' para coincidir con el tipo de 'ci' en la entidad.
    Optional<Docente> findByCi(String ci); 

    // --- Métodos de Búsqueda Personalizada (READ) ---
    // Busca docente cuyo nombre o apellido contengan la cadena dada (ignorando mayúsculas/minúsculas).
    List<Docente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    // --- Métodos de Transacción y Bloqueo ---
    // Bloquea el registro del Docente para escritura (bloqueo pesimista).
    // Esto es útil en entornos de alta concurrencia para evitar problemas de datos sucios.
    // Aquí el método original findById(Integer ci) se ajusta a findById(String ci)
    // para usar la CI como clave primaria de tipo String.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Docente> findById(String ci); 
}