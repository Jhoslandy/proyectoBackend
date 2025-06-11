// Archivo: src/main/java/com/example/ProyectoTaw/service/IMateriaService.java

package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.MateriaDTO;
import com.example.ProyectoTaw.model.Materia; // Importar si es necesario para métodos de bloqueo

import java.util.List;

public interface IMateriaService {

    List<MateriaDTO> obtenerTodasLasMaterias();

    MateriaDTO obtenerMateriaPorId(Long id);

    List<MateriaDTO> buscarMaterias(String query);

    MateriaDTO crearMateria(MateriaDTO materiaDTO);

    MateriaDTO actualizarMateria(Long id, MateriaDTO materiaDTO);

    void eliminarMateria(Long id);

    Materia obtenerMateriaConBloqueo(Long id);

    // *******************************************************************
    // ****** AGREGAR/VERIFICAR ESTE MÉTODO EN IMateriaService.java ******
    // *******************************************************************
    MateriaDTO obtenerMateriaPorCodigoUnico(String codigoUnico);
}