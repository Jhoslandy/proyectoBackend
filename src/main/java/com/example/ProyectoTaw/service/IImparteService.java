package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.ImparteDTO;
import com.example.ProyectoTaw.model.Imparte; // Importamos Imparte si necesitamos devolver la entidad para algún método específico (como el bloqueo)

import java.util.List;

public interface IImparteService {

    /**
     * Obtiene una lista de todas las relaciones Materia-Docente registradas (se_da).
     * @return Lista de ImparteDTO.
     */
    List<ImparteDTO> obtenerTodasLasRelaciones();

    /**
     * Obtiene una relación Materia-Docente por su ID único.
     * @param idImparte El ID de la relación.
     * @return ImparteDTO si se encuentra, o lanza una excepción si no.
     */
    ImparteDTO obtenerRelacionPorId(Long idImparte);

    /**
     * Obtiene una lista de relaciones Materia-Docente para una materia específica por su código único.
     * @param materiaCodigoUnico El código único de la materia.
     * @return Lista de ImparteDTO para la materia.
     */
    List<ImparteDTO> obtenerRelacionesPorMateria(String materiaCodigoUnico);

    /**
     * Obtiene una lista de relaciones Materia-Docente para un Docente específico por su ID.
     * @param ciDocente El ID del Docente.
     * @return Lista de ImparteDTO para el Docente.
     */
    List<ImparteDTO> obtenerRelacionesPorDocente(String ciDocente);

    /**
     * Obtiene una relación Materia-Docente específica por la combinación de su código único de materia y el ID del Docente.
     * @param materiaCodigoUnico El código único de la materia.
     * @param ciDocente El ID del Docente.
     * @return ImparteDTO si se encuentra, o lanza una excepción si no.
     */
    ImparteDTO obtenerRelacionPorMateriaYDocente(String materiaCodigoUnico, String ciDocente);

    /**
     * Crea un nuevo registro de relación Materia-Docente.
     * @param imparteDTO Los datos de la relación a crear.
     * @return El ImparteDTO de la relación creada.
     */
    ImparteDTO crearRelacion(ImparteDTO imparteDTO);

    /**
     * Actualiza la información de un registro de relación Materia-Docente existente.
     * En este contexto, una actualización podría significar cambiar las referencias,
     * pero lo más común es simplemente validar la existencia del ID.
     * @param idImparte El ID de la relación a actualizar.
     * @param imparteDTO Los nuevos datos de la relación (deberían ser los mismos, solo se usa para validar unicidad si se cambian los códigos).
     * @return El ImparteDTO de la relación actualizada.
     */
    ImparteDTO actualizarRelacion(Long idImparte, ImparteDTO imparteDTO);

    /**
     * Elimina un registro de relación Materia-Docente por su ID.
     * @param idImparte El ID de la relación a eliminar.
     */
    void eliminarRelacion(Long idImparte);

    /**
     * Obtiene un registro de relación Materia-Docente por su ID con un bloqueo pesimista.
     * Esto es útil para operaciones que requieren exclusividad sobre el registro.
     * @param idImparte El ID de la relación.
     * @return La entidad Imparte con el bloqueo aplicado.
     */
    Imparte obtenerRelacionConBloqueo(Long idImparte);
}