package com.example.ProyectoTaw.validator;

import com.example.ProyectoTaw.dto.InscritoDTO;
import com.example.ProyectoTaw.model.Inscrito; // Puede que necesites el modelo para validaciones de actualización
import com.example.ProyectoTaw.repository.InscritoRepository; // Para validaciones que requieran acceder a la DB
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Tu clase de excepción de negocio
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InscritoValidator {

    private final InscritoRepository inscritoRepository;

    // Inyecta el repositorio si necesitas realizar validaciones que impliquen consultar la DB
    public InscritoValidator(InscritoRepository inscritoRepository) {
        this.inscritoRepository = inscritoRepository;
    }

    /**
     * Valida los datos de un InscritoDTO antes de la creación.
     * Esta validación complementa las anotaciones @Valid del DTO y la lógica de los 6 meses en el servicio.
     * @param inscritoDTO El DTO de la inscripción a validar.
     * @throws BusinessException si la validación falla.
     */
    public void validarCreacionInscripcion(InscritoDTO inscritoDTO) {
        // Ejemplo de validación adicional:
        // Asegurarse de que la fecha de inscripción no sea extremadamente antigua
        // o que no haya alguna regla de negocio sobre el día de la semana, etc.
        // Aquí no se valida la unicidad de estudiante-materia-fecha ni la regla de los 6 meses,
        // ya que esa lógica está centralizada en el servicio para una mejor gestión de la BusinessException
        // y la interacción con otras dependencias (MateriaRepository, EstudianteRepository).

        if (inscritoDTO.getFechaInscripcion().isBefore(LocalDate.of(2000, 1, 1))) {
            throw new BusinessException("La fecha de inscripción es demasiado antigua. Debe ser posterior al 01-01-2000.");
        }

        // Puedes agregar más validaciones aquí si tu negocio lo requiere.
        // Por ejemplo, que el CI del estudiante tenga un formato específico (más allá del tamaño)
        // o que el código de la materia cumpla alguna convención adicional.
    }

    /**
     * Valida los datos de un InscritoDTO antes de una actualización.
     * @param inscritoDTO El DTO de la inscripción con los datos actualizados.
     * @param inscripcionExistente La entidad Inscrito existente antes de la actualización.
     * @throws BusinessException si la validación falla.
     */
    public void validarActualizacionInscripcion(InscritoDTO inscritoDTO, Inscrito inscripcionExistente) {
        // En este caso, la lógica de unicidad y la regla de los 6 meses (si aplica para actualizaciones)
        // se manejan en el servicio.
        // Este validador puede enfocarse en:

        // 1. Validar que la fecha de inscripción no se cambie a una fecha futura (ya cubierta por @PastOrPresent en DTO)
        // if (inscritoDTO.getFechaInscripcion().isAfter(LocalDate.now())) {
        //     throw new BusinessException("La fecha de inscripción actualizada no puede ser futura.");
        // }

        // 2. Si hay campos que no deberían ser modificables en una actualización:
        // Por ejemplo, si una vez que se inscribe, no se puede cambiar el estudiante o la materia a la que está inscrito
        // (es decir, esas son "claves naturales" y un cambio sería una nueva inscripción).
        // if (!inscritoDTO.getEstudianteCi().equals(inscripcionExistente.getEstudiante().getCi())) {
        //     throw new BusinessException("No se puede cambiar el estudiante de una inscripción existente.");
        // }
        // if (!inscritoDTO.getMateriaCodigoUnico().equals(inscripcionExistente.getMateria().getCodigoUnico())) {
        //     throw new BusinessException("No se puede cambiar la materia de una inscripción existente.");
        // }

        // Dado que tu implementación de servicio permite actualizar el estudiante, materia y fecha,
        // y ya maneja las validaciones de unicidad y 6 meses, este validador podría ser más simple
        // o servir para validaciones que no son relacionales.
        // Por ejemplo, si la fecha de inscripción actualizada no puede ser anterior a la original por alguna razón:
        if (inscritoDTO.getFechaInscripcion().isBefore(inscripcionExistente.getFechaInscripcion())) {
             throw new BusinessException("La fecha de inscripción no puede ser anterior a la fecha original.");
        }
    }
}