package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {
    
    Boolean existsByEmail(String email);
    Boolean existsByNroMatricula(String nroMatricula);
    Boolean existsByCi(Integer ci); // Cambiado de String a Integer

    Optional<Estudiante> findByNroMatricula(String nroMatricula);
    Optional<Estudiante> findByEmail(String email);
    Optional<Estudiante> findByCi(Integer ci); // Cambiado de String a Integer

    List<Estudiante> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Estudiante> findById(Integer ci); // Usamos 'ci' como clave primaria
}
