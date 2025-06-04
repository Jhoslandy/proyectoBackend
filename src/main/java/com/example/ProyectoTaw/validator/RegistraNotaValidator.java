package com.example.ProyectoTaw.validator;

import com.example.ProyectoTaw.dto.RegistraNotaDTO;
import com.example.ProyectoTaw.model.RegistraNota;
import com.example.ProyectoTaw.repository.RegistraNotaRepository;
import com.example.ProyectoTaw.repository.EstudianteRepository;
import com.example.ProyectoTaw.repository.CursoRepository;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class RegistraNotaValidator {

    private final RegistraNotaRepository registraNotaRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

    public RegistraNotaValidator(RegistraNotaRepository registraNotaRepository,
                                 EstudianteRepository estudianteRepository,
                                 CursoRepository cursoRepository) {
        this.registraNotaRepository = registraNotaRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
    }

    public void validarNotaValida(Double nota) {
        if (nota == null) {
            throw new BusinessException("La nota no puede ser nula.");
        }
        if (nota < 0.0 || nota > 100.0) {
            throw new BusinessException("La nota debe estar entre 0.0 y 100.0.");
        }
    }

    public void validarFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new BusinessException("La fecha no puede ser nula.");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new BusinessException("La fecha no puede ser en el futuro.");
        }
    }


    public void validarExistenciaEstudiante(String estudianteId) {
        if (estudianteId == null || estudianteId.trim().isEmpty() ||
                estudianteRepository.findById(estudianteId).isEmpty()) {
            throw new BusinessException("El estudiante con CI " + estudianteId + " no existe.");
        }
    }

    public void validarRegistroUnico(String estudianteId, Integer cursoId, String evaluacion, Long idActual) {
        Optional<RegistraNota> existente = registraNotaRepository
                .findByEstudianteCiAndCursoIdCursoAndEvaluacion(estudianteId, cursoId, evaluacion);

        if (existente.isPresent() && (idActual == null || !existente.get().getId().equals(idActual))) {
            throw new BusinessException("Ya existe un registro de nota para esta evaluación del estudiante en este curso.");
        }
    }

    public void validarExistenciaCurso(Integer cursoId) {
        if (cursoId == null || cursoRepository.findById(cursoId).isEmpty()) {
            throw new BusinessException("El curso con ID " + cursoId + " no existe.");
        }
    }

    public void validacionCompletaNota(RegistraNotaDTO dto) {
        validarExistenciaEstudiante(dto.getEstudianteId());
        validarExistenciaCurso(dto.getCursoId());
        validarNotaValida(dto.getNota());
        validarFecha(dto.getFecha());
        validarRegistroUnico(dto.getEstudianteId(), dto.getCursoId(), dto.getEvaluacion(), null);
    }

    public void validarActualizacionNota(RegistraNotaDTO dto, RegistraNota notaExistente) {
        if (!notaExistente.getEvaluacion().equalsIgnoreCase(dto.getEvaluacion()) ||
                !notaExistente.getCurso().getIdCurso().equals(dto.getCursoId()) ||
                !notaExistente.getEstudiante().getCi().equals(dto.getEstudianteId())) {

            validarRegistroUnico(dto.getEstudianteId(), dto.getCursoId(), dto.getEvaluacion(), notaExistente.getId());
        }

        validarExistenciaEstudiante(dto.getEstudianteId());
        validarExistenciaCurso(dto.getCursoId());
        validarNotaValida(dto.getNota());
        validarFecha(dto.getFecha());
    }
}
