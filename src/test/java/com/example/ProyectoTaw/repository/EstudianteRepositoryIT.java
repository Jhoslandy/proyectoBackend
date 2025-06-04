package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Estudiante;
import com.example.ProyectoTaw.repository.EstudianteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteRepositoryIT {
    @Autowired
    private EstudianteRepository repo;

    @Test
    void guardarYBuscarEstudiante() {
        Estudiante estudiante = Estudiante.builder()
            .ci("9999999")
            .nombre("Ana")
            .apellido("López")
            .email("ana@uni.edu")
            .fechaNac(LocalDate.of(1999, 5, 10))
            .build();

        repo.save(estudiante);

        assertThat(repo.findById("9999999")).isPresent();
    }
}
