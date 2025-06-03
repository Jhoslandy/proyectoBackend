package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.MateriaPrerequisitoDTO;
import com.example.ProyectoTaw.model.MateriaPrerequisito;
import com.example.ProyectoTaw.repository.MateriaPrerequisitoRepository;
import com.example.ProyectoTaw.service.IMateriaPrerequisitoService;
import com.example.ProyectoTaw.validator.MateriaPrerequisitoValidator;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaPrerequisitoServiceImpl implements IMateriaPrerequisitoService {

    private final MateriaPrerequisitoRepository prerequisitoRepository;
    private final MateriaPrerequisitoValidator validator;

    @Autowired
    public MateriaPrerequisitoServiceImpl(MateriaPrerequisitoRepository prerequisitoRepository,
                                          MateriaPrerequisitoValidator validator) {
        this.prerequisitoRepository = prerequisitoRepository;
        this.validator = validator;
    }

    @Override
    public MateriaPrerequisitoDTO crearRelacion(MateriaPrerequisitoDTO dto) {
        validator.validarRelacion(dto);
        MateriaPrerequisito relacion = MateriaPrerequisito.builder()
                .materiaId(dto.getMateriaId())
                .prerequisitoId(dto.getPrerequisitoId())
                .build();
        relacion = prerequisitoRepository.save(relacion);
        return convertToDTO(relacion);
    }

    @Override
    public List<MateriaPrerequisitoDTO> listarPorMateriaId(Long materiaId) {
        return prerequisitoRepository.findByMateriaId(materiaId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MateriaPrerequisitoDTO> listarPorPrerequisitoId(Long prerequisitoId) {
        return prerequisitoRepository.findByPrerequisitoId(prerequisitoId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarRelacion(Long id) {
        if (!prerequisitoRepository.existsById(id)) {
            throw new BusinessException("La relación con ID " + id + " no existe.");
        }
        prerequisitoRepository.deleteById(id);
    }

    private MateriaPrerequisitoDTO convertToDTO(MateriaPrerequisito entity) {
        return MateriaPrerequisitoDTO.builder()
                .id(entity.getId())
                .materiaId(entity.getMateriaId())
                .prerequisitoId(entity.getPrerequisitoId())
                .build();
    }

    @Override
    public MateriaPrerequisitoDTO actualizarRelacion(Long id, MateriaPrerequisitoDTO dto) {
        MateriaPrerequisito existente = prerequisitoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("No se encontró la relación con ID " + id));

        // Validar nueva relación
        validator.validarRelacion(dto);

        // Actualizar campos
        existente.setMateriaId(dto.getMateriaId());
        existente.setPrerequisitoId(dto.getPrerequisitoId());

        prerequisitoRepository.save(existente);
        return convertToDTO(existente);
    }

}
