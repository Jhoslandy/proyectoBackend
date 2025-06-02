package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.SeDaDTO;
import com.example.ProyectoTaw.model.SeDa; // Importamos SeDa si necesitamos devolver la entidad para algún método específico (como el bloqueo)

import java.util.List;

public interface ISeDaService {

    /**
     * Obtiene una lista de todas las relaciones Materia-Curso registradas (se_da).
     * @return Lista de SeDaDTO.
     */
    List<SeDaDTO> obtenerTodasLasRelaciones();

    /**
     * Obtiene una relación Materia-Curso por su ID único.
     * @param idSeDa El ID de la relación.
     * @return SeDaDTO si se encuentra, o lanza una excepción si no.
     */
    SeDaDTO obtenerRelacionPorId(Long idSeDa);

    /**
     * Obtiene una lista de relaciones Materia-Curso para una materia específica por su código único.
     * @param materiaCodigoUnico El código único de la materia.
     * @return Lista de SeDaDTO para la materia.
     */
    List<SeDaDTO> obtenerRelacionesPorMateria(String materiaCodigoUnico);

    /**
     * Obtiene una lista de relaciones Materia-Curso para un curso específico por su ID.
     * @param cursoIdCurso El ID del curso.
     * @return Lista de SeDaDTO para el curso.
     */
    List<SeDaDTO> obtenerRelacionesPorCurso(Integer cursoIdCurso);

    /**
     * Obtiene una relación Materia-Curso específica por la combinación de su código único de materia y el ID del curso.
     * @param materiaCodigoUnico El código único de la materia.
     * @param cursoIdCurso El ID del curso.
     * @return SeDaDTO si se encuentra, o lanza una excepción si no.
     */
    SeDaDTO obtenerRelacionPorMateriaYCurso(String materiaCodigoUnico, Integer cursoIdCurso);

    /**
     * Crea un nuevo registro de relación Materia-Curso.
     * @param seDaDTO Los datos de la relación a crear.
     * @return El SeDaDTO de la relación creada.
     */
    SeDaDTO crearRelacion(SeDaDTO seDaDTO);

    /**
     * Actualiza la información de un registro de relación Materia-Curso existente.
     * En este contexto, una actualización podría significar cambiar las referencias,
     * pero lo más común es simplemente validar la existencia del ID.
     * @param idSeDa El ID de la relación a actualizar.
     * @param seDaDTO Los nuevos datos de la relación (deberían ser los mismos, solo se usa para validar unicidad si se cambian los códigos).
     * @return El SeDaDTO de la relación actualizada.
     */
    SeDaDTO actualizarRelacion(Long idSeDa, SeDaDTO seDaDTO);

    /**
     * Elimina un registro de relación Materia-Curso por su ID.
     * @param idSeDa El ID de la relación a eliminar.
     */
    void eliminarRelacion(Long idSeDa);

    /**
     * Obtiene un registro de relación Materia-Curso por su ID con un bloqueo pesimista.
     * Esto es útil para operaciones que requieren exclusividad sobre el registro.
     * @param idSeDa El ID de la relación.
     * @return La entidad SeDa con el bloqueo aplicado.
     */
    SeDa obtenerRelacionConBloqueo(Long idSeDa);
}