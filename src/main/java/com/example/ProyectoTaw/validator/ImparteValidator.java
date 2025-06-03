package com.example.ProyectoTaw.validator;

import com.example.ProyectoTaw.dto.ImparteDTO;
import com.example.ProyectoTaw.dto.ImparteDTO;
import com.example.ProyectoTaw.model.Imparte;
import com.example.ProyectoTaw.model.Imparte;
import com.example.ProyectoTaw.repository.ImparteRepository;
import com.example.ProyectoTaw.repository.ImparteRepository;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ImparteValidator {
    private final ImparteRepository imparteRepository;

    public ImparteValidator(ImparteRepository imparteRepository) {
        this.imparteRepository = imparteRepository;
    }

    /**
     * Valida que la combinación materia-Docente sea única (aunque ya está en la entidad,
     * es bueno tenerla aquí para una validación de negocio más explícita antes de la DB).
     * @param materiaCodigoUnico Código único de la materia.
     * @param ciDocente ID del Docente.
     * @param idImparteActual ID de la relación actual (para actualizaciones).
     * @throws BusinessException si la combinación no es única.
     */
    public void validaRelacionUnica(String materiaCodigoUnico, String ciDocente, Long idImparteActual) {
        Optional<Imparte> existente = imparteRepository.findByMateriaCodigoUnicoAndDocenteCiDocente(materiaCodigoUnico, ciDocente);

        if (existente.isPresent()) {
            if (idImparteActual == null || !existente.get().getIdImparte().equals(idImparteActual)) {
                throw new BusinessException("Ya existe una relación entre la materia '" + materiaCodigoUnico +
                                            "' y el Docente ci " + ciDocente + ".");
            }
        }
    }

    public void validarCreacionRelacion(ImparteDTO imparteDTO) {
        // Llama a la validación de unicidad, pasando null para idImparteActual
        validaRelacionUnica(imparteDTO.getMateriaCodigoUnico(), imparteDTO.getCiDocente(), null);
        // Agrega cualquier otra validación específica para la creación si es necesaria
    }

    @SuppressWarnings("unlikely-arg-type")
    public void validarActualizacionRelacion(ImparteDTO imparteDTO, Imparte relacionExistente) {
        // Si la materia o el Docente cambian, valida la unicidad de la nueva combinación
        if (!relacionExistente.getMateria().getCodigoUnico().equals(imparteDTO.getMateriaCodigoUnico()) ||
            !relacionExistente.getDocente().getCiDocente().equals(imparteDTO.getCiDocente())) {
            validaRelacionUnica(imparteDTO.getMateriaCodigoUnico(), imparteDTO.getCiDocente(), relacionExistente.getIdImparte());
        }
        // Agrega cualquier otra validación específica para la actualización si es necesaria
    }

}
