package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.MateriaPrerequisitoDTO;

import java.util.List;

public interface IMateriaPrerequisitoService {

    MateriaPrerequisitoDTO crearRelacion(MateriaPrerequisitoDTO dto);

    List<MateriaPrerequisitoDTO> listarPorMateriaId(Long materiaId);

    List<MateriaPrerequisitoDTO> listarPorPrerequisitoId(Long prerequisitoId);

    void eliminarRelacion(Long id);

    MateriaPrerequisitoDTO actualizarRelacion(Long id, MateriaPrerequisitoDTO dto);

}
