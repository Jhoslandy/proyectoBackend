package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.ImparteDTO;
import com.example.ProyectoTaw.model.Imparte;

import java.util.List;

public interface IImparteService {

    // -------------------- CREAR --------------------

    /**
     * Crea una nueva relación entre una materia y un docente.
     *
     * @param imparteDTO Los datos de la relación a crear.
     * @return La relación creada como DTO.
     */
    ImparteDTO crearRelacion(ImparteDTO imparteDTO);

    // -------------------- OBTENER --------------------

    /**
     * Obtiene todas las relaciones materia-docente registradas.
     *
     * @return Lista de todas las relaciones como DTO.
     */
    List<ImparteDTO> obtenerTodasLasRelaciones();

    /**
     * Obtiene una relación específica por su ID único.
     *
     * @param idImparte ID de la relación.
     * @return DTO de la relación encontrada.
     */
    ImparteDTO obtenerRelacionPorId(Long idImparte);

    /**
     * Obtiene todas las relaciones para una materia específica.
     *
     * @param materiaCodigoUnico Código único de la materia.
     * @return Lista de relaciones como DTO.
     */
    List<ImparteDTO> obtenerRelacionesPorMateria(String materiaCodigoUnico);

    /**
     * Obtiene todas las relaciones para un docente específico.
     *
     * @param ciDocente CI del docente.
     * @return Lista de relaciones como DTO.
     */
    List<ImparteDTO> obtenerRelacionesPorDocente(String ciDocente);

    /**
     * Obtiene una relación específica por código de materia y CI del docente.
     *
     * @param materiaCodigoUnico Código único de la materia.
     * @param ciDocente CI del docente.
     * @return DTO de la relación encontrada.
     */
    ImparteDTO obtenerRelacionPorMateriaYDocente(String materiaCodigoUnico, String ciDocente);

    // -------------------- ACTUALIZAR --------------------

    /**
     * Actualiza una relación existente.
     *
     * @param idImparte ID de la relación a actualizar.
     * @param imparteDTO Nuevos datos de la relación.
     * @return DTO actualizado.
     */
    ImparteDTO actualizarRelacion(Long idImparte, ImparteDTO imparteDTO);

    // -------------------- ELIMINAR --------------------

    /**
     * Elimina una relación por su ID.
     *
     * @param idImparte ID de la relación a eliminar.
     */
    void eliminarRelacion(Long idImparte);

    // -------------------- TRANSACCIONES AVANZADAS --------------------

    /**
     * Obtiene una relación con bloqueo pesimista (para operaciones críticas concurrentes).
     *
     * @param idImparte ID de la relación.
     * @return Entidad Imparte con el bloqueo aplicado.
     */
    Imparte obtenerRelacionConBloqueo(Long idImparte);
}
