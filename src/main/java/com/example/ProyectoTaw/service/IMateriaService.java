package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.MateriaDTO;
import com.example.ProyectoTaw.model.Materia;

import java.util.List;

public interface IMateriaService {
    MateriaDTO crearMateria(MateriaDTO materiaDTO);

    MateriaDTO obtenerMateriaPorId(Long id);

    List<MateriaDTO> listarMaterias();

    MateriaDTO actualizarMateria(Long id, MateriaDTO materiaDTO);

    void eliminarMateria(Long id);
}
