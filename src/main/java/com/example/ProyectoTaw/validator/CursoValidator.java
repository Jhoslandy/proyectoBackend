package com.example.ProyectoTaw.validator;

import org.springframework.stereotype.Component;
import com.example.ProyectoTaw.dto.CursoDTO;
import com.example.ProyectoTaw.model.Curso;
import com.example.ProyectoTaw.repository.CursoRepository;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

// import java.time.DayOfWeek; // REMOVE THIS IMPORT
import java.time.LocalTime;
import java.util.Optional;

@Component
public class CursoValidator {

    private final CursoRepository cursoRepository;

    public CursoValidator(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    /**
     * Valida que no exista otro curso con el mismo día (string) y horario.
     * @param dia El día de la semana del curso (String).
     * @param horario La hora del curso.
     * @param idCursoActual El ID del curso actual (si es una actualización) o null (si es una creación).
     * @throws BusinessException si ya existe un curso con el mismo día y horario para otro curso.
     */
    public void validaDiaHorarioUnico(String dia, LocalTime horario, Integer idCursoActual) { // Changed type to String
        Optional<Curso> existente = cursoRepository.findByDiaAndHorario(dia, horario); // Uses String dia
        if (existente.isPresent() && (idCursoActual == null || !existente.get().getIdCurso().equals(idCursoActual))) {
            throw new BusinessException("Ya existe un curso programado para el día " + dia + " a las " + horario.toString());
        }
    }

    /**
     * Valida que el semestre no esté vacío o nulo y tenga una longitud aceptable.
     * @param semestre El semestre a validar.
     * @throws BusinessException si el semestre es inválido.
     */
    public void validaSemestre(String semestre) {
        if (semestre == null || semestre.trim().isEmpty()) {
            throw new BusinessException("El semestre es obligatorio y no puede estar vacío.");
        }
        if (semestre.trim().length() > 50) {
            throw new BusinessException("El semestre no puede tener más de 50 caracteres.");
        }
    }

    /**
     * Valida que el año sea un valor válido y dentro de un rango razonable.
     * @param anio El año a validar.
     * @throws BusinessException si el año es inválido.
     */
    public void validaAnio(Integer anio) {
        if (anio == null) {
            throw new BusinessException("El año es obligatorio.");
        }
        if (anio < 1900 || anio > 2100) {
            throw new BusinessException("El año debe estar entre 1900 y 2100.");
        }
    }

    /**
     * Realiza una validación completa para la creación de un nuevo curso.
     * @param cursoDTO Los datos del curso a validar.
     * @throws BusinessException si alguna validación falla.
     */
    public void validacionCompletaCurso(CursoDTO cursoDTO) {
        // The @Pattern annotation in CursoDTO handles the valid day names.
        // This method still handles the uniqueness check.
        validaDiaHorarioUnico(cursoDTO.getDia(), cursoDTO.getHorario(), null);
        validaSemestre(cursoDTO.getSemestre());
        validaAnio(cursoDTO.getAnio());
    }

    /**
     * Realiza validaciones específicas para la actualización de un curso existente.
     * @param cursoDTO Los nuevos datos del curso.
     * @param cursoExistente La entidad del curso tal como está actualmente en la base de datos.
     * @throws BusinessException si alguna validación falla.
     */
    public void validarActualizacionCurso(CursoDTO cursoDTO, Curso cursoExistente) {
        // If the day or horario have changed, validate their uniqueness
        if (!cursoExistente.getDia().equalsIgnoreCase(cursoDTO.getDia()) || !cursoExistente.getHorario().equals(cursoDTO.getHorario())) {
            validaDiaHorarioUnico(cursoDTO.getDia(), cursoDTO.getHorario(), cursoExistente.getIdCurso());
        }

        // Validations that always apply
        validaSemestre(cursoDTO.getSemestre());
        validaAnio(cursoDTO.getAnio());
    }
}