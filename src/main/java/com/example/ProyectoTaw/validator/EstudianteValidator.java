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

    public void validaEmailUnico(String email, Long idActual) {
        Optional<Estudiante> existente = estudianteRepository.findByEmail(email);
        if (existente.isPresent() && !existente.get().getId().equals(idActual)) {
            throw new BusinessException("Ya existe un estudiante con este email.");
        }
    }

    public void validaNroMatriculaUnico(String nroMatricula, Long idActual) {
        Optional<Estudiante> existente = estudianteRepository.findByNroMatricula(nroMatricula);
        if (existente.isPresent() && !existente.get().getId().equals(idActual)) {
            throw new BusinessException("Ya existe un estudiante con este número de matrícula.");
        }
    }

    public void validaCiUnico(String ci, Long idActual) {
        Optional<Estudiante> existente = estudianteRepository.findByCi(ci);
        if (existente.isPresent() && !existente.get().getId().equals(idActual)) {
            throw new BusinessException("Ya existe un estudiante con este CI.");
        }
    }

    // public void validaNombreUsuarioUnico(String nombreUsuario, Long idActual) { // Eliminado
    //     Optional<Estudiante> existente = estudianteRepository.findByNombreUsuario(nombreUsuario);
    //     if (existente.isPresent() && !existente.get().getId().equals(idActual)) {
    //         throw new BusinessException("Ya existe un estudiante con este nombre de usuario.");
    //     }
    // }

    public void validaDominioEmail(String email) {
        String dominio = email.substring(email.indexOf('@') + 1);
        List<String> dominiosBloqueados = Arrays.asList("dominiobloqueado.com", "spam.com");

        if (dominiosBloqueados.contains(dominio)) {
            throw new BusinessException("El dominio de email no está permitido.");
        }
    }

    public void validaNombreEstudiante(String nombre){
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessException("El nombre no puede estar vacío o nulo.");
        }
    }
  
    public void validaApellidoEstudiante(String apellido){
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new BusinessException("El apellido es obligatorio y no puede estar vacío.");
        }
    }

    public void validacionCompletaEstudiante(EstudianteDTO estudiante) {
        validaEmailUnico(estudiante.getEmail(), estudiante.getId());
        validaNroMatriculaUnico(estudiante.getNroMatricula(), estudiante.getId());
        validaCiUnico(estudiante.getCi(), estudiante.getId());
        // validaNombreUsuarioUnico(estudiante.getNombreUsuario(), estudiante.getId()); // Eliminado
        validaDominioEmail(estudiante.getEmail());
        validaNombreEstudiante(estudiante.getNombre());
        validaApellidoEstudiante(estudiante.getApellido());
    }

    public void validarActualizacionEstudiante(EstudianteDTO estudianteDTO, Estudiante estudianteExistente) {
        if (!estudianteExistente.getEmail().equalsIgnoreCase(estudianteDTO.getEmail())) {
            validaEmailUnico(estudianteDTO.getEmail(), estudianteExistente.getId());
        }
        if (!estudianteExistente.getNroMatricula().equalsIgnoreCase(estudianteDTO.getNroMatricula())) {
            validaNroMatriculaUnico(estudianteDTO.getNroMatricula(), estudianteExistente.getId());
        }
        if (!estudianteExistente.getCi().equalsIgnoreCase(estudianteDTO.getCi())) {
            validaCiUnico(estudianteDTO.getCi(), estudianteExistente.getId());
        }
        // if (!estudianteExistente.getNombreUsuario().equalsIgnoreCase(estudianteDTO.getNombreUsuario())) { // Eliminado
        //    validaNombreUsuarioUnico(estudianteDTO.getNombreUsuario(), estudianteExistente.getId());
        // }
        validaDominioEmail(estudianteDTO.getEmail());
        validaNombreEstudiante(estudianteDTO.getNombre());
        validaApellidoEstudiante(estudianteDTO.getApellido());
    }
}