package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.DocenteDTO;
import com.example.ProyectoTaw.model.Docente;

import java.util.List;

public interface IDocenteService {

    /**
     * Obtiene una lista de todos los Docentes disponibles.
     * @return Lista de DocenteDTO.
     */
    List<DocenteDTO> obtenerTodosLosDocentes();

    /**
     * Obtiene un Docente por su Carnet de Identidad (CI).
     * @param ci El Carnet de Identidad del Docente.
     * @return DocenteDTO si se encuentra, o lanza una excepción si no.
     */
    DocenteDTO obtenerDocentePorCi(String ci); // Cambiado de nroMatricula a ci y tipo a String

    /**
     * Busca Docentes por una cadena de consulta en su nombre o apellido.
     * @param query La cadena de texto para buscar.
     * @return Lista de DocenteDTO que coinciden con la búsqueda.
     */
    List<DocenteDTO> buscarDocentes(String query);

    /**
     * Crea un nuevo Docente en el sistema.
     * @param docenteDTO Los datos del Docente a crear.
     * @return El DocenteDTO del Docente creado.
     */
    DocenteDTO crearDocente(DocenteDTO docenteDTO);

    /**
     * Actualiza la información de un Docente existente.
     * @param ci El Carnet de Identidad (CI) del Docente a actualizar.
     * @param docenteDTO Los nuevos datos del Docente.
     * @return El DocenteDTO del Docente actualizado.
     */
    DocenteDTO actualizarDocente(String ci, DocenteDTO docenteDTO); // Tipo cambiado a String

    /**
     * Elimina un Docente del sistema por su Carnet de Identidad (CI).
     * @param ci El Carnet de Identidad (CI) del Docente a eliminar.
     */
    void eliminarDocente(String ci); // Tipo cambiado a String

    /**
     * Obtiene un Docente por su Carnet de Identidad (CI) con un bloqueo pesimista.
     * Esto es útil para operaciones que requieren exclusividad sobre el registro.
     * @param ci El Carnet de Identidad (CI) del Docente.
     * @return La entidad Docente con el bloqueo aplicado.
     */
    Docente obtenerDocenteConBloqueo(String ci); // Tipo cambiado a String
}