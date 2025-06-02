package com.example.ProyectoTaw.validator;

import com.example.ProyectoTaw.dto.MateriaDTO;
import com.example.ProyectoTaw.model.Materia;
import com.example.ProyectoTaw.repository.MateriaRepository;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MateriaValidator {

    private final MateriaRepository materiaRepository;

    public MateriaValidator(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    public void validarNombreUnico(String nombre, Long idActual) {
        Optional<Materia> existente = materiaRepository.findByNombre(nombre);
        if (existente.isPresent() && (idActual == null || !existente.get().getId().equals(idActual))) {
            throw new BusinessException("Ya existe una materia con el nombre: " + nombre);
        }
    }

    public void validarNombreMateria(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessException("El nombre de la materia no puede estar vacío.");
        }
        if (nombre.length() < 3 || nombre.length() > 100) {
            throw new BusinessException("El nombre debe tener entre 3 y 100 caracteres.");
        }
    }

    public void validacionCompletaMateria(MateriaDTO materiaDTO) {
        validarNombreUnico(materiaDTO.getNombreMateria(), null);
        validarNombreMateria(materiaDTO.getNombreMateria());
    }

    public void validarActualizacionMateria(MateriaDTO materiaDTO, Materia materiaExistente) {
        if (!materiaExistente.getNombre().equalsIgnoreCase(materiaDTO.getNombreMateria())) {
            validarNombreUnico(materiaDTO.getNombreMateria(), materiaExistente.getId());
        }
        validarNombreMateria(materiaDTO.getNombreMateria());
    }
}
