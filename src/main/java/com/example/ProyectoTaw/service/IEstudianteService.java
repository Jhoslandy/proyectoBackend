package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;

import java.util.List;

public interface IEstudianteService {

    /**
     * Obtiene una lista de todos los estudiantes disponibles.
     * @return Lista de EstudianteDTO.
     */
    List<EstudianteDTO> obtenerTodosLosEstudiantes();

    /**
     * Obtiene un estudiante por su Carnet de Identidad (CI).
     * @param ci El Carnet de Identidad del estudiante.
     * @return EstudianteDTO si se encuentra, o lanza una excepción si no.
     */
    EstudianteDTO obtenerEstudiantePorCi(String ci); // Cambiado de nroMatricula a ci y tipo a String

    /**
     * Busca estudiantes por una cadena de consulta en su nombre o apellido.
     * @param query La cadena de texto para buscar.
     * @return Lista de EstudianteDTO que coinciden con la búsqueda.
     */
    List<EstudianteDTO> buscarEstudiantes(String query);

    /**
     * Crea un nuevo estudiante en el sistema.
     * @param estudianteDTO Los datos del estudiante a crear.
     * @return El EstudianteDTO del estudiante creado.
     */
    EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO);

    /**
     * Actualiza la información de un estudiante existente.
     * @param ci El Carnet de Identidad (CI) del estudiante a actualizar.
     * @param estudianteDTO Los nuevos datos del estudiante.
     * @return El EstudianteDTO del estudiante actualizado.
     */
    EstudianteDTO actualizarEstudiante(String ci, EstudianteDTO estudianteDTO); // Tipo cambiado a String

    /**
     * Elimina un estudiante del sistema por su Carnet de Identidad (CI).
     * @param ci El Carnet de Identidad (CI) del estudiante a eliminar.
     */
    void eliminarEstudiante(String ci); // Tipo cambiado a String

    /**
     * Obtiene un estudiante por su Carnet de Identidad (CI) con un bloqueo pesimista.
     * Esto es útil para operaciones que requieren exclusividad sobre el registro.
     * @param ci El Carnet de Identidad (CI) del estudiante.
     * @return La entidad Estudiante con el bloqueo aplicado.
     */
    Estudiante obtenerEstudianteConBloqueo(String ci); // Tipo cambiado a String
}