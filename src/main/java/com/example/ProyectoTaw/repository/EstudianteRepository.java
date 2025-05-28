package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByNroMatricula(String nroMatricula);
    Boolean existsByCi(String ci);
    // Boolean existsByNombreUsuario(String nombreUsuario); // Eliminado: ya no hay nombre_usuario en Estudiante

    Optional<Estudiante> findByNroMatricula(String nroMatricula);
    Optional<Estudiante> findByEmail(String email);
    Optional<Estudiante> findByCi(String ci);
    // Optional<Estudiante> findByNombreUsuario(String nombreUsuario); // Eliminado: ya no hay nombre_usuario en Estudiante

    List<Estudiante> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Estudiante> findById(Long id);
}