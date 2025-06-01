package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;
import com.example.ProyectoTaw.repository.EstudianteRepository;
import com.example.ProyectoTaw.service.IEstudianteService;
import com.example.ProyectoTaw.validator.EstudianteValidator;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstudianteServiceImpl implements IEstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final EstudianteValidator estudianteValidator;

    @Autowired
    public EstudianteServiceImpl(EstudianteRepository estudianteRepository, EstudianteValidator estudianteValidator) {
        this.estudianteRepository = estudianteRepository;
        this.estudianteValidator = estudianteValidator;
    }

    @Override
    @Cacheable(value = "estudiantes")
    public List<EstudianteDTO> obtenerTodosLosEstudiantes() {
        return estudianteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "estudiante", key = "#nroMatricula")
    public EstudianteDTO obtenerEstudiantePorNroMatricula(String nroMatricula) {
        Estudiante estudiante = estudianteRepository.findByNroMatricula(nroMatricula)
                .orElseThrow(() -> new BusinessException("Estudiante con número de matrícula " + nroMatricula + " no encontrado"));
        return convertToDTO(estudiante);
    }

    @Override
    @Cacheable(value = "estudiantes", key = "#query")
    public List<EstudianteDTO> buscarEstudiantes(String query) {
        return estudianteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(query, query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "estudiante", key = "#result.ci")
    @CacheEvict(value = {"estudiantes"}, allEntries = true)
    public EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO) {
        estudianteValidator.validacionCompletaEstudiante(estudianteDTO);
        Estudiante estudiante = convertToEntity(estudianteDTO);
        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);
        return convertToDTO(estudianteGuardado);
    }

    @Override
    @CachePut(value = "estudiante", key = "#ci")
    @CacheEvict(value = {"estudiantes"}, allEntries = true)
    public EstudianteDTO actualizarEstudiante(Integer ci, EstudianteDTO estudianteDTO) {
        Estudiante estudianteExistente = estudianteRepository.findById(ci)
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + ci + " no encontrado para actualizar"));

        estudianteValidator.validarActualizacionEstudiante(estudianteDTO, estudianteExistente);

        estudianteExistente.setNombre(estudianteDTO.getNombre());
        estudianteExistente.setApellido(estudianteDTO.getApellido());
        estudianteExistente.setEmail(estudianteDTO.getEmail());
        estudianteExistente.setFechaNacimiento(estudianteDTO.getFechaNacimiento());
        estudianteExistente.setNroMatricula(estudianteDTO.getNroMatricula());

        Estudiante estudianteActualizado = estudianteRepository.save(estudianteExistente);
        return convertToDTO(estudianteActualizado);
    }

    @Override
    @CacheEvict(value = {"estudiante", "estudiantes"}, allEntries = true)
    public void eliminarEstudiante(Integer ci) {
        if (!estudianteRepository.existsById(ci)) {
            throw new BusinessException("Estudiante con CI " + ci + " no encontrado para eliminar");
        }
        estudianteRepository.deleteById(ci);
    }

    @Override
    @Transactional
    public Estudiante obtenerEstudianteConBloqueo(Integer ci) {
        Estudiante est = estudianteRepository.findById(ci)
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + ci + " no encontrado"));
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return est;
    }

    private EstudianteDTO convertToDTO(Estudiante estudiante) {
        if (estudiante == null) return null;
        return EstudianteDTO.builder()
                .ci(estudiante.getCi())
                .nombre(estudiante.getNombre())
                .apellido(estudiante.getApellido())
                .email(estudiante.getEmail())
                .fechaNacimiento(estudiante.getFechaNacimiento())
                .nroMatricula(estudiante.getNroMatricula())
                .build();
    }

    private Estudiante convertToEntity(EstudianteDTO estudianteDTO) {
        if (estudianteDTO == null) return null;
        return Estudiante.builder()
                .ci(estudianteDTO.getCi())
                .nombre(estudianteDTO.getNombre())
                .apellido(estudianteDTO.getApellido())
                .email(estudianteDTO.getEmail())
                .fechaNacimiento(estudianteDTO.getFechaNacimiento())
                .nroMatricula(estudianteDTO.getNroMatricula())
                .build();
    }
}
