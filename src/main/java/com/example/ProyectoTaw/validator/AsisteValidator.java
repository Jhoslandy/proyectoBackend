package com.example.ProyectoTaw.validator;

import org.springframework.stereotype.Component;
import com.example.ProyectoTaw.dto.AsisteDTO;
import com.example.ProyectoTaw.model.Asiste; // Necesario para validar actualizaciones
import com.example.ProyectoTaw.repository.AsisteRepository; // Para validaciones de unicidad
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta ruta sea correcta

import java.time.LocalDate;
import java.util.Optional;

@Component // Indica que esta clase es un componente de Spring y puede ser inyectada
public class AsisteValidator {

    private final AsisteRepository asisteRepository;

    // Inyección de dependencias del repositorio a través del constructor
    public AsisteValidator(AsisteRepository asisteRepository) {
        this.asisteRepository = asisteRepository;
    }

    /**
     * Valida que la combinación de estudiante, curso y fecha sea única.
     * Esto es crucial ya que un estudiante no debería "asistir" al mismo curso en la misma fecha más de una vez.
     * @param estudianteCi El CI del estudiante.
     * @param cursoIdCurso El ID del curso.
     * @param fecha La fecha de la asistencia.
     * @param idAsisteActual El ID de la asistencia actual (si es una actualización) o null (si es una creación).
     * @throws BusinessException si la combinación ya existe para otra asistencia.
     */
    public void validaAsistenciaUnica(String estudianteCi, Integer cursoIdCurso, LocalDate fecha, Long idAsisteActual) {
        Optional<Asiste> existente = asisteRepository.findByEstudianteCiAndCursoIdCursoAndFecha(estudianteCi, cursoIdCurso, fecha);

        if (existente.isPresent()) {
            // Si existe un registro Y su ID no es el ID de la asistencia actual, significa que es un duplicado.
            if (idAsisteActual == null || !existente.get().getIdAsiste().equals(idAsisteActual)) {
                throw new BusinessException("Ya existe un registro de asistencia para el estudiante " + estudianteCi +
                                            " en el curso " + cursoIdCurso + " en la fecha " + fecha + ".");
            }
        }
    }

    /**
     * Valida que la fecha de asistencia no sea nula y no sea una fecha futura.
     * @param fecha La fecha a validar.
     * @throws BusinessException si la fecha es inválida.
     */
    public void validaFechaAsistencia(LocalDate fecha) {
        if (fecha == null) {
            throw new BusinessException("La fecha de asistencia es obligatoria.");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new BusinessException("La fecha de asistencia no puede ser una fecha futura.");
        }
    }

    /**
     * Valida que el estado de presencia no sea nulo.
     * @param presente El estado de presencia a validar.
     * @throws BusinessException si el estado de presencia es nulo.
     */
    public void validaEstadoPresente(Boolean presente) {
        if (presente == null) {
            throw new BusinessException("El estado de presencia (presente/ausente) es obligatorio.");
        }
    }

    /**
     * Realiza una validación completa para la creación de un nuevo registro de asistencia.
     * @param asisteDTO Los datos de la asistencia a validar.
     * @throws BusinessException si alguna validación falla.
     */
    public void validarCreacionAsistencia(AsisteDTO asisteDTO) {
        // En este caso, para una creación, idAsisteActual es null porque aún no existe en la DB
        validaAsistenciaUnica(asisteDTO.getEstudianteCi(), asisteDTO.getCursoIdCurso(), asisteDTO.getFecha(), null);
        validaFechaAsistencia(asisteDTO.getFecha());
        validaEstadoPresente(asisteDTO.getPresente());
        // Las validaciones de @NotNull, @PastOrPresent en el DTO se realizan automáticamente si usas @Valid en tu controlador.
        // Este validador añade lógica de negocio (unicidad, lógica de fechas avanzada).
    }

    /**
     * Realiza validaciones específicas para la actualización de un registro de asistencia existente.
     * @param asisteDTO Los nuevos datos de la asistencia.
     * @param asistenciaExistente La entidad de la asistencia tal como está actualmente en la base de datos.
     * @throws BusinessException si alguna validación falla.
     */
    public void validarActualizacionAsistencia(AsisteDTO asisteDTO, Asiste asistenciaExistente) {
        // Validar unicidad si alguno de los campos que forman la "clave natural" ha cambiado
        // (estudiante_ci, curso_id_curso, fecha)
        if (!asistenciaExistente.getEstudiante().getCi().equals(asisteDTO.getEstudianteCi()) ||
            !asistenciaExistente.getCurso().getIdCurso().equals(asisteDTO.getCursoIdCurso()) ||
            !asistenciaExistente.getFecha().equals(asisteDTO.getFecha())) {

            validaAsistenciaUnica(asisteDTO.getEstudianteCi(), asisteDTO.getCursoIdCurso(), asisteDTO.getFecha(), asistenciaExistente.getIdAsiste());
        }

        // Validaciones que siempre deben aplicarse, sin importar si los campos cambiaron
        validaFechaAsistencia(asisteDTO.getFecha());
        validaEstadoPresente(asisteDTO.getPresente());
    }
}