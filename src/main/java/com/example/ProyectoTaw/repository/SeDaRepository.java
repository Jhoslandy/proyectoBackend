package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.SeDa;
import com.example.ProyectoTaw.model.Curso;
import com.example.ProyectoTaw.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@Repository // Indica que esta interfaz es un componente de repositorio de Spring
public interface SeDaRepository extends JpaRepository<SeDa, Long> {
    // JpaRepository<[Clase de Entidad], [Tipo de la Clave Primaria]>
    // Aquí usamos 'Long' porque 'idSeDa' (la clave primaria) es de tipo Long en tu entidad SeDa.

    // --- Métodos de Búsqueda Personalizada (READ) ---

    /**
     * Busca todos los registros de 'se_da' para una materia específica por su código único.
     * @param materiaCodigoUnico El código único de la materia.
     * @return Lista de SeDa que corresponden a la materia.
     */
    List<SeDa> findByMateriaCodigoUnico(String materiaCodigoUnico);

    /**
     * Busca todos los registros de 'se_da' para un curso específico por su ID.
     * @param cursoIdCurso El ID del curso.
     * @return Lista de SeDa que corresponden al curso.
     */
    List<SeDa> findByCursoIdCurso(Integer cursoIdCurso);

    /**
     * Busca un registro de 'se_da' específico por la combinación de código único de materia y ID de curso.
     * Útil si quieres encontrar la relación exacta.
     * @param materiaCodigoUnico El código único de la materia.
     * @param cursoIdCurso El ID del curso.
     * @return Un Optional que contiene el registro SeDa si existe, o vacío si no.
     */
    Optional<SeDa> findByMateriaCodigoUnicoAndCursoIdCurso(String materiaCodigoUnico, Integer cursoIdCurso);

    // --- Métodos de Verificación de Existencia ---

    /**
     * Verifica si ya existe un registro de 'se_da' con la combinación dada de código único de materia y ID de curso.
     * Esto es crucial debido a la 'uniqueConstraint' en la entidad.
     * @param materiaCodigoUnico El código único de la materia.
     * @param cursoIdCurso El ID del curso.
     * @return true si la combinación existe, false en caso contrario.
     */
    Boolean existsByMateriaCodigoUnicoAndCursoIdCurso(String materiaCodigoUnico, Integer cursoIdCurso);

    // --- Métodos de Transacción y Bloqueo ---

    /**
     * Bloquea el registro de 'se_da' para escritura (bloqueo pesimista) por su ID.
     * Esto es útil en entornos de alta concurrencia para evitar problemas de datos sucios.
     * @param id El ID del registro 'se_da'.
     * @return Un Optional que contiene la entidad SeDa con el bloqueo aplicado.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SeDa> findById(Long id);

    // --- Métodos de Eliminación Personalizada (si fueran necesarios) ---

    /**
     * Elimina todos los registros de 'se_da' asociados a una materia específica.
     * @param materiaCodigoUnico El código único de la materia.
     */
    void deleteByMateriaCodigoUnico(String materiaCodigoUnico);

    /**
     * Elimina todos los registros de 'se_da' asociados a un curso específico.
     * @param cursoIdCurso El ID del curso.
     */
    void deleteByCursoIdCurso(Integer cursoIdCurso);
}