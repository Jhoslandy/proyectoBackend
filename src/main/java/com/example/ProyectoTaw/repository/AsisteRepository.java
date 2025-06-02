package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Asiste;
// import com.example.ProyectoTaw.model.AsisteId; // ¡Esta línea debe ser ELIMINADA!
import com.example.ProyectoTaw.model.Curso;
import com.example.ProyectoTaw.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository // Indica que esta interfaz es un componente de repositorio de Spring
public interface AsisteRepository extends JpaRepository<Asiste, Long> { // CAMBIO AQUÍ: Ahora es Long

    // --- Métodos de Búsqueda Personalizada (READ) ---

    // Buscar todas las asistencias de un estudiante específico por su CI
    List<Asiste> findByEstudianteCi(String estudianteCi);

    // Buscar todas las asistencias para un curso específico por su ID
    List<Asiste> findByCursoIdCurso(Integer cursoIdCurso);

    // Buscar asistencias por estudiante y fecha
    List<Asiste> findByEstudianteCiAndFecha(String estudianteCi, LocalDate fecha);

    // Buscar asistencias por curso y fecha
    List<Asiste> findByCursoIdCursoAndFecha(Integer cursoIdCurso, LocalDate fecha);

    // Buscar asistencias de un estudiante para un curso específico
    List<Asiste> findByEstudianteCiAndCursoIdCurso(String estudianteCi, Integer cursoIdCurso);

    // Buscar asistencias por estudiante, curso y estado de presencia
    List<Asiste> findByEstudianteCiAndCursoIdCursoAndPresente(String estudianteCi, Integer cursoIdCurso, Boolean presente);

    // Buscar asistencias por fecha
    List<Asiste> findByFecha(LocalDate fecha);

    // Buscar asistencias por estado de presencia
    List<Asiste> findByPresente(Boolean presente);

    // --- Métodos de Verificación de Existencia ---

    // Verificar si ya existe un registro de asistencia para un estudiante, curso y fecha específicos
    // (Útil si quieres que esta combinación sea única, aunque el ID ahora sea autoincrementable)
    Boolean existsByEstudianteCiAndCursoIdCursoAndFecha(String estudianteCi, Integer cursoIdCurso, LocalDate fecha);

    // --- Métodos de Transacción y Bloqueo ---
    // El método findById ya usa el nuevo tipo de ID (Long) automáticamente.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Asiste> findById(Long id); // CAMBIO AQUÍ: Ahora el parámetro es Long
    
    Optional<Asiste> findByEstudianteCiAndCursoIdCursoAndFecha(String estudianteCi, Integer cursoIdCurso, LocalDate fecha);

    // Eliminar asistencias por estudiante y curso (útil para borrar todas las asistencias de un estudiante en un curso)
    void deleteByEstudianteCiAndCursoIdCurso(String estudianteCi, Integer cursoIdCurso);

    // Eliminar asistencias por estudiante
    void deleteByEstudianteCi(String estudianteCi);

    // Eliminar asistencias por curso
    void deleteByCursoIdCurso(Integer cursoIdCurso);
}