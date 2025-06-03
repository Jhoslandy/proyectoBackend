package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.InscritoDTO;
import com.example.ProyectoTaw.model.Inscrito; // Necesario para devolver la entidad en el método de bloqueo

import java.time.LocalDate;
import java.util.List;

public interface IInscritoService {

    /**
     * Obtiene una lista de todas las inscripciones registradas.
     * @return Lista de InscritoDTO.
     */
    List<InscritoDTO> obtenerTodasLasInscripciones();

    /**
     * Obtiene una inscripción por su ID único.
     * @param idInscrito El ID de la inscripción.
     * @return InscritoDTO si se encuentra, o lanza una excepción si no.
     */
    InscritoDTO obtenerInscripcionPorId(Long idInscrito);

    /**
     * Obtiene una lista de inscripciones para un estudiante específico por su CI.
     * @param estudianteCi La C.I. del estudiante.
     * @return Lista de InscritoDTO para el estudiante.
     */
    List<InscritoDTO> obtenerInscripcionesPorEstudiante(String estudianteCi);

    /**
     * Obtiene una lista de inscripciones para una materia específica por su código único.
     * @param materiaCodigoUnico El código único de la materia.
     * @return Lista de InscritoDTO para la materia.
     */
    List<InscritoDTO> obtenerInscripcionesPorMateria(String materiaCodigoUnico);

    /**
     * Obtiene una inscripción específica por la combinación de CI de estudiante, código único de materia y fecha de inscripción.
     * @param estudianteCi La C.I. del estudiante.
     * @param materiaCodigoUnico El código único de la materia.
     * @param fechaInscripcion La fecha de inscripción.
     * @return InscritoDTO si se encuentra, o lanza una excepción si no.
     */
    InscritoDTO obtenerInscripcionPorEstudianteMateriaYFecha(String estudianteCi, String materiaCodigoUnico, LocalDate fechaInscripcion);

    /**
     * Crea un nuevo registro de inscripción.
     * Incluye la lógica de negocio para la regla de los 6 meses.
     * @param inscritoDTO Los datos de la inscripción a crear.
     * @return El InscritoDTO de la inscripción creada.
     */
    InscritoDTO crearInscripcion(InscritoDTO inscritoDTO);

    /**
     * Actualiza la información de un registro de inscripción existente.
     * Incluye la lógica de negocio para la regla de los 6 meses si la fecha o las referencias cambian.
     * @param idInscrito El ID de la inscripción a actualizar.
     * @param inscritoDTO Los nuevos datos de la inscripción.
     * @return El InscritoDTO de la inscripción actualizada.
     */
    InscritoDTO actualizarInscripcion(Long idInscrito, InscritoDTO inscritoDTO);

    /**
     * Elimina un registro de inscripción por su ID.
     * @param idInscrito El ID de la inscripción a eliminar.
     */
    void eliminarInscripcion(Long idInscrito);

    /**
     * Obtiene un registro de inscripción por su ID con un bloqueo pesimista.
     * Esto es útil para operaciones que requieren exclusividad sobre el registro.
     * @param idInscrito El ID de la inscripción.
     * @return La entidad Inscrito con el bloqueo aplicado.
     */
    Inscrito obtenerInscripcionConBloqueo(Long idInscrito);
}