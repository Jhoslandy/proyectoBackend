// Archivo: src/main/java/com/example/ProyectoTaw/service/impl/MateriaServiceImpl.java

package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.MateriaDTO;
import com.example.ProyectoTaw.model.Materia;
import com.example.ProyectoTaw.repository.MateriaRepository;
import com.example.ProyectoTaw.service.IMateriaService;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta ruta sea correcta

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaServiceImpl implements IMateriaService {

    private final MateriaRepository materiaRepository;

    @Autowired
    public MateriaServiceImpl(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    @Override
    @Cacheable(value = "materias")
    public List<MateriaDTO> obtenerTodasLasMaterias() {
        return materiaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "materia", key = "#id")
    public MateriaDTO obtenerMateriaPorId(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Materia con ID " + id + " no encontrada"));
        return convertToDTO(materia);
    }

    @Override
    @Cacheable(value = "materias", key = "#query")
    public List<MateriaDTO> buscarMaterias(String query) {
        return materiaRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(query, query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "materia", key = "#result.id")
    @CacheEvict(value = {"materias"}, allEntries = true)
    @Transactional
    public MateriaDTO crearMateria(MateriaDTO materiaDTO) {
        if (materiaRepository.existsByCodigoUnico(materiaDTO.getCodigoUnico())) {
            throw new BusinessException("Ya existe una materia con el código único: " + materiaDTO.getCodigoUnico());
        }

        Materia materia = convertToEntity(materiaDTO);
        Materia materiaGuardada = materiaRepository.save(materia);
        return convertToDTO(materiaGuardada);
    }

    @Override
    @CachePut(value = "materia", key = "#id")
    @CacheEvict(value = {"materias"}, allEntries = true)
    @Transactional
    public MateriaDTO actualizarMateria(Long id, MateriaDTO materiaDTO) {
        Materia materiaExistente = materiaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Materia con ID " + id + " no encontrada para actualizar"));

        if (!materiaExistente.getCodigoUnico().equals(materiaDTO.getCodigoUnico()) && materiaRepository.existsByCodigoUnico(materiaDTO.getCodigoUnico())) {
            throw new BusinessException("El código único '" + materiaDTO.getCodigoUnico() + "' ya está registrado para otra materia.");
        }

        materiaExistente.setNombre(materiaDTO.getNombreMateria()); // Ya corregido
        materiaExistente.setCodigoUnico(materiaDTO.getCodigoUnico());
        materiaExistente.setDescripcion(materiaDTO.getDescripcion());

        Materia materiaActualizada = materiaRepository.save(materiaExistente);
        return convertToDTO(materiaActualizada);
    }

    @Override
    @CacheEvict(value = {"materia", "materias"}, allEntries = true)
    @Transactional
    public void eliminarMateria(Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new BusinessException("Materia con ID " + id + " no encontrada para eliminar");
        }
        materiaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Materia obtenerMateriaConBloqueo(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Materia con ID " + id + " no encontrada"));
        try {
            Thread.sleep(15000); // Simulate long operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("La operación de bloqueo fue interrumpida.");
        }
        return materia;
    }

    // *******************************************************************
    // ****** AGREGAR/VERIFICAR ESTE MÉTODO EN MateriaServiceImpl.java ******
    // *******************************************************************
    @Override
    @Cacheable(value = "materiaPorCodigoUnico", key = "#codigoUnico")
    public MateriaDTO obtenerMateriaPorCodigoUnico(String codigoUnico) {
        Materia materia = materiaRepository.findByCodigoUnico(codigoUnico)
                .orElseThrow(() -> new BusinessException("Materia con código único " + codigoUnico + " no encontrada."));
        return convertToDTO(materia);
    }

    // --- Métodos de Conversión DTO <-> Entidad ---
    private MateriaDTO convertToDTO(Materia materia) {
        if (materia == null) {
            return null;
        }
        return MateriaDTO.builder()
                .id(materia.getId())
                .nombreMateria(materia.getNombre()) // Ya corregido
                .codigoUnico(materia.getCodigoUnico())
                .descripcion(materia.getDescripcion())
                .build();
    }

    private Materia convertToEntity(MateriaDTO materiaDTO) {
        if (materiaDTO == null) {
            return null;
        }
        return Materia.builder()
                .id(materiaDTO.getId())
                .nombre(materiaDTO.getNombreMateria()) // Ya corregido
                .codigoUnico(materiaDTO.getCodigoUnico())
                .descripcion(materiaDTO.getDescripcion())
                .build();
    }
}