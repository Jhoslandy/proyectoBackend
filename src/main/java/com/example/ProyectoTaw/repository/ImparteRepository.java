package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Imparte;
import com.example.ProyectoTaw.model.Docente;
import com.example.ProyectoTaw.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@Repository // Indica que esta interfaz es un componente de repositorio de Spring
public interface ImparteRepository extends JpaRepository<Imparte, Long> {
    // JpaRepository<[Clase de Entidad], [Tipo de la Clave Primaria]>
    // Aquí usamos 'Long' porque 'idImparte' (la clave primaria) es de tipo Long en tu entidad Imparte.

    // --- Métodos de Búsqueda Personalizada (READ) ---

    /**
     * Busca todos los registros de 'se_da' para una materia específica por su código único.
     * @param materiaCodigoUnico El código único de la materia.
     * @return Lista de Imparte que corresponden a la materia.
     */
    List<Imparte> findByMateriaCodigoUnico(String materiaCodigoUnico);

    /**
     * Busca todos los registros de 'se_da' para un Docente específico por su ID.
     * @param ciDocente El CI del Docente.
     * @return Lista de Imparte que corresponden al Docente.
     */
    List<Imparte> findByCiDocente(String ciDocente);

    /**
     * Busca un registro de 'se_da' específico por la combinación de código único de materia y CI de Docente.
     * Útil si quieres encontrar la relación exacta.
     * @param materiaCodigoUnico El código único de la materia.
     * @param ciDocente El ID del Docente.
     * @return Un Optional que contiene el registro Imparte si existe, o vacío si no.
     */
    Optional<Imparte> findByMateriaCodigoUnicoAndciDocente(String materiaCodigoUnico, String ciDocente);

    // --- Métodos de Verificación de Existencia ---

    /**
     * Verifica si ya existe un registro de 'se_da' con la combinación dada de código único de materia y CI de Docente.
     * Esto es crucial debido a la 'uniqueConstraint' en la entidad.
     * @param materiaCodigoUnico El código único de la materia.
     * @param ciDocente El ID del Docente.
     * @return true si la combinación existe, false en caso contrario.
     */
    Boolean existsByMateriaCodigoUnicoAndCiDocente(String materiaCodigoUnico, String ciDocente);

    // --- Métodos de Transacción y Bloqueo ---

    /**
     * Bloquea el registro de 'se_da' para escritura (bloqueo pesimista) por su ID.
     * Esto es útil en entornos de alta concurrencia para evitar problemas de datos sucios.
     * @param id El ID del registro 'se_da'.
     * @return Un Optional que contiene la entidad Imparte con el bloqueo aplicado.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Imparte> findById(Long id);

    // --- Métodos de Eliminación Personalizada (si fueran necesarios) ---

    /**
     * Elimina todos los registros de 'se_da' asociados a una materia específica.
     * @param materiaCodigoUnico El código único de la materia.
     */
    void deleteByMateriaCodigoUnico(String materiaCodigoUnico);

    /**
     * Elimina todos los registros de 'se_da' asociados a un Docente específico.
     * @param ciDocente El CI del Docente.
     */
    void deleteByCiDocente(String ciDocente);
}
