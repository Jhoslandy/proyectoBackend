package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.SeDaDTO;
import com.example.ProyectoTaw.model.Materia; // Necesario para buscar Materia
import com.example.ProyectoTaw.model.Curso;     // Necesario para buscar Curso
import com.example.ProyectoTaw.model.SeDa;
import com.example.ProyectoTaw.repository.SeDaRepository;
import com.example.ProyectoTaw.repository.MateriaRepository; // Asume que tienes este repositorio
import com.example.ProyectoTaw.repository.CursoRepository;     // Ya lo tienes
import com.example.ProyectoTaw.service.ISeDaService;
import com.example.ProyectoTaw.validator.SeDaValidator; // Asumimos que tendrás un validador para SeDa
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
public class SeDaServiceImpl implements ISeDaService {

    private final SeDaRepository seDaRepository;
    private final MateriaRepository materiaRepository; // Para buscar materias
    private final CursoRepository cursoRepository;     // Para buscar cursos
    private final SeDaValidator seDaValidator;         // Tu validador para SeDa

    @Autowired
    public SeDaServiceImpl(SeDaRepository seDaRepository,
                           MateriaRepository materiaRepository,
                           CursoRepository cursoRepository,
                           SeDaValidator seDaValidator) {
        this.seDaRepository = seDaRepository;
        this.materiaRepository = materiaRepository;
        this.cursoRepository = cursoRepository;
        this.seDaValidator = seDaValidator;
    }

    @Override
    @Cacheable(value = "relacionesSeDa")
    public List<SeDaDTO> obtenerTodasLasRelaciones() {
        return seDaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "relacionSeDa", key = "#idSeDa")
    public SeDaDTO obtenerRelacionPorId(Long idSeDa) {
        SeDa relacion = seDaRepository.findById(idSeDa)
                .orElseThrow(() -> new BusinessException("Relación Materia-Curso con ID " + idSeDa + " no encontrada"));
        return convertToDTO(relacion);
    }

    @Override
    @Cacheable(value = "relacionesPorMateria", key = "#materiaCodigoUnico")
    public List<SeDaDTO> obtenerRelacionesPorMateria(String materiaCodigoUnico) {
        return seDaRepository.findByMateriaCodigoUnico(materiaCodigoUnico).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "relacionesPorCurso", key = "#cursoIdCurso")
    public List<SeDaDTO> obtenerRelacionesPorCurso(Integer cursoIdCurso) {
        return seDaRepository.findByCursoIdCurso(cursoIdCurso).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "relacionPorMateriaYCurso", key = "{#materiaCodigoUnico, #cursoIdCurso}")
    public SeDaDTO obtenerRelacionPorMateriaYCurso(String materiaCodigoUnico, Integer cursoIdCurso) {
        SeDa relacion = seDaRepository.findByMateriaCodigoUnicoAndCursoIdCurso(materiaCodigoUnico, cursoIdCurso)
                .orElseThrow(() -> new BusinessException("Relación entre materia '" + materiaCodigoUnico +
                                                       "' y curso ID " + cursoIdCurso + " no encontrada."));
        return convertToDTO(relacion);
    }

