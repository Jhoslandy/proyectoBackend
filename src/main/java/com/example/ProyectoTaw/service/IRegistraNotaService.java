
package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.RegistraNotaDTO;
import com.example.ProyectoTaw.model.RegistraNota;

import java.util.List;

public interface IRegistraNotaService {
    RegistraNotaDTO crearNota(RegistraNotaDTO dto);
    RegistraNotaDTO obtenerNotaPorId(Long id);
    List<RegistraNotaDTO> listarNotas();
    RegistraNotaDTO actualizarNota(Long id, RegistraNotaDTO dto);
    void eliminarNota(Long id);
}