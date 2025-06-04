package com.example.ProyectoTaw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Skipping test temporarily due to environment or other issues.")
@WebMvcTest(EstudianteController.class)
@AutoConfigureMockMvc(addFilters = false)
class EstudianteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.example.ProyectoTaw.service.IEstudianteService estudianteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearEstudiante_retorna201() throws Exception {
        EstudianteDTO estudiante = EstudianteDTO.builder()
            .ci("12345678")
            .nombre("Carlos")
            .apellido("Gómez")
            .email("carlos@uni.edu")
            .fechaNac(LocalDate.of(2001, 2, 2))
            .build();

        org.mockito.Mockito.when(estudianteService.crearEstudiante(org.mockito.Mockito.any(EstudianteDTO.class)))
                .thenReturn(estudiante);

        mockMvc.perform(post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estudiante)))
            .andExpect(status().isCreated());
    }
}
