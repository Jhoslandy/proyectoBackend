package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.DocenteDTO;
import com.example.ProyectoTaw.dto.DocenteDTO;
import com.example.ProyectoTaw.model.Docente;
import com.example.ProyectoTaw.model.Docente;
import com.example.ProyectoTaw.repository.DocenteRepository;
import com.example.ProyectoTaw.repository.DocenteRepository;
import com.example.ProyectoTaw.service.IDocenteService;
import com.example.ProyectoTaw.validator.DocenteValidator;
import com.example.ProyectoTaw.validator.DocenteValidator;
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
public class DocenteServiceImpl implements IDocenteService {

    private final DocenteRepository docenteRepository;
    private final DocenteValidator docenteValidator;

    @Autowired
    public DocenteServiceImpl(DocenteRepository docenteRepository, DocenteValidator docenteValidator) {
        this.docenteRepository = docenteRepository;
        this.docenteValidator = docenteValidator;
    }

    @Override
    @Cacheable(value = "docentes")
    public List<DocenteDTO> obtenerTodosLosDocentes() {
        return docenteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "docente", key = "#ci") // Cambiado a 'ci'
    public DocenteDTO obtenerDocentePorCi(String ci) { // Cambiado a 'obtenerDocentePorCi' y tipo a String
        Docente docente = docenteRepository.findByCiDocente(ci) // Usando findByCi
                .orElseThrow(() -> new BusinessException("Docente con CI " + ci + " no encontrado"));
        return convertToDTO(docente);
    }

    @Override
    @Cacheable(value = "docentes", key = "#query")
    public List<DocenteDTO> buscarDocentes(String query) {
        return docenteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(query, query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "docente", key = "#result.ci") // Ajustado a result.ci
    @CacheEvict(value = {"docentes"}, allEntries = true)
    @Transactional // Agregado @Transactional para operaciones de escritura
    public DocenteDTO crearDocente(DocenteDTO docenteDTO) {
        // Validar si el CI ya existe antes de crear
        if (docenteRepository.existsByCiDocente(docenteDTO.getCi())) {
            throw new BusinessException("Ya existe un Docente con la CI: " + docenteDTO.getCi());
        }
        // Validar si el email ya existe antes de crear
        if (docenteRepository.existsByEmail(docenteDTO.getEmail())) {
            throw new BusinessException("Ya existe un Docente con el email: " + docenteDTO.getEmail());
        }

        docenteValidator.validacionCompletaDocente(docenteDTO); // Asume que este método también valida campos

        Docente docente = convertToEntity(docenteDTO);
        Docente docenteGuardado = docenteRepository.save(docente);
        return convertToDTO(docenteGuardado);
    }

    @Override
    @CachePut(value = "docente", key = "#ci")
    @CacheEvict(value = {"docentes"}, allEntries = true)
    @Transactional // Agregado @Transactional para operaciones de escritura
    public DocenteDTO actualizarDocente(String ci, DocenteDTO docenteDTO) { // Tipo cambiado a String
        Docente docenteExistente = docenteRepository.findById(ci) // Usando findById
                .orElseThrow(() -> new BusinessException("Docente con CI " + ci + " no encontrado para actualizar"));

        // Validar que el email, si se cambia, no exista para otro Docente
        if (!docenteExistente.getEmail().equals(docenteDTO.getEmail()) && docenteRepository.existsByEmail(docenteDTO.getEmail())) {
            throw new BusinessException("El email '" + docenteDTO.getEmail() + "' ya está registrado para otro docente.");
        }

        docenteValidator.validarActualizacionDocente(docenteDTO, docenteExistente); // Asume validación

        // Actualizar campos de la entidad con los datos del DTO
        // Importante: No actualizamos 'ci' ya que es la clave primaria y no debería cambiar
        docenteExistente.setNombre(docenteDTO.getNombre());
        docenteExistente.setApellido(docenteDTO.getApellido());
        docenteExistente.setEmail(docenteDTO.getEmail());
        // Convierte LocalDate del DTO a Date para la entidad
        docenteExistente.setFechaNac(docenteDTO.getFechaNac());
        docenteExistente.setDepartamento(docenteDTO.getDepartamento());
        docenteExistente.setNroEmpleado(docenteDTO.getNroEmpleado());

        Docente docenteActualizado = docenteRepository.save(docenteExistente);
        return convertToDTO(docenteActualizado);
    }

    @Override
    @CacheEvict(value = {"docente", "docentes"}, allEntries = true)
    @Transactional // Agregado @Transactional para operaciones de escritura
    public void eliminarDocente(String ci) { // Tipo cambiado a String
        if (!docenteRepository.existsById(ci)) {
            throw new BusinessException("docente con CI " + ci + " no encontrado para eliminar");
        }
        docenteRepository.deleteById(ci);
    }

    @Override
    @Transactional // Asegura que esta operación se ejecute dentro de una transacción
    public Docente obtenerDocenteConBloqueo(String ci) { // Tipo cambiado a String
        // Usamos el findById que tiene la anotación @Lock en el repositorio
        Docente est = docenteRepository.findById(ci) 
                .orElseThrow(() -> new BusinessException("Docente con CI " + ci + " no encontrado"));
        
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

    // Convierte una entidad Docente a un DocenteDTO
    private DocenteDTO convertToDTO(Docente docente) {
        return DocenteDTO.builder()
                .ci(docente.getCiDocente())
                .nombre(docente.getNombre())
                .apellido(docente.getApellido())
                .email(docente.getEmail())
                // Convierte java.util.Date a java.time.LocalDate
                .fechaNac(docente.getFechaNac())
                .departamento(docente.getDepartamento())
                .nroEmpleado(docente.getNroEmpleado())
                .build();
    }

    // Convierte un docenteDTO a una entidad docente
    private Docente convertToEntity(DocenteDTO docenteDTO) {
        if (docenteDTO == null) {
            return null;
        }
        return Docente.builder()
                .ciDocente(docenteDTO.getCi())
                .nombre(docenteDTO.getNombre())
                .apellido(docenteDTO.getApellido())
                .email(docenteDTO.getEmail())
                // Convierte java.time.LocalDate a java.util.Date
                .fechaNac(docenteDTO.getFechaNac())
                .departamento(docenteDTO.getDepartamento())
                .nroEmpleado(docenteDTO.getNroEmpleado())
                .build();
    }
}
