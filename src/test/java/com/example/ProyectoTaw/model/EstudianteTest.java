package com.example.ProyectoTaw.model;

import com.example.ProyectoTaw.model.Estudiante;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class EstudianteTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void estudianteConEmailInvalidoNoEsValido() {
        Estudiante estudiante = Estudiante.builder()
            .ci("9999999")
            .nombre("Juan")
            .apellido("Pérez")
            .email("no-es-email")
            .fechaNac(LocalDate.of(2000, 1, 1))
            .build();

        Set<ConstraintViolation<Estudiante>> violations = validator.validate(estudiante);
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("email"));
    }
}
