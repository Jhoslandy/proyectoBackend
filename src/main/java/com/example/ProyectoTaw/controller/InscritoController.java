package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.InscritoDTO;
import com.example.ProyectoTaw.model.Inscrito; // Necesario para el método de bloqueo
import com.example.ProyectoTaw.service.IInscritoService;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta ruta sea correcta

import jakarta.validation.Valid; // Para habilitar la validación en el DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // Para manejar fechas en los path variables si es necesario
import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/inscripciones") // Define la URL base para todos los endpoints de este controlador
public class InscritoController {

    private final IInscritoService inscritoService;

    @Autowired // Inyección de dependencias del servicio
    public InscritoController(IInscritoService inscritoService) {
        this.inscritoService = inscritoService;
    }

    /**
     * Endpoint para obtener todas las inscripciones.
     * GET /api/inscripciones
     * @return ResponseEntity con una lista de InscritoDTO y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<InscritoDTO>> getAllInscripciones() {
        List<InscritoDTO> inscripciones = inscritoService.obtenerTodasLasInscripciones();
        return new ResponseEntity<>(inscripciones, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener una inscripción por su ID.
     * GET /api/inscripciones/{id}
     * @param id El ID del registro de la inscripción.
     * @return ResponseEntity con el InscritoDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InscritoDTO> getInscripcionById(@PathVariable Long id) {
        // El servicio lanzará BusinessException si no se encuentra, manejado por GlobalExceptionHandler
        InscritoDTO inscripcion = inscritoService.obtenerInscripcionPorId(id);
        return new ResponseEntity<>(inscripcion, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todas las inscripciones de un estudiante por su CI.
     * GET /api/inscripciones/estudiante/{estudianteCi}
     * @param estudianteCi La CI del estudiante.
     * @return ResponseEntity con una lista de InscritoDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/estudiante/{estudianteCi}")
    public ResponseEntity<List<InscritoDTO>> getInscripcionesByEstudianteCi(@PathVariable String estudianteCi) {
        List<InscritoDTO> inscripciones = inscritoService.obtenerInscripcionesPorEstudiante(estudianteCi);
        return new ResponseEntity<>(inscripciones, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todas las inscripciones de una materia por su código único.
     * GET /api/inscripciones/materia/{materiaCodigoUnico}
     * @param materiaCodigoUnico El código único de la materia.
     * @return ResponseEntity con una lista de InscritoDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/materia/{materiaCodigoUnico}")
    public ResponseEntity<List<InscritoDTO>> getInscripcionesByMateriaCodigoUnico(@PathVariable String materiaCodigoUnico) {
        List<InscritoDTO> inscripciones = inscritoService.obtenerInscripcionesPorMateria(materiaCodigoUnico);
        return new ResponseEntity<>(inscripciones, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener una inscripción específica por la combinación de CI de estudiante, código de materia y fecha.
     * GET /api/inscripciones/estudiante/{estudianteCi}/materia/{materiaCodigoUnico}/fecha/{fechaInscripcion}
     * NOTA: La fecha debe venir en formato YYYY-MM-DD.
     * @param estudianteCi La CI del estudiante.
     * @param materiaCodigoUnico El código único de la materia.
     * @param fechaInscripcion La fecha de inscripción en formato String (ej. "2023-10-26").
     * @return ResponseEntity con el InscritoDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/estudiante/{estudianteCi}/materia/{materiaCodigoUnico}/fecha/{fechaInscripcion}")
    public ResponseEntity<InscritoDTO> getInscripcionByEstudianteMateriaAndFecha(
            @PathVariable String estudianteCi,
            @PathVariable String materiaCodigoUnico,
            @PathVariable LocalDate fechaInscripcion) { // Spring convierte el String a LocalDate automáticamente
        InscritoDTO inscripcion = inscritoService.obtenerInscripcionPorEstudianteMateriaYFecha(estudianteCi, materiaCodigoUnico, fechaInscripcion);
        return new ResponseEntity<>(inscripcion, HttpStatus.OK);
    }

    /**
     * Endpoint para crear un nuevo registro de inscripción.
     * POST /api/inscripciones
     * @param inscritoDTO Los datos de la inscripción a crear. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el InscritoDTO creado y estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<InscritoDTO> createInscripcion(@Valid @RequestBody InscritoDTO inscritoDTO) {
        InscritoDTO createdInscripcion = inscritoService.crearInscripcion(inscritoDTO);
        return new ResponseEntity<>(createdInscripcion, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un registro de inscripción existente por su ID.
     * PUT /api/inscripciones/{id}
     * @param id El ID del registro de la inscripción a actualizar.
     * @param inscritoDTO Los nuevos datos de la inscripción. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el InscritoDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<InscritoDTO> updateInscripcion(@PathVariable Long id,
                                                      @Valid @RequestBody InscritoDTO inscritoDTO) {
        // La validación @Valid en el DTO se ejecuta antes de llamar al servicio
        InscritoDTO updatedInscripcion = inscritoService.actualizarInscripcion(id, inscritoDTO);
        return new ResponseEntity<>(updatedInscripcion, HttpStatus.OK);
    }

    /**
     * Endpoint para eliminar un registro de inscripción por su ID.
     * DELETE /api/inscripciones/{id}
     * @param id El ID del registro de la inscripción a eliminar.
     * @return ResponseEntity con estado HTTP 204 (NO_CONTENT) si se elimina exitosamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInscripcion(@PathVariable Long id) {
        inscritoService.eliminarInscripcion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint para obtener un registro de inscripción por su ID con un bloqueo pesimista.
     * Este endpoint es para casos específicos de alta concurrencia donde se necesita asegurar
     * la exclusividad sobre un registro durante una operación.
     * GET /api/inscripciones/{id}/bloqueo
     * @param id El ID del registro de la inscripción.
     * @return ResponseEntity con la entidad Inscrito y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}/bloqueo")
    public ResponseEntity<Inscrito> getInscripcionWithLock(@PathVariable Long id) {
        Inscrito inscripcion = inscritoService.obtenerInscripcionConBloqueo(id);
        return new ResponseEntity<>(inscripcion, HttpStatus.OK);
    }
}