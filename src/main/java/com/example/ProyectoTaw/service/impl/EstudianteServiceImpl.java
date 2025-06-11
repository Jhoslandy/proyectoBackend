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
// import java.sql.Date; // No longer strictly needed if using LocalDate for entity
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class EstudianteServiceImpl implements IEstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final EstudianteValidator estudianteValidator;

    @Autowired
    public EstudianteServiceImpl(EstudianteRepository estudianteRepository, EstudianteValidator estudianteValidator) {
        this.estudianteRepository = estudianteRepository;
        this.estudianteValidator = estudianteValidator;
    }

    public EstudianteServiceImpl(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
        this.estudianteValidator = null;
    }

    @Override
    @Cacheable(value = "estudiantes")
    public List<EstudianteDTO> obtenerTodosLosEstudiantes() {
        return estudianteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "estudiante", key = "#ci")
    public EstudianteDTO obtenerEstudiantePorCi(String ci) {
        Estudiante estudiante = estudianteRepository.findByCi(ci)
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + ci + " no encontrado"));
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
    @Transactional
    public EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO) {
        if (estudianteRepository.existsByCi(estudianteDTO.getCi())) {
            throw new BusinessException("Ya existe un estudiante con la CI: " + estudianteDTO.getCi());
        }
        if (estudianteRepository.existsByEmail(estudianteDTO.getEmail())) {
            throw new BusinessException("Ya existe un estudiante con el email: " + estudianteDTO.getEmail());
        }

        if (estudianteValidator != null) {
            estudianteValidator.validacionCompletaEstudiante(estudianteDTO);
        }

        Estudiante estudiante = convertToEntity(estudianteDTO);
        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);
        return convertToDTO(estudianteGuardado);
    }

    @Override
    @CachePut(value = "estudiante", key = "#ci")
    @CacheEvict(value = {"estudiantes"}, allEntries = true)
    @Transactional
    public EstudianteDTO actualizarEstudiante(String ci, EstudianteDTO estudianteDTO) {
        Estudiante estudianteExistente = estudianteRepository.findById(ci)
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + ci + " no encontrado para actualizar"));

        if (!estudianteExistente.getEmail().equals(estudianteDTO.getEmail()) && estudianteRepository.existsByEmail(estudianteDTO.getEmail())) {
            throw new BusinessException("El email '" + estudianteDTO.getEmail() + "' ya está registrado para otro estudiante.");
        }

        if (estudianteValidator != null) {
            estudianteValidator.validarActualizacionEstudiante(estudianteDTO, estudianteExistente);
        }

        estudianteExistente.setNombre(estudianteDTO.getNombre());
        estudianteExistente.setApellido(estudianteDTO.getApellido());
        estudianteExistente.setEmail(estudianteDTO.getEmail());
        estudianteExistente.setFechaNac(estudianteDTO.getFechaNac());

        Estudiante estudianteActualizado = estudianteRepository.save(estudianteExistente);
        return convertToDTO(estudianteActualizado);
    }

    @Override
    @CacheEvict(value = {"estudiante", "estudiantes"}, allEntries = true)
    @Transactional
    public void eliminarEstudiante(String ci) {
        if (!estudianteRepository.existsById(ci)) {
            throw new BusinessException("Estudiante con CI " + ci + " no encontrado para eliminar");
        }
        estudianteRepository.deleteById(ci);
    }

    @Override
    @Transactional
    public Estudiante obtenerEstudianteConBloqueo(String ci) {
        Estudiante est = estudianteRepository.findById(ci)
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + ci + " no encontrado"));

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("La operación de bloqueo fue interrumpida.");
        }
        return est;
    }

    // --- Métodos de Conversión DTO <-> Entidad ---

    private EstudianteDTO convertToDTO(Estudiante estudiante) {
        return EstudianteDTO.builder()
                .ci(estudiante.getCi())
                .nombre(estudiante.getNombre())
                .apellido(estudiante.getApellido())
                .email(estudiante.getEmail())
                .fechaNac(estudiante.getFechaNac())
                .build();
    }

    private Estudiante convertToEntity(EstudianteDTO estudianteDTO) {
        if (estudianteDTO == null) {
            return null;
        }
        return Estudiante.builder()
                .ci(estudianteDTO.getCi())
                .nombre(estudianteDTO.getNombre())
                .apellido(estudianteDTO.getApellido())
                .email(estudianteDTO.getEmail())
                .fechaNac(estudianteDTO.getFechaNac())
                .build();
    }

    @Override
    public EstudianteDTO obtenerEstudiantePorEmail(String email) {
        // CORRECTED IMPLEMENTATION:
        Estudiante estudiante = estudianteRepository.findByEmail(email) //
                .orElseThrow(() -> new BusinessException("Estudiante con email " + email + " no encontrado"));
        return convertToDTO(estudiante);
    }
}