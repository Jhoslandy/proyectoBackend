package com.example.ProyectoTaw.service;

import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;
import com.example.ProyectoTaw.repository.EstudianteRepository;
import com.example.ProyectoTaw.service.impl.EstudianteServiceImpl;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EstudianteServiceTest {
    @Test
    void obtenerEstudiantePorId_devuelveDTO() {
        EstudianteRepository repo = mock(EstudianteRepository.class);
        EstudianteServiceImpl service = new EstudianteServiceImpl(repo);
        Estudiante estudiante = Estudiante.builder().ci("1L").nombre("Juan").build();
        when(repo.findByCi("1L")).thenReturn(Optional.of(estudiante));
        EstudianteDTO dto = service.obtenerEstudiantePorCi("1L");
        assertThat(dto.getNombre()).isEqualTo("Juan");
    }
}
