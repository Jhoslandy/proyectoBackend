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

import java.time.LocalDate;
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
    @CachePut(value = "estudiante", key = "#result.nroMatricula")
    @CacheEvict(value = {"estudiantes"}, allEntries = true)
    public EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO) {
        estudianteValidator.validacionCompletaEstudiante(estudianteDTO);
        Estudiante estudiante = convertToEntity(estudianteDTO);
        // No se establece fechaInscripcion ni campos de usuario ya que no están en el modelo Estudiante
        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);
        return convertToDTO(estudianteGuardado);
    }

    @Override
    @CachePut(value = "estudiante", key = "#id")
    @CacheEvict(value = {"estudiantes"}, allEntries = true)
    public EstudianteDTO actualizarEstudiante(Long id, EstudianteDTO estudianteDTO) {
        Estudiante estudianteExistente = estudianteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Estudiante con ID " + id + " no encontrado para actualizar"));
        
        estudianteValidator.validarActualizacionEstudiante(estudianteDTO, estudianteExistente);

        // Actualizar solo los campos que están en el modelo Estudiante
        estudianteExistente.setNombre(estudianteDTO.getNombre());
        estudianteExistente.setApellido(estudianteDTO.getApellido());
        estudianteExistente.setCi(estudianteDTO.getCi());
        estudianteExistente.setEmail(estudianteDTO.getEmail());
        estudianteExistente.setFechaNacimiento(estudianteDTO.getFechaNacimiento());
        estudianteExistente.setNroMatricula(estudianteDTO.getNroMatricula());
        
        Estudiante estudianteActualizado = estudianteRepository.save(estudianteExistente);
        return convertToDTO(estudianteActualizado);
    }

    @Override
    @CacheEvict(value = {"estudiante", "estudiantes"}, allEntries = true)
    public void eliminarEstudiante(Long id) {
        if (!estudianteRepository.existsById(id)) {
            throw new BusinessException("Estudiante con ID " + id + " no encontrado para eliminar");
        }
        estudianteRepository.deleteById(id);
    }

    @Transactional
    public Estudiante obtenerEstudianteConBloqueo(Long id) {
        Estudiante est = estudianteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Estudiante no encontrado"));
            try { 
                Thread.sleep(15000); 
            } catch (InterruptedException e) { 
                Thread.currentThread().interrupt(); 
            }
        return est;
    }

    // Método auxiliar para convertir entidad a DTO
    private EstudianteDTO convertToDTO(Estudiante estudiante) {
        if (estudiante == null) {
            return null;
        }
        return EstudianteDTO.builder()
                .id(estudiante.getId())
                .nombre(estudiante.getNombre())
                .apellido(estudiante.getApellido())
                .ci(estudiante.getCi())
                .email(estudiante.getEmail())
                .fechaNacimiento(estudiante.getFechaNacimiento())
                .nroMatricula(estudiante.getNroMatricula())
                .build();
    }
    
    // Método auxiliar para convertir DTO a entidad
    private Estudiante convertToEntity(EstudianteDTO estudianteDTO) {
        if (estudianteDTO == null) {
            return null;
        }
        return Estudiante.builder()
                .id(estudianteDTO.getId())
                .nombre(estudianteDTO.getNombre())
                .apellido(estudianteDTO.getApellido())
                .ci(estudianteDTO.getCi())
                .email(estudianteDTO.getEmail())
                .fechaNacimiento(estudianteDTO.getFechaNacimiento())
                .nroMatricula(estudianteDTO.getNroMatricula())
                .build();
    }
}