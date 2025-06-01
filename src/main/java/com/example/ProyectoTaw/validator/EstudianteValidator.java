package com.example.ProyectoTaw.validator;

import org.springframework.stereotype.Component;
import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;
import com.example.ProyectoTaw.repository.EstudianteRepository;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class EstudianteValidator {

    private final EstudianteRepository estudianteRepository;

    public EstudianteValidator(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    public void validaEmailUnico(String email, Integer ciActual) {
        Optional<Estudiante> existente = estudianteRepository.findByEmail(email);
        if (existente.isPresent() && !existente.get().getCi().equals(ciActual)) {
            throw new BusinessException("Ya existe un estudiante con este email.");
        }
    }

    public void validaNroMatriculaUnico(String nroMatricula, Integer ciActual) {
        Optional<Estudiante> existente = estudianteRepository.findByNroMatricula(nroMatricula);
        if (existente.isPresent() && !existente.get().getCi().equals(ciActual)) {
            throw new BusinessException("Ya existe un estudiante con este número de matrícula.");
        }
    }

    public void validaCiUnico(Integer ci, Integer ciActual) {
        Optional<Estudiante> existente = estudianteRepository.findByCi(ci);
        if (existente.isPresent() && !existente.get().getCi().equals(ciActual)) {
            throw new BusinessException("Ya existe un estudiante con este CI.");
        }
    }

    public void validaDominioEmail(String email) {
        String dominio = email.substring(email.indexOf('@') + 1);
        List<String> dominiosBloqueados = Arrays.asList("dominiobloqueado.com", "spam.com");

        if (dominiosBloqueados.contains(dominio)) {
            throw new BusinessException("El dominio de email no está permitido.");
        }
    }

    public void validaNombreEstudiante(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessException("El nombre no puede estar vacío o nulo.");
        }
    }

    public void validaApellidoEstudiante(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new BusinessException("El apellido es obligatorio y no puede estar vacío.");
        }
    }

    public void validacionCompletaEstudiante(EstudianteDTO estudiante) {
        validaEmailUnico(estudiante.getEmail(), estudiante.getCi());
        validaNroMatriculaUnico(estudiante.getNroMatricula(), estudiante.getCi());
        validaCiUnico(estudiante.getCi(), estudiante.getCi()); // Verifica que no se repita
        validaDominioEmail(estudiante.getEmail());
        validaNombreEstudiante(estudiante.getNombre());
        validaApellidoEstudiante(estudiante.getApellido());
    }

    public void validarActualizacionEstudiante(EstudianteDTO estudianteDTO, Estudiante estudianteExistente) {
        if (!estudianteExistente.getEmail().equalsIgnoreCase(estudianteDTO.getEmail())) {
            validaEmailUnico(estudianteDTO.getEmail(), estudianteExistente.getCi());
        }
        if (!estudianteExistente.getNroMatricula().equalsIgnoreCase(estudianteDTO.getNroMatricula())) {
            validaNroMatriculaUnico(estudianteDTO.getNroMatricula(), estudianteExistente.getCi());
        }
        if (!estudianteExistente.getCi().equals(estudianteDTO.getCi())) {
            validaCiUnico(estudianteDTO.getCi(), estudianteExistente.getCi());
        }
        validaDominioEmail(estudianteDTO.getEmail());
        validaNombreEstudiante(estudianteDTO.getNombre());
        validaApellidoEstudiante(estudianteDTO.getApellido());
    }
}
