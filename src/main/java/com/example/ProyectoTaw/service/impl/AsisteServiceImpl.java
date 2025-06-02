package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.AsisteDTO;
import com.example.ProyectoTaw.model.Asiste;
import com.example.ProyectoTaw.model.Estudiante; // Necesario para buscar Estudiante
import com.example.ProyectoTaw.model.Curso;     // Necesario para buscar Curso
import com.example.ProyectoTaw.repository.AsisteRepository;
import com.example.ProyectoTaw.repository.EstudianteRepository; // Necesario
import com.example.ProyectoTaw.repository.CursoRepository;     // Necesario
import com.example.ProyectoTaw.service.IAsisteService;
import com.example.ProyectoTaw.validator.AsisteValidator; // Asumimos que tendrás un validador para Asiste
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta clase exista

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
public class AsisteServiceImpl implements IAsisteService {

    private final AsisteRepository asisteRepository;
    private final EstudianteRepository estudianteRepository; // Para buscar estudiantes
    private final CursoRepository cursoRepository;           // Para buscar cursos
    private final AsisteValidator asisteValidator;           // Tu validador para Asiste

    @Autowired
    public AsisteServiceImpl(AsisteRepository asisteRepository,
                             EstudianteRepository estudianteRepository,
                             CursoRepository cursoRepository,
                             AsisteValidator asisteValidator) {
        this.asisteRepository = asisteRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
        this.asisteValidator = asisteValidator;
    }

    @Override
    @Cacheable(value = "asistencias")
    public List<AsisteDTO> obtenerTodasLasAsistencias() {
        return asisteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "asistencia", key = "#idAsiste")
    public AsisteDTO obtenerAsistenciaPorId(Long idAsiste) {
        Asiste asistencia = asisteRepository.findById(idAsiste)
                .orElseThrow(() -> new BusinessException("Asistencia con ID " + idAsiste + " no encontrada"));
        return convertToDTO(asistencia);
    }

