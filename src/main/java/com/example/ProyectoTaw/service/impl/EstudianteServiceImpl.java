package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;
import com.example.ProyectoTaw.repository.EstudianteRepository;
import com.example.ProyectoTaw.service.IEstudianteService;
import com.example.ProyectoTaw.validator.EstudianteValidator;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta clase exista

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId; // Necesario para la conversión de Date a LocalDate

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
    @Cacheable(value = "estudiante", key = "#ci") // Cambiado a 'ci'
    public EstudianteDTO obtenerEstudiantePorCi(String ci) { // Cambiado a 'obtenerEstudiantePorCi' y tipo a String
        Estudiante estudiante = estudianteRepository.findByCi(ci) // Usando findByCi
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
    @CachePut(value = "estudiante", key = "#result.ci") // Ajustado a result.ci
    @CacheEvict(value = {"estudiantes"}, allEntries = true)
    @Transactional // Agregado @Transactional para operaciones de escritura
    public EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO) {
        // Validar si el CI ya existe antes de crear
        if (estudianteRepository.existsByCi(estudianteDTO.getCi())) {
            throw new BusinessException("Ya existe un estudiante con la CI: " + estudianteDTO.getCi());
        }
        // Validar si el email ya existe antes de crear
        if (estudianteRepository.existsByEmail(estudianteDTO.getEmail())) {
            throw new BusinessException("Ya existe un estudiante con el email: " + estudianteDTO.getEmail());
        }

        estudianteValidator.validacionCompletaEstudiante(estudianteDTO); // Asume que este método también valida campos

        Estudiante estudiante = convertToEntity(estudianteDTO);
        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);
        return convertToDTO(estudianteGuardado);
    }

    @Override
    @CachePut(value = "estudiante", key = "#ci")
    @CacheEvict(value = {"estudiantes"}, allEntries = true)
    @Transactional // Agregado @Transactional para operaciones de escritura
    public EstudianteDTO actualizarEstudiante(String ci, EstudianteDTO estudianteDTO) { // Tipo cambiado a String
        Estudiante estudianteExistente = estudianteRepository.findById(ci) // Usando findById
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + ci + " no encontrado para actualizar"));

        // Validar que el email, si se cambia, no exista para otro estudiante
        if (!estudianteExistente.getEmail().equals(estudianteDTO.getEmail()) && estudianteRepository.existsByEmail(estudianteDTO.getEmail())) {
            throw new BusinessException("El email '" + estudianteDTO.getEmail() + "' ya está registrado para otro estudiante.");
        }

        estudianteValidator.validarActualizacionEstudiante(estudianteDTO, estudianteExistente); // Asume validación

        // Actualizar campos de la entidad con los datos del DTO
        // Importante: No actualizamos 'ci' ya que es la clave primaria y no debería cambiar
        estudianteExistente.setNombre(estudianteDTO.getNombre());
        estudianteExistente.setApellido(estudianteDTO.getApellido());
        estudianteExistente.setEmail(estudianteDTO.getEmail());
        // Convierte LocalDate del DTO a Date para la entidad
        estudianteExistente.setFechaNac(estudianteDTO.getFechaNac());

        Estudiante estudianteActualizado = estudianteRepository.save(estudianteExistente);
        return convertToDTO(estudianteActualizado);
    }

    @Override
    @CacheEvict(value = {"estudiante", "estudiantes"}, allEntries = true)
    @Transactional // Agregado @Transactional para operaciones de escritura
    public void eliminarEstudiante(String ci) { // Tipo cambiado a String
        if (!estudianteRepository.existsById(ci)) {
            throw new BusinessException("Estudiante con CI " + ci + " no encontrado para eliminar");
        }
        estudianteRepository.deleteById(ci);
    }

    @Override
    @Transactional // Asegura que esta operación se ejecute dentro de una transacción
    public Estudiante obtenerEstudianteConBloqueo(String ci) { // Tipo cambiado a String
        // Usamos el findById que tiene la anotación @Lock en el repositorio
        Estudiante est = estudianteRepository.findById(ci) 
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + ci + " no encontrado"));
        
        // Simula un proceso largo que mantiene el bloqueo
        try {
            Thread.sleep(15000); // 15 segundos
        } catch (InterruptedException e) {
            // Restaura el estado de interrupción
            Thread.currentThread().interrupt(); 
            throw new BusinessException("La operación de bloqueo fue interrumpida.");
        }
        return est;
    }

    // --- Métodos de Conversión DTO <-> Entidad ---

    // Convierte una entidad Estudiante a un EstudianteDTO
    private EstudianteDTO convertToDTO(Estudiante estudiante) {
        return EstudianteDTO.builder()
                .ci(estudiante.getCi())
                .nombre(estudiante.getNombre())
                .apellido(estudiante.getApellido())
                .email(estudiante.getEmail())
                // Convierte java.util.Date a java.time.LocalDate
                .fechaNac(estudiante.getFechaNac())
                .build();
    }

    // Convierte un EstudianteDTO a una entidad Estudiante
    private Estudiante convertToEntity(EstudianteDTO estudianteDTO) {
        if (estudianteDTO == null) {
            return null;
        }
        return Estudiante.builder()
                .ci(estudianteDTO.getCi())
                .nombre(estudianteDTO.getNombre())
                .apellido(estudianteDTO.getApellido())
                .email(estudianteDTO.getEmail())
                // Convierte java.time.LocalDate a java.util.Date
                .fechaNac(estudianteDTO.getFechaNac())
                .build();
    }
}