    @Override
    @CacheEvict(value = {"relacionesSeDa", "relacionesPorMateria", "relacionesPorCurso", "relacionPorMateriaYCurso"}, allEntries = true)
    @Transactional
    public SeDaDTO crearRelacion(SeDaDTO seDaDTO) {
        // Validar que la Materia y el Curso existan
        Materia materia = materiaRepository.findByCodigoUnico(seDaDTO.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código único '" + seDaDTO.getMateriaCodigoUnico() + "' no encontrada."));

        Curso curso = cursoRepository.findById(seDaDTO.getCursoIdCurso())
                .orElseThrow(() -> new BusinessException("Curso con ID " + seDaDTO.getCursoIdCurso() + " no encontrado."));

        // Validar unicidad antes de crear (por la UniqueConstraint en la entidad)
        if (seDaRepository.existsByMateriaCodigoUnicoAndCursoIdCurso(
                seDaDTO.getMateriaCodigoUnico(), seDaDTO.getCursoIdCurso())) {
            throw new BusinessException("Ya existe una relación entre la materia '" + seDaDTO.getMateriaCodigoUnico() +
                                        "' y el curso ID " + seDaDTO.getCursoIdCurso() + ".");
        }

        seDaValidator.validarCreacionRelacion(seDaDTO); // Asume que este método valida el DTO

        SeDa relacion = convertToEntity(seDaDTO, materia, curso);
        SeDa relacionGuardada = seDaRepository.save(relacion);
        return convertToDTO(relacionGuardada);
    }

    @Override
    @CachePut(value = "relacionSeDa", key = "#idSeDa")
    @CacheEvict(value = {"relacionesSeDa", "relacionesPorMateria", "relacionesPorCurso", "relacionPorMateriaYCurso"}, allEntries = true)
    @Transactional
    public SeDaDTO actualizarRelacion(Long idSeDa, SeDaDTO seDaDTO) {
        SeDa relacionExistente = seDaRepository.findById(idSeDa)
                .orElseThrow(() -> new BusinessException("Relación Materia-Curso con ID " + idSeDa + " no encontrada para actualizar"));

        // Validar que la Materia y el Curso existan si se intenta cambiar
        Materia nuevaMateria = materiaRepository.findByCodigoUnico(seDaDTO.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código único '" + seDaDTO.getMateriaCodigoUnico() + "' no encontrada."));

        Curso nuevoCurso = cursoRepository.findById(seDaDTO.getCursoIdCurso())
                .orElseThrow(() -> new BusinessException("Curso con ID " + seDaDTO.getCursoIdCurso() + " no encontrado."));

        // Validar unicidad si se cambian los campos que formaban la clave natural
        if (!relacionExistente.getMateria().getCodigoUnico().equals(seDaDTO.getMateriaCodigoUnico()) ||
            !relacionExistente.getCurso().getIdCurso().equals(seDaDTO.getCursoIdCurso())) {

            if (seDaRepository.existsByMateriaCodigoUnicoAndCursoIdCurso(
                seDaDTO.getMateriaCodigoUnico(), seDaDTO.getCursoIdCurso())) {
                throw new BusinessException("Ya existe una relación entre la materia '" + seDaDTO.getMateriaCodigoUnico() +
                                            "' y el curso ID " + seDaDTO.getCursoIdCurso() + ".");
            }
        }

        seDaValidator.validarActualizacionRelacion(seDaDTO, relacionExistente); // Asume validación

        // Actualizar campos de la entidad
        relacionExistente.setMateria(nuevaMateria);
        relacionExistente.setCurso(nuevoCurso);

        SeDa relacionActualizada = seDaRepository.save(relacionExistente);
        return convertToDTO(relacionActualizada);
    }

    @Override
    @CacheEvict(value = {"relacionSeDa", "relacionesSeDa", "relacionesPorMateria", "relacionesPorCurso", "relacionPorMateriaYCurso"}, allEntries = true)
    @Transactional
    public void eliminarRelacion(Long idSeDa) {
        if (!seDaRepository.existsById(idSeDa)) {
            throw new BusinessException("Relación Materia-Curso con ID " + idSeDa + " no encontrada para eliminar");
        }
        seDaRepository.deleteById(idSeDa);
    }

    @Override
    @Transactional
    public SeDa obtenerRelacionConBloqueo(Long idSeDa) {
        SeDa relacion = seDaRepository.findById(idSeDa)
                .orElseThrow(() -> new BusinessException("Relación Materia-Curso con ID " + idSeDa + " no encontrada."));

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

    // Convierte una entidad SeDa a un SeDaDTO
    private SeDaDTO convertToDTO(SeDa seDa) {
        if (seDa == null) {
            return null;
        }
        return SeDaDTO.builder()
                .idSeDa(seDa.getIdSeDa())
                .materiaCodigoUnico(seDa.getMateria().getCodigoUnico())
                .cursoIdCurso(seDa.getCurso().getIdCurso())
                .build();
    }

    // Convierte un SeDaDTO a una entidad SeDa
    // Necesita las entidades Materia y Curso para establecer las relaciones ManyToOne
    private SeDa convertToEntity(SeDaDTO seDaDTO, Materia materia, Curso curso) {
        if (seDaDTO == null) {
            return null;
        }
        return SeDa.builder()
                .idSeDa(seDaDTO.getIdSeDa()) // Puede ser nulo en la creación
                .materia(materia)
                .curso(curso)
                .build();
    }
}