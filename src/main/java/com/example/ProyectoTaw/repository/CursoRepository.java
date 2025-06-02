package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {

    // Verifies if a course exists with the given string day and string horario
    Boolean existsByDiaAndHorario(String dia, String horario); // Changed type to String

    // Finds a course by the given string day and string horario
    Optional<Curso> findByDiaAndHorario(String dia, String horario); // Changed type to String

    List<Curso> findBySemestre(String semestre);

    List<Curso> findByAnio(Integer anio);

    List<Curso> findByDiaIgnoreCase(String dia);

    List<Curso> findBySemestreAndAnio(String semestre, Integer anio);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Curso> findById(Integer idCurso);
}