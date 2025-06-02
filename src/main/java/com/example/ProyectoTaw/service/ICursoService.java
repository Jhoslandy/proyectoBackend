package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.CursoDTO;
import com.example.ProyectoTaw.model.Curso;

import java.util.List;
// import java.time.DayOfWeek; // REMOVE THIS IMPORT

public interface ICursoService {

    List<CursoDTO> obtenerTodosLosCursos();

    CursoDTO obtenerCursoPorId(Integer idCurso);

    List<CursoDTO> buscarCursosPorSemestre(String semestre);

    List<CursoDTO> buscarCursosPorAnio(Integer anio);

    /**
     * Busca cursos por día de la semana.
     * @param dia El día de la semana a buscar (ahora como String en español).
     * @return Lista de CursoDTO que coinciden con el día.
     */
    List<CursoDTO> buscarCursosPorDia(String dia); // Changed type to String

    CursoDTO crearCurso(CursoDTO cursoDTO);

    CursoDTO actualizarCurso(Integer idCurso, CursoDTO cursoDTO);

    void eliminarCurso(Integer idCurso);

    Curso obtenerCursoConBloqueo(Integer idCurso);
}