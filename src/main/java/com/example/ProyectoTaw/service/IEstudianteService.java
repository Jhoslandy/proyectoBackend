package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;
import java.util.List;

public interface IEstudianteService {
    
    List<EstudianteDTO> obtenerTodosLosEstudiantes();

    EstudianteDTO obtenerEstudiantePorNroMatricula(String nroMatricula);

    List<EstudianteDTO> buscarEstudiantes(String query);

    EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO);
    
    EstudianteDTO actualizarEstudiante(Long id, EstudianteDTO estudianteDTO);

    void eliminarEstudiante(Long id);

    Estudiante obtenerEstudianteConBloqueo(Long id);
}