    @Override
    @Cacheable(value = "asistenciasPorEstudiante", key = "#estudianteCi")
    public List<AsisteDTO> obtenerAsistenciasPorEstudiante(String estudianteCi) {
        return asisteRepository.findByEstudianteCi(estudianteCi).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "asistenciasPorCurso", key = "#cursoIdCurso")
    public List<AsisteDTO> obtenerAsistenciasPorCurso(Integer cursoIdCurso) {
        return asisteRepository.findByCursoIdCurso(cursoIdCurso).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "asistenciasDeEstudianteEnCurso", key = "{#estudianteCi, #cursoIdCurso}")
    public List<AsisteDTO> obtenerAsistenciasDeEstudianteEnCurso(String estudianteCi, Integer cursoIdCurso) {
        return asisteRepository.findByEstudianteCiAndCursoIdCurso(estudianteCi, cursoIdCurso).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"asistencias", "asistenciasPorEstudiante", "asistenciasPorCurso", "asistenciasDeEstudianteEnCurso"}, allEntries = true)
    @Transactional
    public AsisteDTO crearAsistencia(AsisteDTO asisteDTO) {
        // Validar que el estudiante y el curso existan
        Estudiante estudiante = estudianteRepository.findByCi(asisteDTO.getEstudianteCi())
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + asisteDTO.getEstudianteCi() + " no encontrado."));

        Curso curso = cursoRepository.findById(asisteDTO.getCursoIdCurso())
                .orElseThrow(() -> new BusinessException("Curso con ID " + asisteDTO.getCursoIdCurso() + " no encontrado."));

        // Opcional: Validar si ya existe una asistencia para la misma combinación estudiante-curso-fecha
        // (Si has agregado la restricción UNIQUE en el modelo Asiste)
        if (asisteRepository.existsByEstudianteCiAndCursoIdCursoAndFecha(
            asisteDTO.getEstudianteCi(), asisteDTO.getCursoIdCurso(), asisteDTO.getFecha())) {
            throw new BusinessException("Ya existe un registro de asistencia para este estudiante, curso y fecha.");
        }

        asisteValidator.validarCreacionAsistencia(asisteDTO); // Asume que este método valida el DTO

        Asiste asistencia = convertToEntity(asisteDTO, estudiante, curso);
        Asiste asistenciaGuardada = asisteRepository.save(asistencia);
        return convertToDTO(asistenciaGuardada);
    }

    @Override
    @CachePut(value = "asistencia", key = "#idAsiste")
    @CacheEvict(value = {"asistencias", "asistenciasPorEstudiante", "asistenciasPorCurso", "asistenciasDeEstudianteEnCurso"}, allEntries = true)
    @Transactional
    public AsisteDTO actualizarAsistencia(Long idAsiste, AsisteDTO asisteDTO) {
        Asiste asistenciaExistente = asisteRepository.findById(idAsiste)
                .orElseThrow(() -> new BusinessException("Asistencia con ID " + idAsiste + " no encontrada para actualizar"));

        // Validar que el estudiante y el curso existan si se intenta cambiar (aunque normalmente no se cambia)
        Estudiante nuevoEstudiante = estudianteRepository.findByCi(asisteDTO.getEstudianteCi())
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + asisteDTO.getEstudianteCi() + " no encontrado."));

        Curso nuevoCurso = cursoRepository.findById(asisteDTO.getCursoIdCurso())
                .orElseThrow(() -> new BusinessException("Curso con ID " + asisteDTO.getCursoIdCurso() + " no encontrado."));

        // Opcional: Validar unicidad si se cambian los campos que formaban la clave compuesta original
        if (!asistenciaExistente.getEstudiante().getCi().equals(asisteDTO.getEstudianteCi()) ||
            !asistenciaExistente.getCurso().getIdCurso().equals(asisteDTO.getCursoIdCurso()) ||
            !asistenciaExistente.getFecha().equals(asisteDTO.getFecha())) {

            if (asisteRepository.existsByEstudianteCiAndCursoIdCursoAndFecha(
                asisteDTO.getEstudianteCi(), asisteDTO.getCursoIdCurso(), asisteDTO.getFecha())) {
                throw new BusinessException("Ya existe un registro de asistencia para la combinación estudiante-curso-fecha proporcionada.");
            }
        }

        asisteValidator.validarActualizacionAsistencia(asisteDTO, asistenciaExistente); // Asume validación

        // Actualizar campos de la entidad
        asistenciaExistente.setEstudiante(nuevoEstudiante);
        asistenciaExistente.setCurso(nuevoCurso);
        asistenciaExistente.setFecha(asisteDTO.getFecha());
        asistenciaExistente.setPresente(asisteDTO.getPresente());

        Asiste asistenciaActualizada = asisteRepository.save(asistenciaExistente);
        return convertToDTO(asistenciaActualizada);
    }

    @Override
    @CacheEvict(value = {"asistencia", "asistencias", "asistenciasPorEstudiante", "asistenciasPorCurso", "asistenciasDeEstudianteEnCurso"}, allEntries = true)
    @Transactional
    public void eliminarAsistencia(Long idAsiste) {
        if (!asisteRepository.existsById(idAsiste)) {
            throw new BusinessException("Asistencia con ID " + idAsiste + " no encontrada para eliminar");
        }
        asisteRepository.deleteById(idAsiste);
    }

    @Override
    @Transactional
    public Asiste obtenerAsistenciaConBloqueo(Long idAsiste) {
        Asiste asiste = asisteRepository.findById(idAsiste)
                .orElseThrow(() -> new BusinessException("Asistencia con ID " + idAsiste + " no encontrada."));

        // Simula un proceso largo que mantiene el bloqueo
        try {
            Thread.sleep(15000); // 15 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("La operación de bloqueo fue interrumpida.");
        }
        return asiste;
    }

    // --- Métodos de Conversión DTO <-> Entidad ---

    // Convierte una entidad Asiste a un AsisteDTO
    private AsisteDTO convertToDTO(Asiste asiste) {
        if (asiste == null) {
            return null;
        }
        return AsisteDTO.builder()
                .idAsiste(asiste.getIdAsiste())
                .estudianteCi(asiste.getEstudiante().getCi())
                .cursoIdCurso(asiste.getCurso().getIdCurso())
                .fecha(asiste.getFecha())
                .presente(asiste.getPresente())
                .build();
    }

    // Convierte un AsisteDTO a una entidad Asiste
    // Necesita las entidades Estudiante y Curso para establecer las relaciones ManyToOne
    private Asiste convertToEntity(AsisteDTO asisteDTO, Estudiante estudiante, Curso curso) {
        if (asisteDTO == null) {
            return null;
        }
        return Asiste.builder()
                .idAsiste(asisteDTO.getIdAsiste()) // Puede ser nulo en la creación
                .estudiante(estudiante)
                .curso(curso)
                .fecha(asisteDTO.getFecha())
                .presente(asisteDTO.getPresente())
                .build();
    }
}