package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.MateriaPrerequisito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MateriaPrerequisitoRepository extends JpaRepository<MateriaPrerequisito, Long> {

    List<MateriaPrerequisito> findByMateriaId(Long materiaId);

    List<MateriaPrerequisito> findByPrerequisitoId(Long prerequisitoId);

    // ✅ Este es el método que te falta:
    boolean existsByMateriaIdAndPrerequisitoId(Long materiaId, Long prerequisitoId);
}
