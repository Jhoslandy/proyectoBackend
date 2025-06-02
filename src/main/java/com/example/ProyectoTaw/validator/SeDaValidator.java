package com.example.ProyectoTaw.validator;

import com.example.ProyectoTaw.dto.SeDaDTO;
import com.example.ProyectoTaw.model.SeDa;
import com.example.ProyectoTaw.repository.SeDaRepository;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SeDaValidator {

    private final SeDaRepository seDaRepository;

    public SeDaValidator(SeDaRepository seDaRepository) {
        this.seDaRepository = seDaRepository;
    }

    /**
     * Valida que la combinación materia-curso sea única (aunque ya está en la entidad,
     * es bueno tenerla aquí para una validación de negocio más explícita antes de la DB).
     * @param materiaCodigoUnico Código único de la materia.
     * @param cursoIdCurso ID del curso.
     * @param idSeDaActual ID de la relación actual (para actualizaciones).
     * @throws BusinessException si la combinación no es única.
     */
    public void validaRelacionUnica(String materiaCodigoUnico, Integer cursoIdCurso, Long idSeDaActual) {
        Optional<SeDa> existente = seDaRepository.findByMateriaCodigoUnicoAndCursoIdCurso(materiaCodigoUnico, cursoIdCurso);

        if (existente.isPresent()) {
            if (idSeDaActual == null || !existente.get().getIdSeDa().equals(idSeDaActual)) {
                throw new BusinessException("Ya existe una relación entre la materia '" + materiaCodigoUnico +
                                            "' y el curso ID " + cursoIdCurso + ".");
            }
        }
    }

    public void validarCreacionRelacion(SeDaDTO seDaDTO) {
        // Llama a la validación de unicidad, pasando null para idSeDaActual
        validaRelacionUnica(seDaDTO.getMateriaCodigoUnico(), seDaDTO.getCursoIdCurso(), null);
        // Agrega cualquier otra validación específica para la creación si es necesaria
    }

    public void validarActualizacionRelacion(SeDaDTO seDaDTO, SeDa relacionExistente) {
        // Si la materia o el curso cambian, valida la unicidad de la nueva combinación
        if (!relacionExistente.getMateria().getCodigoUnico().equals(seDaDTO.getMateriaCodigoUnico()) ||
            !relacionExistente.getCurso().getIdCurso().equals(seDaDTO.getCursoIdCurso())) {
            validaRelacionUnica(seDaDTO.getMateriaCodigoUnico(), seDaDTO.getCursoIdCurso(), relacionExistente.getIdSeDa());
        }
        // Agrega cualquier otra validación específica para la actualización si es necesaria
    }
}