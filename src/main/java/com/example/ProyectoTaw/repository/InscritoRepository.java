package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Inscrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.time.LocalDate; // Importa LocalDate para el manejo de fechas
import java.util.List;
import java.util.Optional;

@Repository // Indica que esta interfaz es un componente de repositorio de Spring
public interface InscritoRepository extends JpaRepository<Inscrito, Long> {
    // JpaRepository<[Clase de Entidad], [Tipo de la Clave Primaria]>
    // Aquí usamos 'Long' porque 'idInscrito' (la clave primaria) es de tipo Long en tu entidad Inscrito.

    // --- Métodos de Búsqueda Personalizada (READ) ---

    /**
     * Busca todas las inscripciones para un estudiante específico por su CI.
     * @param estudianteCi La C.I. del estudiante.
     * @return Lista de Inscrito que corresponden al estudiante.
     */
    List<Inscrito> findByEstudianteCi(String estudianteCi);

    /**
     * Busca todas las inscripciones para una materia específica por su código único.
     * @param materiaCodigoUnico El código único de la materia.
     * @return Lista de Inscrito que corresponden a la materia.
     */
    List<Inscrito> findByMateriaCodigoUnico(String materiaCodigoUnico);

    /**
     * Busca una inscripción específica por la combinación de la CI del estudiante,
     * el código único de la materia y la fecha de inscripción.
     * Útil para verificar si una inscripción EXACTA ya existe (debido a la UniqueConstraint).
     * @param estudianteCi La C.I. del estudiante.
     * @param materiaCodigoUnico El código único de la materia.
     * @param fechaInscripcion La fecha de inscripción.
     * @return Un Optional que contiene el registro Inscrito si existe, o vacío si no.
     */
    Optional<Inscrito> findByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcion(String estudianteCi, String materiaCodigoUnico, LocalDate fechaInscripcion);

    /**
     * Busca la última inscripción de un estudiante en una materia específica.
     * Es crucial para la lógica de la regla de los "6 meses" de reinscripción.
     * Ordena por fecha de inscripción de forma descendente y toma el primero.
     * @param estudianteCi La C.I. del estudiante.
     * @param materiaCodigoUnico El código único de la materia.
     * @return Un Optional que contiene la última inscripción si existe, o vacío si no.
     */
    Optional<Inscrito> findFirstByEstudianteCiAndMateriaCodigoUnicoOrderByFechaInscripcionDesc(String estudianteCi, String materiaCodigoUnico);


    // --- Métodos de Verificación de Existencia ---

    /**
     * Verifica si ya existe una inscripción con la combinación exacta de estudiante, materia y fecha.
     * Esto es útil para una validación rápida a nivel de repositorio, que se alinea con la UniqueConstraint.
     * @param estudianteCi La C.I. del estudiante.
     * @param materiaCodigoUnico El código único de la materia.
     * @param fechaInscripcion La fecha de inscripción.
     * @return true si la combinación exacta existe, false en caso contrario.
     */
    Boolean existsByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcion(String estudianteCi, String materiaCodigoUnico, LocalDate fechaInscripcion);

    // --- Métodos de Transacción y Bloqueo ---

    /**
     * Bloquea el registro de inscripción para escritura (bloqueo pesimista) por su ID.
     * Esto es útil en entornos de alta concurrencia para evitar problemas de datos sucios.
     * @param id El ID del registro de inscripción.
     * @return Un Optional que contiene la entidad Inscrito con el bloqueo aplicado.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inscrito> findById(Long id);

    // --- Métodos de Eliminación Personalizada (si fueran necesarios) ---

    /**
     * Elimina todas las inscripciones asociadas a un estudiante específico.
     * @param estudianteCi La C.I. del estudiante.
     */
    void deleteByEstudianteCi(String estudianteCi);

    /**
     * Elimina todas las inscripciones asociadas a una materia específica.
     * @param materiaCodigoUnico El código único de la materia.
     */
    void deleteByMateriaCodigoUnico(String materiaCodigoUnico);
}