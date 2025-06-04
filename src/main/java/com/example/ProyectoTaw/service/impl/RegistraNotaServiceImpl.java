package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.RegistraNotaDTO;
import com.example.ProyectoTaw.model.Curso;
import com.example.ProyectoTaw.model.Estudiante;
import com.example.ProyectoTaw.model.RegistraNota;
import com.example.ProyectoTaw.repository.RegistraNotaRepository;
import com.example.ProyectoTaw.service.IRegistraNotaService;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;
import com.example.ProyectoTaw.validator.RegistraNotaValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistraNotaServiceImpl implements IRegistraNotaService {

    private final RegistraNotaRepository registraNotaRepository;
    private final RegistraNotaValidator registraNotaValidator;


    @Autowired
    public RegistraNotaServiceImpl(RegistraNotaRepository registraNotaRepository, RegistraNotaValidator registraNotaValidator) {
        this.registraNotaRepository = registraNotaRepository;
        this.registraNotaValidator = registraNotaValidator;
    }

    @Override
    @Cacheable(value = "registrosNotas")
    public List<RegistraNotaDTO> listarNotas() {
        return registraNotaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "registroNota", key = "#id")
    public RegistraNotaDTO obtenerNotaPorId(Long id) {
        RegistraNota registro = registraNotaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Registro de nota con ID " + id + " no encontrado"));
        return convertToDTO(registro);
    }

    @Override
    @CachePut(value = "registroNota", key = "#result.id")
    @CacheEvict(value = {"registrosNotas"}, allEntries = true)
    public RegistraNotaDTO crearNota(RegistraNotaDTO dto) {
        registraNotaValidator.validacionCompletaNota(dto);
        RegistraNota registro = convertToEntity(dto);
        return convertToDTO(registraNotaRepository.save(registro));
    }

    @Override
    @CachePut(value = "registroNota", key = "#id")
    @CacheEvict(value = {"registrosNotas"}, allEntries = true)
    public RegistraNotaDTO actualizarNota(Long id, RegistraNotaDTO dto) {
        RegistraNota existente = registraNotaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Registro de nota con ID " + id + " no encontrado"));

        registraNotaValidator.validarActualizacionNota(dto, existente);

        Estudiante estudiante = new Estudiante();
        estudiante.setCi(dto.getEstudianteId());

        Curso curso = new Curso();
        curso.setIdCurso(dto.getCursoId());

        existente.setEstudiante(estudiante);
        existente.setCurso(curso);
        existente.setEvaluacion(dto.getEvaluacion());
        existente.setNota(dto.getNota());
        existente.setFecha(dto.getFecha());

        return convertToDTO(registraNotaRepository.save(existente));
    }

    @Override
    @CacheEvict(value = {"registroNota", "registrosNotas"}, allEntries = true)
    public void eliminarNota(Long id) {
        if (!registraNotaRepository.existsById(id)) {
            throw new BusinessException("Registro de nota con ID " + id + " no encontrado para eliminar");
        }
        registraNotaRepository.deleteById(id);
    }

    @Transactional
    public RegistraNota obtenerRegistroConBloqueo(Long id) {
        RegistraNota registro = registraNotaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Registro de nota con ID " + id + " no encontrado"));
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return registro;
    }

    private RegistraNotaDTO convertToDTO(RegistraNota r) {
        if (r == null) return null;
        return RegistraNotaDTO.builder()
                .id(r.getId())
                .estudianteId(r.getEstudiante().getCi())
                .cursoId(r.getCurso().getIdCurso())
                .evaluacion(r.getEvaluacion())
                .nota(r.getNota())
                .fecha(r.getFecha())
                .build();
    }

    private RegistraNota convertToEntity(RegistraNotaDTO dto) {
        if (dto == null) return null;

        Estudiante estudiante = new Estudiante();
        estudiante.setCi(dto.getEstudianteId());

        Curso curso = new Curso();
        curso.setIdCurso(dto.getCursoId());

        return RegistraNota.builder()
                .id(dto.getId())
                .estudiante(estudiante)
                .curso(curso)
                .evaluacion(dto.getEvaluacion())
                .nota(dto.getNota())
                .fecha(dto.getFecha())
                .build();
    }
}
