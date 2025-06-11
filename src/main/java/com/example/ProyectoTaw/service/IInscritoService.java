// Archivo: src/main/java/com/example/ProyectoTaw/service/IInscritoService.java

package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.InscritoDTO;
import com.example.ProyectoTaw.model.Inscrito;

import java.time.LocalDate;
import java.util.List;

public interface IInscritoService {

    List<InscritoDTO> obtenerTodasLasInscripciones();

    InscritoDTO obtenerInscripcionPorId(Long idInscrito);

    List<InscritoDTO> obtenerInscripcionesPorEstudiante(String estudianteCi);

    List<InscritoDTO> obtenerInscripcionesPorMateria(String materiaCodigoUnico);

    InscritoDTO obtenerInscripcionPorEstudianteMateriaYFecha(String estudianteCi, String materiaCodigoUnico, LocalDate fechaInscripcion);

    InscritoDTO crearInscripcion(InscritoDTO inscritoDTO);

    InscritoDTO actualizarInscripcion(Long idInscrito, InscritoDTO inscritoDTO);

    void eliminarInscripcion(Long idInscrito);

    Inscrito obtenerInscripcionConBloqueo(Long idInscrito);

    // *******************************************************************
    // ****** AGREGAR ESTE NUEVO MÉTODO EN IInscritoService.java ******
    // *******************************************************************
    void eliminarInscripcionPorEstudianteYMateria(String estudianteCi, String materiaCodigoUnico);
}