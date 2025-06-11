// Archivo: src/main/java/com/example/ProyectoTaw/repository/InscritoRepository.java

package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Inscrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscritoRepository extends JpaRepository<Inscrito, Long> {

    List<Inscrito> findByEstudianteCi(String estudianteCi);

    List<Inscrito> findByMateriaCodigoUnico(String materiaCodigoUnico);

    Optional<Inscrito> findByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcion(String estudianteCi, String materiaCodigoUnico, LocalDate fechaInscripcion);

    // *******************************************************************
    // ****** AGREGAR ESTE NUEVO MÉTODO EN InscritoRepository.java ******
    // *******************************************************************
    // Este método permite a Spring Data JPA buscar una inscripción
    // por el CI del estudiante (atravesando la relación 'estudiante')
    // y por el código único de la materia (atravesando la relación 'materia').
    Optional<Inscrito> findByEstudianteCiAndMateriaCodigoUnico(String estudianteCi, String materiaCodigoUnico);

    // Si tuvieras una regla de 6 meses que verificar, por ejemplo:
    // Boolean existsByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcionAfter(String estudianteCi, String materiaCodigoUnico, LocalDate date);
}