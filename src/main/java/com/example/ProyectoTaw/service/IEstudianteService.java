package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;

import java.util.List;

public interface IEstudianteService {

    List<EstudianteDTO> obtenerTodosLosEstudiantes();

    EstudianteDTO obtenerEstudiantePorNroMatricula(String nroMatricula);

    List<EstudianteDTO> buscarEstudiantes(String query);

    EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO);

    EstudianteDTO actualizarEstudiante(Integer ci, EstudianteDTO estudianteDTO);

    void eliminarEstudiante(Integer ci);

    Estudiante obtenerEstudianteConBloqueo(Integer ci);
}
