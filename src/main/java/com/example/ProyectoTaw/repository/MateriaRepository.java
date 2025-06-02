package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.List;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    Optional<Materia> findByCodigoUnico(String codigoUnico);

    Optional<Materia> findByNombre(String nombre);
}