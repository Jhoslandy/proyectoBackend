package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.AsisteDTO;
import com.example.ProyectoTaw.model.Asiste; // Importamos Asiste si necesitamos devolver la entidad para algún método específico (como el bloqueo)

import java.time.LocalDate;
import java.util.List;

public interface IAsisteService {

    /**
     * Obtiene una lista de todas las asistencias registradas.
     * @return Lista de AsisteDTO.
     */
    List<AsisteDTO> obtenerTodasLasAsistencias();

    /**
     * Obtiene una asistencia por su ID único.
     * @param idAsiste El ID de la asistencia.
     * @return AsisteDTO si se encuentra, o lanza una excepción si no.
     */
    AsisteDTO obtenerAsistenciaPorId(Long idAsiste);

    /**
     * Obtiene una lista de asistencias para un estudiante específico por su CI.
     * @param estudianteCi La CI del estudiante.
     * @return Lista de AsisteDTO para el estudiante.
     */
    List<AsisteDTO> obtenerAsistenciasPorEstudiante(String estudianteCi);

    /**
     * Obtiene una lista de asistencias para un curso específico por su ID.
     * @param cursoIdCurso El ID del curso.
     * @return Lista de AsisteDTO para el curso.
     */
    List<AsisteDTO> obtenerAsistenciasPorCurso(Integer cursoIdCurso);

    /**
     * Obtiene una lista de asistencias para un estudiante en un curso específico.
     * @param estudianteCi La CI del estudiante.
     * @param cursoIdCurso El ID del curso.
     * @return Lista de AsisteDTO para el estudiante en ese curso.
     */
    List<AsisteDTO> obtenerAsistenciasDeEstudianteEnCurso(String estudianteCi, Integer cursoIdCurso);

    /**
     * Crea un nuevo registro de asistencia.
     * @param asisteDTO Los datos de la asistencia a crear.
     * @return El AsisteDTO de la asistencia creada.
     */
    AsisteDTO crearAsistencia(AsisteDTO asisteDTO);

    /**
     * Actualiza la información de un registro de asistencia existente.
     * @param idAsiste El ID de la asistencia a actualizar.
     * @param asisteDTO Los nuevos datos de la asistencia.
     * @return El AsisteDTO de la asistencia actualizada.
     */
    AsisteDTO actualizarAsistencia(Long idAsiste, AsisteDTO asisteDTO);

    /**
     * Elimina un registro de asistencia por su ID.
     * @param idAsiste El ID de la asistencia a eliminar.
     */
    void eliminarAsistencia(Long idAsiste);

    /**
     * Obtiene un registro de asistencia por su ID con un bloqueo pesimista.
     * Esto es útil para operaciones que requieren exclusividad sobre el registro.
     * @param idAsiste El ID de la asistencia.
     * @return La entidad Asiste con el bloqueo aplicado.
     */
    Asiste obtenerAsistenciaConBloqueo(Long idAsiste);
}