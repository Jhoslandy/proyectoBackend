package com.example.ProyectoTaw.service.impl;

import com.example.ProyectoTaw.dto.InscritoDTO;
import com.example.ProyectoTaw.model.Estudiante; // Necesario para buscar Estudiante
import com.example.ProyectoTaw.model.Materia;     // Necesario para buscar Materia
import com.example.ProyectoTaw.model.Inscrito;
import com.example.ProyectoTaw.repository.InscritoRepository;
import com.example.ProyectoTaw.repository.EstudianteRepository; // Asume que tienes este repositorio
import com.example.ProyectoTaw.repository.MateriaRepository;     // Asume que tienes este repositorio
import com.example.ProyectoTaw.service.IInscritoService;
import com.example.ProyectoTaw.validator.InscritoValidator; // Asumimos que tendrás un validador para Inscrito
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta clase exista

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
    private final EstudianteRepository estudianteRepository; // Para buscar estudiantes
    private final MateriaRepository materiaRepository;     // Para buscar materias
    private final InscritoValidator inscritoValidator;         // Tu validador para Inscrito

    @Autowired
    public InscritoServiceImpl(InscritoRepository inscritoRepository,
                               EstudianteRepository estudianteRepository,
                               MateriaRepository materiaRepository,
                               InscritoValidator inscritoValidator) {
        this.inscritoRepository = inscritoRepository;
        this.estudianteRepository = estudianteRepository;
        this.materiaRepository = materiaRepository;
        this.inscritoValidator = inscritoValidator;
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
        Inscrito inscripcion = inscritoRepository.findById(idInscrito)
                .orElseThrow(() -> new BusinessException("Inscripción con ID " + idInscrito + " no encontrada"));
        return convertToDTO(inscripcion);
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
        Inscrito inscripcion = inscritoRepository.findByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcion(estudianteCi, materiaCodigoUnico, fechaInscripcion)
                .orElseThrow(() -> new BusinessException("Inscripción para estudiante '" + estudianteCi +
                                                       "', materia '" + materiaCodigoUnico +
                                                       "' en la fecha " + fechaInscripcion + " no encontrada."));
        return convertToDTO(inscripcion);
    }

    @Override
    @CacheEvict(value = {"inscripciones", "inscripcion", "inscripcionesPorEstudiante", "inscripcionesPorMateria", "inscripcionPorEstudianteMateriaYFecha"}, allEntries = true)
    @Transactional
    public InscritoDTO crearInscripcion(InscritoDTO inscritoDTO) {
        // 1. Validar que Estudiante y Materia existan
        Estudiante estudiante = estudianteRepository.findByCi(inscritoDTO.getEstudianteCi())
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + inscritoDTO.getEstudianteCi() + " no encontrado."));

        Materia materia = materiaRepository.findByCodigoUnico(inscritoDTO.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código único '" + inscritoDTO.getMateriaCodigoUnico() + "' no encontrada."));

        // 2. Lógica de REINSCRIPCIÓN y RESTRICCIÓN DE 6 MESES
        // Obtener la última inscripción del estudiante para esta materia
        Optional<Inscrito> ultimaInscripcion = inscritoRepository.findFirstByEstudianteCiAndMateriaCodigoUnicoOrderByFechaInscripcionDesc(
            inscritoDTO.getEstudianteCi(), inscritoDTO.getMateriaCodigoUnico());

        if (ultimaInscripcion.isPresent()) {
            LocalDate ultimaFecha = ultimaInscripcion.get().getFechaInscripcion();
            // La próxima inscripción debe ser al menos 6 meses después de la última
            LocalDate fechaMinimaProximaInscripcion = ultimaFecha.plusMonths(6);

            if (inscritoDTO.getFechaInscripcion().isBefore(fechaMinimaProximaInscripcion)) {
                throw new BusinessException("No es posible inscribir al estudiante " + inscritoDTO.getEstudianteCi() +
                                            " en la materia '" + inscritoDTO.getMateriaCodigoUnico() +
                                            "' en la fecha " + inscritoDTO.getFechaInscripcion() +
                                            ". La próxima inscripción debe ser posterior al " + fechaMinimaProximaInscripcion + ".");
            }
        }
        
        // 3. Validar unicidad a nivel de DB (Estudiante, Materia, Fecha)
        if (inscritoRepository.existsByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcion(
                inscritoDTO.getEstudianteCi(), inscritoDTO.getMateriaCodigoUnico(), inscritoDTO.getFechaInscripcion())) {
            throw new BusinessException("Ya existe una inscripción para el estudiante '" + inscritoDTO.getEstudianteCi() +
                                        "' en la materia '" + inscritoDTO.getMateriaCodigoUnico() +
                                        "' en la fecha " + inscritoDTO.getFechaInscripcion() + ".");
        }

        // 4. Validación del DTO (con tu validador, para campos como fecha futura, formatos, etc.)
        inscritoValidator.validarCreacionInscripcion(inscritoDTO);

        // 5. Convertir DTO a entidad y guardar
        Inscrito inscrito = convertToEntity(inscritoDTO, estudiante, materia);
        Inscrito inscritoGuardado = inscritoRepository.save(inscrito);
        return convertToDTO(inscritoGuardado);
    }

    @Override
    @CachePut(value = "inscripcion", key = "#idInscrito")
    @CacheEvict(value = {"inscripciones", "inscripcionesPorEstudiante", "inscripcionesPorMateria", "inscripcionPorEstudianteMateriaYFecha"}, allEntries = true)
    @Transactional
    public InscritoDTO actualizarInscripcion(Long idInscrito, InscritoDTO inscritoDTO) {
        Inscrito inscripcionExistente = inscritoRepository.findById(idInscrito)
                .orElseThrow(() -> new BusinessException("Inscripción con ID " + idInscrito + " no encontrada para actualizar"));

        // Validar que Estudiante y Materia existan si se intenta cambiar sus referencias
        Estudiante nuevoEstudiante = estudianteRepository.findByCi(inscritoDTO.getEstudianteCi())
                .orElseThrow(() -> new BusinessException("Estudiante con CI " + inscritoDTO.getEstudianteCi() + " no encontrado."));

        Materia nuevaMateria = materiaRepository.findByCodigoUnico(inscritoDTO.getMateriaCodigoUnico())
                .orElseThrow(() -> new BusinessException("Materia con código único '" + inscritoDTO.getMateriaCodigoUnico() + "' no encontrada."));

        // Lógica de unicidad y regla de 6 meses si los campos clave o la fecha de inscripción cambian
        boolean changedKeyFields = !inscripcionExistente.getEstudiante().getCi().equals(inscritoDTO.getEstudianteCi()) ||
                                   !inscripcionExistente.getMateria().getCodigoUnico().equals(inscritoDTO.getMateriaCodigoUnico()) ||
                                   !inscripcionExistente.getFechaInscripcion().equals(inscritoDTO.getFechaInscripcion());

        if (changedKeyFields) {
            // Si la combinación clave (estudiante, materia, fecha) ha cambiado, verificar unicidad de la nueva combinación
            if (inscritoRepository.existsByEstudianteCiAndMateriaCodigoUnicoAndFechaInscripcion(
                    inscritoDTO.getEstudianteCi(), inscritoDTO.getMateriaCodigoUnico(), inscritoDTO.getFechaInscripcion())) {
                throw new BusinessException("La combinación de estudiante, materia y fecha ya existe para otra inscripción.");
            }

            // Aplicar la regla de los 6 meses si se está actualizando la fecha o las referencias
            Optional<Inscrito> ultimaInscripcionParaEstaMateriaEstudiante = inscritoRepository.findFirstByEstudianteCiAndMateriaCodigoUnicoOrderByFechaInscripcionDesc(
                inscritoDTO.getEstudianteCi(), inscritoDTO.getMateriaCodigoUnico());

            if (ultimaInscripcionParaEstaMateriaEstudiante.isPresent() && !ultimaInscripcionParaEstaMateriaEstudiante.get().getIdInscrito().equals(idInscrito)) {
                 // Si la última inscripción para este estudiante/materia NO es la que estamos actualizando,
                 // entonces estamos validando una NUEVA fecha contra una anterior (potencialmente una reinscripción).
                LocalDate ultimaFecha = ultimaInscripcionParaEstaMateriaEstudiante.get().getFechaInscripcion();
                LocalDate fechaMinimaProximaInscripcion = ultimaFecha.plusMonths(6);

                if (inscritoDTO.getFechaInscripcion().isBefore(fechaMinimaProximaInscripcion)) {
                    throw new BusinessException("La fecha de inscripción " + inscritoDTO.getFechaInscripcion() +
                                                " para la reinscripción debe ser posterior al " + fechaMinimaProximaInscripcion + ".");
                }
            } else if (ultimaInscripcionParaEstaMateriaEstudiante.isPresent() && ultimaInscripcionParaEstaMateriaEstudiante.get().getIdInscrito().equals(idInscrito)){
                // Si la última inscripción para este estudiante/materia ES la que estamos actualizando,
                // Y la fecha de inscripción actual es diferente, debemos revalidar contra la ANTERIOR fecha.
                // Esto es para evitar que se ponga una fecha inválida.
                 if (inscritoDTO.getFechaInscripcion().isBefore(inscripcionExistente.getFechaInscripcion()) &&
                     !inscritoDTO.getFechaInscripcion().equals(inscripcionExistente.getFechaInscripcion())) {
                      throw new BusinessException("No se puede establecer una fecha de inscripción anterior a la actual para la misma inscripción.");
                 }
            }
        }


        // Validación del DTO (para campos obligatorios, formatos, etc.)
        inscritoValidator.validarActualizacionInscripcion(inscritoDTO, inscripcionExistente);

        // Actualizar campos de la entidad
        inscripcionExistente.setEstudiante(nuevoEstudiante);
        inscripcionExistente.setMateria(nuevaMateria);
        inscripcionExistente.setFechaInscripcion(inscritoDTO.getFechaInscripcion());

        Inscrito inscritoActualizado = inscritoRepository.save(inscripcionExistente);
        return convertToDTO(inscritoActualizado);
    }

    @Override
    @CacheEvict(value = {"inscripcion", "inscripciones", "inscripcionesPorEstudiante", "inscripcionesPorMateria", "inscripcionPorEstudianteMateriaYFecha"}, allEntries = true)
    @Transactional
    public void eliminarInscripcion(Long idInscrito) {
        if (!inscritoRepository.existsById(idInscrito)) {
            throw new BusinessException("Inscripción con ID " + idInscrito + " no encontrada para eliminar");
        }
        inscritoRepository.deleteById(idInscrito);
    }

    @Override
    @Transactional
    public Inscrito obtenerInscripcionConBloqueo(Long idInscrito) {
        Inscrito inscripcion = inscritoRepository.findById(idInscrito)
                .orElseThrow(() -> new BusinessException("Inscripción con ID " + idInscrito + " no encontrada."));

        // Simula un proceso largo que mantiene el bloqueo
        try {
            Thread.sleep(15000); // 15 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("La operación de bloqueo fue interrumpida.");
        }
        return inscripcion;
    }

    // --- Métodos de Conversión DTO <-> Entidad ---

    // Convierte una entidad Inscrito a un InscritoDTO
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

    // Convierte un InscritoDTO a una entidad Inscrito
    // Necesita las entidades Estudiante y Materia para establecer las relaciones ManyToOne
    private Inscrito convertToEntity(InscritoDTO inscritoDTO, Estudiante estudiante, Materia materia) {
        if (inscritoDTO == null) {
            return null;
        }
        return Inscrito.builder()
                .idInscrito(inscritoDTO.getIdInscrito()) // Puede ser nulo en la creación
                .estudiante(estudiante)
                .materia(materia)
                .fechaInscripcion(inscritoDTO.getFechaInscripcion())
                .build();
    }
}