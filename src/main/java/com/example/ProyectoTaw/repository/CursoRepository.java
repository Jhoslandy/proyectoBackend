package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;
// import java.time.DayOfWeek; // REMOVE THIS IMPORT
import java.time.LocalTime;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {

    // Verifies if a course exists with the given string day and horario
    Boolean existsByDiaAndHorario(String dia, LocalTime horario); // Changed type to String

    // Finds a course by the given string day and horario
    Optional<Curso> findByDiaAndHorario(String dia, LocalTime horario); // Changed type to String

    // Busca cursos por semestre.
    List<Curso> findBySemestre(String semestre);

    // Busca cursos por año.
    List<Curso> findByAnio(Integer anio);

    // Busca cursos por día (case-insensitive for String)
    List<Curso> findByDiaIgnoreCase(String dia); // Added for flexible search

    // Busca cursos por semestre y año.
    List<Curso> findBySemestreAndAnio(String semestre, Integer anio);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Curso> findById(Integer idCurso);
}