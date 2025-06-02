package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.CursoDTO;
import com.example.ProyectoTaw.model.Curso;

import java.util.List;

public interface ICursoService {

    List<CursoDTO> obtenerTodosLosCursos();

    CursoDTO obtenerCursoPorId(Integer idCurso);

    List<CursoDTO> buscarCursosPorSemestre(String semestre);

    List<CursoDTO> buscarCursosPorAnio(Integer anio);

    List<CursoDTO> buscarCursosPorDia(String dia);

    CursoDTO crearCurso(CursoDTO cursoDTO);

    CursoDTO actualizarCurso(Integer idCurso, CursoDTO cursoDTO);

    void eliminarCurso(Integer idCurso);

    Curso obtenerCursoConBloqueo(Integer idCurso);
}