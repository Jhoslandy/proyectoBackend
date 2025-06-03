package com.example.ProyectoTaw.validator;

import com.example.ProyectoTaw.dto.MateriaPrerequisitoDTO;
import com.example.ProyectoTaw.model.Materia;
import com.example.ProyectoTaw.repository.MateriaRepository;
import com.example.ProyectoTaw.repository.MateriaPrerequisitoRepository;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MateriaPrerequisitoValidator {

    private final MateriaRepository materiaRepository;
    private final MateriaPrerequisitoRepository prerequisitoRepository;

    @Autowired
    public MateriaPrerequisitoValidator(MateriaRepository materiaRepository,
                                        MateriaPrerequisitoRepository prerequisitoRepository) {
        this.materiaRepository = materiaRepository;
        this.prerequisitoRepository = prerequisitoRepository;
    }

    public void validarRelacion(MateriaPrerequisitoDTO dto) {
        if (dto.getMateriaId() == null || dto.getPrerequisitoId() == null) {
            throw new BusinessException("Los IDs de materia y prerrequisito no pueden ser nulos.");
        }

        if (dto.getMateriaId().equals(dto.getPrerequisitoId())) {
            throw new BusinessException("Una materia no puede ser prerrequisito de sí misma.");
        }

        if (!materiaRepository.existsById(dto.getMateriaId())) {
            throw new BusinessException("La materia con ID " + dto.getMateriaId() + " no existe.");
        }

        if (!materiaRepository.existsById(dto.getPrerequisitoId())) {
            throw new BusinessException("El prerrequisito con ID " + dto.getPrerequisitoId() + " no existe.");
        }

        boolean yaExiste = prerequisitoRepository
                .existsByMateriaIdAndPrerequisitoId(dto.getMateriaId(), dto.getPrerequisitoId());

        if (yaExiste) {
            throw new BusinessException("La relación materia-prerrequisito ya existe.");
        }
    }
}
