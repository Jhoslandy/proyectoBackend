package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.CursoDTO;
import com.example.ProyectoTaw.model.Curso;
import com.example.ProyectoTaw.repository.CursoRepository;
import com.example.ProyectoTaw.service.ICursoService;
import com.example.ProyectoTaw.validator.CursoValidator;
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
public class CursoServiceImpl implements ICursoService {

    private final CursoRepository cursoRepository;
    private final CursoValidator cursoValidator;

    @Autowired
    public CursoServiceImpl(CursoRepository cursoRepository, CursoValidator cursoValidator) {
        this.cursoRepository = cursoRepository;
        this.cursoValidator = cursoValidator;
    }

    @Override
    @Cacheable(value = "cursos")
    public List<CursoDTO> obtenerTodosLosCursos() {
        return cursoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "curso", key = "#idCurso")
    public CursoDTO obtenerCursoPorId(Integer idCurso) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new BusinessException("Curso con ID " + idCurso + " no encontrado"));
        return convertToDTO(curso);
    }

    @Override
    @Cacheable(value = "cursos", key = "'semestre-' + #semestre")
    public List<CursoDTO> buscarCursosPorSemestre(String semestre) {
        return cursoRepository.findBySemestre(semestre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "cursos", key = "'anio-' + #anio")
    public List<CursoDTO> buscarCursosPorAnio(Integer anio) {
        return cursoRepository.findByAnio(anio).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "cursos", key = "'dia-' + #dia")
    public List<CursoDTO> buscarCursosPorDia(String dia) {
        return cursoRepository.findByDiaIgnoreCase(dia).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "curso", key = "#result.idCurso")
    @CacheEvict(value = {"cursos"}, allEntries = true)
    @Transactional
    public CursoDTO crearCurso(CursoDTO cursoDTO) {
        // Validation for format handled by @Pattern in DTO
        // Uniqueness check for dia and horario (both Strings now)
        if (cursoRepository.existsByDiaAndHorario(cursoDTO.getDia(), cursoDTO.getHorario())) {
            throw new BusinessException("Ya existe un curso programado para el " + cursoDTO.getDia() + " a las " + cursoDTO.getHorario());
        }

        cursoValidator.validacionCompletaCurso(cursoDTO); // This will call validaDiaHorarioUnico with String

        Curso curso = convertToEntity(cursoDTO);
        Curso cursoGuardado = cursoRepository.save(curso);
        return convertToDTO(cursoGuardado);
    }

    @Override
    @CachePut(value = "curso", key = "#idCurso")
    @CacheEvict(value = {"cursos"}, allEntries = true)
    @Transactional
    public CursoDTO actualizarCurso(Integer idCurso, CursoDTO cursoDTO) {
        Curso cursoExistente = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new BusinessException("Curso con ID " + idCurso + " no encontrado para actualizar"));

        // Validate if day and horario combination, if changed, doesn't exist for another course
        if (!cursoExistente.getDia().equalsIgnoreCase(cursoDTO.getDia()) || !cursoExistente.getHorario().equals(cursoDTO.getHorario())) {
             if (cursoRepository.existsByDiaAndHorario(cursoDTO.getDia(), cursoDTO.getHorario())) {
                 throw new BusinessException("Ya existe otro curso con el mismo día y horario: " + cursoDTO.getDia() + " " + cursoDTO.getHorario());
             }
        }

        cursoValidator.validarActualizacionCurso(cursoDTO, cursoExistente); // This will call validaDiaHorarioUnico with String

        cursoExistente.setDia(cursoDTO.getDia()); // Set the String value directly
        cursoExistente.setHorario(cursoDTO.getHorario()); // Set the String value directly
        cursoExistente.setSemestre(cursoDTO.getSemestre());
        cursoExistente.setAnio(cursoDTO.getAnio());

        Curso cursoActualizado = cursoRepository.save(cursoExistente);
        return convertToDTO(cursoActualizado);
    }

    @Override
    @CacheEvict(value = {"curso", "cursos"}, allEntries = true)
    @Transactional
    public void eliminarCurso(Integer idCurso) {
        if (!cursoRepository.existsById(idCurso)) {
            throw new BusinessException("Curso con ID " + idCurso + " no encontrado para eliminar");
        }
        cursoRepository.deleteById(idCurso);
    }

    @Override
    @Transactional
    public Curso obtenerCursoConBloqueo(Integer idCurso) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new BusinessException("Curso con ID " + idCurso + " no encontrado"));

        try {
            // This is a simulated delay for pessimistic locking demonstration
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("La operación de bloqueo fue interrumpida.");
        }
        return curso;
    }

    // --- Métodos de Conversión DTO <-> Entidad ---

    private CursoDTO convertToDTO(Curso curso) {
        return CursoDTO.builder()
                .idCurso(curso.getIdCurso())
                .dia(curso.getDia())
                .horario(curso.getHorario()) // Directly use the String
                .semestre(curso.getSemestre())
                .anio(curso.getAnio())
                .build();
    }

    private Curso convertToEntity(CursoDTO cursoDTO) {
        if (cursoDTO == null) {
            return null;
        }
        return Curso.builder()
                .idCurso(cursoDTO.getIdCurso())
                .dia(cursoDTO.getDia())
                .horario(cursoDTO.getHorario()) // Directly use the String
                .semestre(cursoDTO.getSemestre())
                .anio(cursoDTO.getAnio())
                .build();
    }
}