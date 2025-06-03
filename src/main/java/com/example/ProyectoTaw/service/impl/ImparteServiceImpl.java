package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.ImparteDTO;
import com.example.ProyectoTaw.model.Imparte;
import com.example.ProyectoTaw.model.Materia;
import com.example.ProyectoTaw.model.Docente;
import com.example.ProyectoTaw.repository.ImparteRepository;
import com.example.ProyectoTaw.repository.MateriaRepository;
import com.example.ProyectoTaw.repository.DocenteRepository;
import com.example.ProyectoTaw.service.IImparteService;
import com.example.ProyectoTaw.validator.ImparteValidator;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de IImparteService.
 * Gestiona la lógica de negocio para las relaciones Materia-Docente (Imparte).
 */
@Service
public class ImparteServiceImpl implements IImparteService {

    private final ImparteRepository imparteRepository;
    private final MateriaRepository materiaRepository;
    private final DocenteRepository docenteRepository;
    private final ImparteValidator imparteValidator;

    @Autowired
    public ImparteServiceImpl(ImparteRepository imparteRepository,
                              MateriaRepository materiaRepository,
                              DocenteRepository docenteRepository,
                              ImparteValidator imparteValidator) {
        this.imparteRepository = imparteRepository;
        this.materiaRepository = materiaRepository;
        this.docenteRepository = docenteRepository;
        this.imparteValidator = imparteValidator;
    }

    @Override
    @Cacheable("relacionesImparte")
    public List<ImparteDTO> obtenerTodasLasRelaciones() {
        return imparteRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "relacionImparte", key = "#idImparte")
    public ImparteDTO obtenerRelacionPorId(Long idImparte) {
        Imparte imparte = imparteRepository.findById(idImparte)
                .orElseThrow(() -> new BusinessException("Relación con ID " + idImparte + " no encontrada."));
        return convertToDTO(imparte);
    }

    @Override
    @Cacheable(value = "relacionesPorMateria", key = "#materiaCodigoUnico")
    public List<ImparteDTO> obtenerRelacionesPorMateria(String materiaCodigoUnico) {
        return imparteRepository.findByMateriaCodigoUnico(materiaCodigoUnico)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "relacionesPorDocente", key = "#ciDocente")
    public List<ImparteDTO> obtenerRelacionesPorDocente(String ciDocente) {
        return imparteRepository.findByDocenteCiDocente(ciDocente)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "relacionPorMateriaYDocente", key = "{#materiaCodigoUnico, #ciDocente}")
    public ImparteDTO obtenerRelacionPorMateriaYDocente(String materiaCodigoUnico, String ciDocente) {
        Imparte imparte = imparteRepository.findByMateriaCodigoUnicoAndDocenteCiDocente(materiaCodigoUnico, ciDocente)
                .orElseThrow(() -> new BusinessException(
                        "No se encontró la relación entre materia '" + materiaCodigoUnico +
                        "' y docente '" + ciDocente + "'."));
        return convertToDTO(imparte);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"relacionesImparte", "relacionesPorMateria", "relacionesPorDocente", "relacionPorMateriaYDocente"}, allEntries = true)
    public ImparteDTO crearRelacion(ImparteDTO dto) {
        Materia materia = materiaRepository.findByCodigoUnico(dto.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código '" + dto.getMateriaCodigoUnico() + "' no encontrada."));

        Docente docente = docenteRepository.findByCiDocente(dto.getCiDocente())
                .orElseThrow(() -> new BusinessException("Docente con CI '" + dto.getCiDocente() + "' no encontrado."));

        if (imparteRepository.existsByMateriaCodigoUnicoAndDocenteCiDocente(dto.getMateriaCodigoUnico(), dto.getCiDocente())) {
            throw new BusinessException("Ya existe una relación entre esa materia y docente.");
        }

        imparteValidator.validarCreacionRelacion(dto);

        Imparte nuevaRelacion = convertToEntity(dto, materia, docente);
        Imparte guardada = imparteRepository.save(nuevaRelacion);

        return convertToDTO(guardada);
    }

    @Override
    @Transactional
    @CachePut(value = "relacionImparte", key = "#idImparte")
    @CacheEvict(value = {"relacionesImparte", "relacionesPorMateria", "relacionesPorDocente", "relacionPorMateriaYDocente"}, allEntries = true)
    public ImparteDTO actualizarRelacion(Long idImparte, ImparteDTO dto) {
        Imparte existente = imparteRepository.findById(idImparte)
                .orElseThrow(() -> new BusinessException("No se encontró la relación con ID " + idImparte + " para actualizar."));

        Materia nuevaMateria = materiaRepository.findByCodigoUnico(dto.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código '" + dto.getMateriaCodigoUnico() + "' no encontrada."));

        Docente nuevoDocente = docenteRepository.findByCiDocente(dto.getCiDocente())
                .orElseThrow(() -> new BusinessException("Docente con CI '" + dto.getCiDocente() + "' no encontrado."));

        boolean cambiaronDatos =
                !existente.getMateria().getCodigoUnico().equals(dto.getMateriaCodigoUnico()) ||
                !existente.getDocente().getCiDocente().equals(dto.getCiDocente());

        if (cambiaronDatos &&
                imparteRepository.existsByMateriaCodigoUnicoAndDocenteCiDocente(dto.getMateriaCodigoUnico(), dto.getCiDocente())) {
            throw new BusinessException("Ya existe una relación con los nuevos datos.");
        }

        imparteValidator.validarActualizacionRelacion(dto, existente);

        existente.setMateria(nuevaMateria);
        existente.setDocente(nuevoDocente);

        Imparte actualizado = imparteRepository.save(existente);
        return convertToDTO(actualizado);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"relacionImparte", "relacionesImparte", "relacionesPorMateria", "relacionesPorDocente", "relacionPorMateriaYDocente"}, allEntries = true)
    public void eliminarRelacion(Long idImparte) {
        if (!imparteRepository.existsById(idImparte)) {
            throw new BusinessException("No existe una relación con ID " + idImparte + " para eliminar.");
        }
        imparteRepository.deleteById(idImparte);
    }

    @Override
    @Transactional
    public Imparte obtenerRelacionConBloqueo(Long idImparte) {
        Imparte relacion = imparteRepository.findById(idImparte)
                .orElseThrow(() -> new BusinessException("Relación con ID " + idImparte + " no encontrada."));

        try {
            Thread.sleep(15000); // Simula proceso que mantiene el bloqueo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Operación de bloqueo interrumpida.");
        }

        return relacion;
    }

    // ------------------------
    // Métodos auxiliares
    // ------------------------

    private ImparteDTO convertToDTO(Imparte imparte) {
        return ImparteDTO.builder()
                .idImparte(imparte.getIdImparte())
                .materiaCodigoUnico(imparte.getMateria().getCodigoUnico())
                .ciDocente(imparte.getDocente().getCiDocente())
                .build();
    }

    private Imparte convertToEntity(ImparteDTO dto, Materia materia, Docente docente) {
        return Imparte.builder()
                .idImparte(dto.getIdImparte()) // nulo si es creación
                .materia(materia)
                .docente(docente)
                .build();
    }
}
