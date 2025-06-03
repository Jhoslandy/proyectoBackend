package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Imparte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImparteRepository extends JpaRepository<Imparte, Long> {

    // Búsqueda por materia
    List<Imparte> findByMateriaCodigoUnico(String materiaCodigoUnico);

    // Búsqueda por docente
    List<Imparte> findByDocenteCiDocente(String ciDocente);

    // Búsqueda por combinación
    Optional<Imparte> findByMateriaCodigoUnicoAndDocenteCiDocente(String materiaCodigoUnico, String ciDocente);

    // Verificación de existencia de una relación específica
    boolean existsByMateriaCodigoUnicoAndDocenteCiDocente(String materiaCodigoUnico, String ciDocente);

    // Bloqueo pesimista por ID
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Imparte> findById(Long id); // Usado para bloqueo en `obtenerRelacionConBloqueo`

    // Eliminación por materia
    void deleteByMateriaCodigoUnico(String materiaCodigoUnico);

    // Eliminación por docente
    void deleteByDocenteCiDocente(String ciDocente);
}
