package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.RegistraNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistraNotaRepository extends JpaRepository<RegistraNota, Long> {
    Optional<RegistraNota> findByEstudianteCiAndCursoIdCursoAndEvaluacion(String estudianteCi, Integer cursoId, String evaluacion);
}