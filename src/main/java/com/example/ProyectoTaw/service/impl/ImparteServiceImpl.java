package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.ImparteDTO;
import com.example.ProyectoTaw.model.Materia; // Necesario para buscar Materia
import com.example.ProyectoTaw.model.Docente;     // Necesario para buscar Docente
import com.example.ProyectoTaw.model.Imparte;
import com.example.ProyectoTaw.repository.ImparteRepository;
import com.example.ProyectoTaw.repository.MateriaRepository; // Asume que tienes este repositorio
import com.example.ProyectoTaw.repository.DocenteRepository;     // Ya lo tienes
import com.example.ProyectoTaw.service.IImparteService;
import com.example.ProyectoTaw.validator.ImparteValidator; // Asumimos que tendrás un validador para Imparte
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta clase exista

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImparteServiceImpl implements IImparteService {

    private final ImparteRepository imparteRepository;
    private final MateriaRepository materiaRepository; // Para buscar materias
    private final DocenteRepository docenteRepository;     // Para buscar Docentes
    private final ImparteValidator imparteValidator;         // Tu validador para Imparte

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
    @Cacheable(value = "relacionesImparte")
    public List<ImparteDTO> obtenerTodasLasRelaciones() {
        return imparteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "relacionImparte", key = "#idImparte")
    public ImparteDTO obtenerRelacionPorId(Long idImparte) {
        Imparte relacion = imparteRepository.findById(idImparte)
                .orElseThrow(() -> new BusinessException("Relación Materia-Docente con ID " + idImparte + " no encontrada"));
        return convertToDTO(relacion);
    }

    @Override
    @Cacheable(value = "relacionesPorMateria", key = "#materiaCodigoUnico")
    public List<ImparteDTO> obtenerRelacionesPorMateria(String materiaCodigoUnico) {
        return imparteRepository.findByMateriaCodigoUnico(materiaCodigoUnico).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "relacionesPorDocente", key = "#ciDocente")
    public List<ImparteDTO> obtenerRelacionesPorDocente(String ciDocente) {
        return imparteRepository.findByCiDocente(ciDocente).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "relacionPorMateriaYDocente", key = "{#materiaCodigoUnico, #ciDocente}")
    public ImparteDTO obtenerRelacionPorMateriaYDocente(String materiaCodigoUnico, String ciDocente) {
        Imparte relacion = imparteRepository.findByMateriaCodigoUnicoAndciDocente(materiaCodigoUnico, ciDocente)
                .orElseThrow(() -> new BusinessException("Relación entre materia '" + materiaCodigoUnico +
                                                       "' y Docente ci " + ciDocente + " no encontrada."));
        return convertToDTO(relacion);
    }
    
    @Override
    @CacheEvict(value = {"relacionesImparte", "relacionesPorMateria", "relacionesPorDocente", "relacionPorMateriaYDocente"}, allEntries = true)
    @Transactional
    public ImparteDTO crearRelacion(ImparteDTO imparteDTO) {
        // Validar que la Materia y el Docente existan
        Materia materia = materiaRepository.findByCodigoUnico(imparteDTO.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código único '" + imparteDTO.getMateriaCodigoUnico() + "' no encontrada."));

        Docente docente = docenteRepository.findByCi(imparteDTO.getCiDocente())
                .orElseThrow(() -> new BusinessException("Docente con ID " + imparteDTO.getCiDocente() + " no encontrado."));

        // Validar unicidad antes de crear (por la UniqueConstraint en la entidad)
        if (imparteRepository.existsByMateriaCodigoUnicoAndCiDocente(
                imparteDTO.getMateriaCodigoUnico(), imparteDTO.getCiDocente())) {
            throw new BusinessException("Ya existe una relación entre la materia '" + imparteDTO.getMateriaCodigoUnico() +
                                        "' y el Docente ID " + imparteDTO.getCiDocente() + ".");
        }

        imparteValidator.validarCreacionRelacion(imparteDTO); // Asume que este método valida el DTO

        Imparte relacion = convertToEntity(imparteDTO, materia, docente);
        Imparte relacionGuardada = imparteRepository.save(relacion);
        return convertToDTO(relacionGuardada);
    }

    @Override
    @CachePut(value = "relacionImparte", key = "#idImparte")
    @CacheEvict(value = {"relacionesImparte", "relacionesPorMateria", "relacionesPorDocente", "relacionPorMateriaYDocente"}, allEntries = true)
    @Transactional
    public ImparteDTO actualizarRelacion(Long idImparte, ImparteDTO imparteDTO) {
        Imparte relacionExistente = imparteRepository.findById(idImparte)
                .orElseThrow(() -> new BusinessException("Relación Materia-Docente con ID " + idImparte + " no encontrada para actualizar"));

        // Validar que la Materia y el Docente existan si se intenta cambiar
        Materia nuevaMateria = materiaRepository.findByCodigoUnico(imparteDTO.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código único '" + imparteDTO.getMateriaCodigoUnico() + "' no encontrada."));

        Docente nuevoDocente = docenteRepository.findByCi(imparteDTO.getCiDocente())
                .orElseThrow(() -> new BusinessException("Docente con ID " + imparteDTO.getCiDocente() + " no encontrado."));

        // Validar unicidad si se cambian los campos que formaban la clave natural
        if (!relacionExistente.getMateria().getCodigoUnico().equals(imparteDTO.getMateriaCodigoUnico()) ||
            !relacionExistente.getDocente().getCiDocente().equals(imparteDTO.getCiDocente())) {

            if (imparteRepository.existsByMateriaCodigoUnicoAndCiDocente(
                imparteDTO.getMateriaCodigoUnico(), imparteDTO.getCiDocente())) {
                throw new BusinessException("Ya existe una relación entre la materia '" + imparteDTO.getMateriaCodigoUnico() +
                                            "' y el Docente ID " + imparteDTO.getCiDocente() + ".");
            }
        }

        imparteValidator.validarActualizacionRelacion(imparteDTO, relacionExistente); // Asume validación

        // Actualizar campos de la entidad
        relacionExistente.setMateria(nuevaMateria);
        relacionExistente.setDocente(nuevoDocente);

        Imparte relacionActualizada = imparteRepository.save(relacionExistente);
        return convertToDTO(relacionActualizada);
    }

    @Override
    @CacheEvict(value = {"relacionImparte", "relacionesImparte", "relacionesPorMateria", "relacionesPorDocente", "relacionPorMateriaYDocente"}, allEntries = true)
    @Transactional
    public void eliminarRelacion(Long idImparte) {
        if (!imparteRepository.existsById(idImparte)) {
            throw new BusinessException("Relación Materia-Docente con ID " + idImparte + " no encontrada para eliminar");
        }
        imparteRepository.deleteById(idImparte);
    }

    @Override
    @Transactional
    public Imparte obtenerRelacionConBloqueo(Long idImparte) {
        Imparte relacion = imparteRepository.findById(idImparte)
                .orElseThrow(() -> new BusinessException("Relación Materia-Docente con ID " + idImparte + " no encontrada."));

        // Simula un proceso largo que mantiene el bloqueo
        try {
            Thread.sleep(15000); // 15 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("La operación de bloqueo fue interrumpida.");
        }
        return relacion;
    }

    // --- Métodos de Conversión DTO <-> Entidad ---

    // Convierte una entidad Imparte a un ImparteDTO
    private ImparteDTO convertToDTO(Imparte imparte) {
        if (imparte == null) {
            return null;
        }
        return ImparteDTO.builder()
                .idImparte(imparte.getIdImparte())
                .materiaCodigoUnico(imparte.getMateria().getCodigoUnico())
                .ciDocente(imparte.getDocente().getCiDocente())
                .build();
    }

    // Convierte un ImparteDTO a una entidad Imparte
    // Necesita las entidades Materia y Docente para establecer las relaciones ManyToOne
    private Imparte convertToEntity(ImparteDTO imparteDTO, Materia materia, Docente docente) {
        if (imparteDTO == null) {
            return null;
        }
        return Imparte.builder()
                .idImparte(imparteDTO.getIdImparte()) // Puede ser nulo en la creación
                .materia(materia) // Establece la relación ManyToOne con Materia
                .Docente(docente) // Establece la relación ManyToOne con Docente
                .build();
    }
}
