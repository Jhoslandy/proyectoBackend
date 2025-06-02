package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.MateriaDTO;
import com.example.ProyectoTaw.model.Materia;
import com.example.ProyectoTaw.repository.MateriaRepository;
import com.example.ProyectoTaw.service.IMateriaService;
import com.example.ProyectoTaw.validator.MateriaValidator;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaServiceImpl implements IMateriaService {

    private final MateriaRepository materiaRepository;
    private final MateriaValidator materiaValidator;

    @Autowired
    public MateriaServiceImpl(MateriaRepository materiaRepository, MateriaValidator materiaValidator) {
        this.materiaRepository = materiaRepository;
        this.materiaValidator = materiaValidator;
    }

    @Override
    @CachePut(value = "materia", key = "#result.id")
    @CacheEvict(value = {"materias"}, allEntries = true)
    public MateriaDTO crearMateria(MateriaDTO dto) {
        materiaValidator.validacionCompletaMateria(dto);
        Materia materia = convertToEntity(dto);
        Materia guardada = materiaRepository.save(materia);
        return convertToDTO(guardada);
    }

    @Override
    @Cacheable(value = "materia", key = "#id")
    public MateriaDTO obtenerMateriaPorId(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Materia con ID " + id + " no encontrada"));
        return convertToDTO(materia);
    }

    @Override
    @Cacheable(value = "materias")
    public List<MateriaDTO> listarMaterias() {
        return materiaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "materia", key = "#id")
    @CacheEvict(value = {"materias"}, allEntries = true)
    public MateriaDTO actualizarMateria(Long id, MateriaDTO dto) {
        Materia existente = materiaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Materia con ID " + id + " no encontrada para actualizar"));

        materiaValidator.validarActualizacionMateria(dto, existente);

        existente.setNombre(dto.getNombreMateria());
        existente.setCodigoUnico(dto.getCodigoUnico());
        existente.setDescripcion(dto.getDescripcion());

        Materia actualizada = materiaRepository.save(existente);
        return convertToDTO(actualizada);
    }

    @Override
    @CacheEvict(value = {"materia", "materias"}, allEntries = true)
    public void eliminarMateria(Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new BusinessException("Materia con ID " + id + " no encontrada para eliminar");
        }
        materiaRepository.deleteById(id);
    }

    private MateriaDTO convertToDTO(Materia materia) {
        if (materia == null) return null;
        return MateriaDTO.builder()
                .id(materia.getId())
                .nombreMateria(materia.getNombre())
                .codigoUnico(materia.getCodigoUnico())
                .descripcion(materia.getDescripcion())
                .build();
    }

    private Materia convertToEntity(MateriaDTO dto) {
        if (dto == null) return null;
        return Materia.builder()
                .id(dto.getId())
                .nombre(dto.getNombreMateria())
                .codigoUnico(dto.getCodigoUnico())
                .descripcion(dto.getDescripcion())
                .build();
    }
}
