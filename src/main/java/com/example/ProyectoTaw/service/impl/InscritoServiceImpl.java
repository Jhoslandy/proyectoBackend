// Archivo: src/main/java/com/example/ProyectoTaw/service/impl/InscritoServiceImpl.java

package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.InscritoDTO;
import com.example.ProyectoTaw.model.Estudiante;
import com.example.ProyectoTaw.model.Materia;
import com.example.ProyectoTaw.model.Inscrito;
import com.example.ProyectoTaw.repository.InscritoRepository;
import com.example.ProyectoTaw.repository.EstudianteRepository;
import com.example.ProyectoTaw.repository.MateriaRepository;
import com.example.ProyectoTaw.service.IInscritoService;
import com.example.ProyectoTaw.validator.InscritoValidator;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InscritoServiceImpl implements IInscritoService {

    private final InscritoRepository inscritoRepository;
    private final EstudianteRepository estudianteRepository;
    private final MateriaRepository materiaRepository;
    private final InscritoValidator inscritoValidator; // Asume que tienes este validador

    @Autowired
    public InscritoServiceImpl(InscritoRepository inscritoRepository,
                               EstudianteRepository estudianteRepository,
                               MateriaRepository materiaRepository,
                               InscritoValidator inscritoValidator) { // Añade el validador al constructor si lo usas
        this.inscritoRepository = inscritoRepository;
        this.estudianteRepository = estudianteRepository;
        this.materiaRepository = materiaRepository;
        this.inscritoValidator = inscritoValidator; // Inicializa el validador
    }

    @Override
    @Cacheable(value = "inscripciones")
    public List<InscritoDTO> obtenerTodasLasInscripciones() {
        return inscritoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "inscripcion", key = "#idInscrito")
    public InscritoDTO obtenerInscripcionPorId(Long idInscrito) {
        Inscrito inscrito = inscritoRepository.findById(idInscrito)
                .orElseThrow(() -> new BusinessException("Inscripción con ID " + idInscrito + " no encontrada."));
        return convertToDTO(inscrito);
    }

    @Override
    @Cacheable(value = "inscripcionesPorEstudiante", key = "#estudianteCi")
    public List<InscritoDTO> obtenerInscripcionesPorEstudiante(String estudianteCi) {
        return inscritoRepository.findByEstudianteCi(estudianteCi).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "inscripcionesPorMateria", key = "#materiaCodigoUnico")
    public List<InscritoDTO> obtenerInscripcionesPorMateria(String materiaCodigoUnico) {
        return inscritoRepository.findByMateriaCodigoUnico(materiaCodigoUnico).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "inscripcionPorEstudianteMateriaYFecha", key = "{#estudianteCi, #materiaCodigoUnico, #fechaInscripcion}")
    public InscritoDTO obtenerInscripcionPorEstudianteMateriaYFecha(String estudianteCi, String materiaCodigoUnico, LocalDate fechaInscripcion) {
        Inscrito inscrito = inscritoRepository.findByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcion(estudianteCi, materiaCodigoUnico, fechaInscripcion)
                .orElseThrow(() -> new BusinessException("Inscripción no encontrada para el estudiante " + estudianteCi + ", materia " + materiaCodigoUnico + " y fecha " + fechaInscripcion));
        return convertToDTO(inscrito);
    }

    @Override
    @CachePut(value = "inscripcion", key = "#result.idInscrito")
    @CacheEvict(value = {"inscripciones", "inscripcionesPorEstudiante", "inscripcionesPorMateria"}, allEntries = true)
    @Transactional
    public InscritoDTO crearInscripcion(InscritoDTO inscritoDTO) {
        // Validar primero las reglas de negocio
        // inscritoValidator.validarCreacion(inscritoDTO); // Descomentar si usas un validador

        Estudiante estudiante = estudianteRepository.findByCi(inscritoDTO.getEstudianteCi())
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + inscritoDTO.getEstudianteCi() + " no encontrado."));
        Materia materia = materiaRepository.findByCodigoUnico(inscritoDTO.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código único " + inscritoDTO.getMateriaCodigoUnico() + " no encontrada."));

        // Regla: no permitir inscripciones a la misma materia en la misma fecha para el mismo estudiante
        if (inscritoRepository.findByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcion(
                inscritoDTO.getEstudianteCi(), inscritoDTO.getMateriaCodigoUnico(), inscritoDTO.getFechaInscripcion()).isPresent()) {
            throw new BusinessException("El estudiante ya está inscrito en esta materia en la fecha especificada.");
        }

        // Regla: no permitir inscripciones a la misma materia antes de 6 meses
        // Comentar si no aplica esta regla
        // if (inscritoRepository.existsByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcionAfter(
        //         inscritoDTO.getEstudianteCi(), inscritoDTO.getMateriaCodigoUnico(), inscritoDTO.getFechaInscripcion().minusMonths(6))) {
        //     throw new BusinessException("No se puede inscribir la misma materia antes de 6 meses.");
        // }


        Inscrito inscrito = convertToEntity(inscritoDTO, estudiante, materia);
        Inscrito savedInscrito = inscritoRepository.save(inscrito);
        return convertToDTO(savedInscrito);
    }

    @Override
    @CachePut(value = "inscripcion", key = "#idInscrito")
    @CacheEvict(value = {"inscripciones", "inscripcionesPorEstudiante", "inscripcionesPorMateria"}, allEntries = true)
    @Transactional
    public InscritoDTO actualizarInscripcion(Long idInscrito, InscritoDTO inscritoDTO) {
        Inscrito existingInscrito = inscritoRepository.findById(idInscrito)
                .orElseThrow(() -> new BusinessException("Inscripción con ID " + idInscrito + " no encontrada para actualizar."));

        // Si se cambia la CI o el Código Único de la materia, o la fecha, validamos las referencias
        Estudiante estudiante = estudianteRepository.findByCi(inscritoDTO.getEstudianteCi())
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + inscritoDTO.getEstudianteCi() + " no encontrado."));
        Materia materia = materiaRepository.findByCodigoUnico(inscritoDTO.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código único " + inscritoDTO.getMateriaCodigoUnico() + " no encontrada."));

        // Validar si la combinación CI, CodigoMateria, FechaInscripcion ya existe para otra inscripción (excluyendo la actual)
        if (!existingInscrito.getEstudiante().getCi().equals(inscritoDTO.getEstudianteCi()) ||
            !existingInscrito.getMateria().getCodigoUnico().equals(inscritoDTO.getMateriaCodigoUnico()) ||
            !existingInscrito.getFechaInscripcion().equals(inscritoDTO.getFechaInscripcion())) {

            if (inscritoRepository.findByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcion(
                inscritoDTO.getEstudianteCi(), inscritoDTO.getMateriaCodigoUnico(), inscritoDTO.getFechaInscripcion()).isPresent()) {
                throw new BusinessException("Ya existe una inscripción con la misma combinación de estudiante, materia y fecha.");
            }
        }
        
        existingInscrito.setEstudiante(estudiante);
        existingInscrito.setMateria(materia);
        existingInscrito.setFechaInscripcion(inscritoDTO.getFechaInscripcion());

        Inscrito updatedInscrito = inscritoRepository.save(existingInscrito);
        return convertToDTO(updatedInscrito);
    }

    @Override
    @CacheEvict(value = {"inscripcion", "inscripciones", "inscripcionesPorEstudiante", "inscripcionesPorMateria"}, allEntries = true)
    @Transactional
    public void eliminarInscripcion(Long idInscrito) {
        if (!inscritoRepository.existsById(idInscrito)) {
            throw new BusinessException("Inscripción con ID " + idInscrito + " no encontrada para eliminar.");
        }
        inscritoRepository.deleteById(idInscrito);
    }

    @Override
    @Transactional
    public Inscrito obtenerInscripcionConBloqueo(Long idInscrito) {
        Inscrito inscripcion = inscritoRepository.findById(idInscrito)
                .orElseThrow(() -> new BusinessException("Inscripción con ID " + idInscrito + " no encontrada."));
        try {
            Thread.sleep(15000); // Simulate long operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("La operación de bloqueo fue interrumpida.");
        }
        return inscripcion;
    }

    // *******************************************************************
    // ****** AGREGAR ESTE NUEVO MÉTODO EN InscritoServiceImpl.java ******
    // *******************************************************************
    @Override
    @Transactional // Es crucial para operaciones de escritura (DELETE)
    @CacheEvict(value = {"inscripcion", "inscripciones", "inscripcionesPorEstudiante", "inscripcionesPorMateria", "inscripcionPorEstudianteMateriaYFecha"}, allEntries = true) // Invalida cachés relevantes
    public void eliminarInscripcionPorEstudianteYMateria(String estudianteCi, String materiaCodigoUnico) {
        Optional<Inscrito> inscritoOptional = inscritoRepository.findByEstudianteCiAndMateriaCodigoUnico(estudianteCi, materiaCodigoUnico);

        if (inscritoOptional.isEmpty()) {
            throw new BusinessException("Inscripción no encontrada para el estudiante CI: " + estudianteCi + " y materia código único: " + materiaCodigoUnico);
        }

        Inscrito inscrito = inscritoOptional.get();
        inscritoRepository.delete(inscrito);
    }


    // --- Métodos de Conversión DTO <-> Entidad ---

    private InscritoDTO convertToDTO(Inscrito inscrito) {
        if (inscrito == null) {
            return null;
        }
        return InscritoDTO.builder()
                .idInscrito(inscrito.getIdInscrito())
                .estudianteCi(inscrito.getEstudiante().getCi())
                .materiaCodigoUnico(inscrito.getMateria().getCodigoUnico())
                .fechaInscripcion(inscrito.getFechaInscripcion())
                .build();
    }

    private Inscrito convertToEntity(InscritoDTO inscritoDTO, Estudiante estudiante, Materia materia) {
        if (inscritoDTO == null) {
            return null;
        }
        return Inscrito.builder()
                .idInscrito(inscritoDTO.getIdInscrito())
                .estudiante(estudiante)
                .materia(materia)
                .fechaInscripcion(inscritoDTO.getFechaInscripcion())
                .build();
    }
}