package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.AsisteDTO;
import com.example.ProyectoTaw.model.Asiste; // Necesario para el método de bloqueo
import com.example.ProyectoTaw.service.IAsisteService;

import jakarta.validation.Valid; // Para habilitar la validación en el DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/asistencias") // Define la URL base para todos los endpoints de este controlador
public class AsisteController {

    private final IAsisteService asisteService;

    @Autowired // Inyección de dependencias del servicio
    public AsisteController(IAsisteService asisteService) {
        this.asisteService = asisteService;
    }

    /**
     * Endpoint para obtener todos los registros de asistencia.
     * GET /api/asistencias
     * @return ResponseEntity con una lista de AsisteDTO y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<AsisteDTO>> getAllAsistencias() {
        List<AsisteDTO> asistencias = asisteService.obtenerTodasLasAsistencias();
        return new ResponseEntity<>(asistencias, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener un registro de asistencia por su ID.
     * GET /api/asistencias/{id}
     * @param id El ID del registro de asistencia.
     * @return ResponseEntity con el AsisteDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AsisteDTO> getAsistenciaById(@PathVariable Long id) {
        // El servicio lanzará BusinessException si no se encuentra, manejado por GlobalExceptionHandler
        AsisteDTO asistencia = asisteService.obtenerAsistenciaPorId(id);
        return new ResponseEntity<>(asistencia, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todos los registros de asistencia de un estudiante por su CI.
     * GET /api/asistencias/estudiante/{estudianteCi}
     * @param estudianteCi La CI del estudiante.
     * @return ResponseEntity con una lista de AsisteDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/estudiante/{estudianteCi}")
    public ResponseEntity<List<AsisteDTO>> getAsistenciasByEstudianteCi(@PathVariable String estudianteCi) {
        List<AsisteDTO> asistencias = asisteService.obtenerAsistenciasPorEstudiante(estudianteCi);
        return new ResponseEntity<>(asistencias, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todos los registros de asistencia de un curso por su ID.
     * GET /api/asistencias/curso/{cursoIdCurso}
     * @param cursoIdCurso El ID del curso.
     * @return ResponseEntity con una lista de AsisteDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/curso/{cursoIdCurso}")
    public ResponseEntity<List<AsisteDTO>> getAsistenciasByCursoIdCurso(@PathVariable Integer cursoIdCurso) {
        List<AsisteDTO> asistencias = asisteService.obtenerAsistenciasPorCurso(cursoIdCurso);
        return new ResponseEntity<>(asistencias, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todos los registros de asistencia de un estudiante en un curso específico.
     * GET /api/asistencias/estudiante/{estudianteCi}/curso/{cursoIdCurso}
     * @param estudianteCi La CI del estudiante.
     * @param cursoIdCurso El ID del curso.
     * @return ResponseEntity con una lista de AsisteDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/estudiante/{estudianteCi}/curso/{cursoIdCurso}")
    public ResponseEntity<List<AsisteDTO>> getAsistenciasByEstudianteAndCurso(
            @PathVariable String estudianteCi,
            @PathVariable Integer cursoIdCurso) {
        List<AsisteDTO> asistencias = asisteService.obtenerAsistenciasDeEstudianteEnCurso(estudianteCi, cursoIdCurso);
        return new ResponseEntity<>(asistencias, HttpStatus.OK);
    }

    /**
     * Endpoint para crear un nuevo registro de asistencia.
     * POST /api/asistencias
     * @param asisteDTO Los datos de la asistencia a crear. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el AsisteDTO creado y estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<AsisteDTO> createAsistencia(@Valid @RequestBody AsisteDTO asisteDTO) {
        AsisteDTO createdAsistencia = asisteService.crearAsistencia(asisteDTO);
        return new ResponseEntity<>(createdAsistencia, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un registro de asistencia existente por su ID.
     * PUT /api/asistencias/{id}
     * @param id El ID del registro de asistencia a actualizar.
     * @param asisteDTO Los nuevos datos de la asistencia. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el AsisteDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<AsisteDTO> updateAsistencia(@PathVariable Long id,
                                                      @Valid @RequestBody AsisteDTO asisteDTO) {
        // La validación @Valid en el DTO se ejecuta antes de llamar al servicio
        AsisteDTO updatedAsistencia = asisteService.actualizarAsistencia(id, asisteDTO);
        return new ResponseEntity<>(updatedAsistencia, HttpStatus.OK);
    }

    /**
     * Endpoint para eliminar un registro de asistencia por su ID.
     * DELETE /api/asistencias/{id}
     * @param id El ID del registro de asistencia a eliminar.
     * @return ResponseEntity con estado HTTP 204 (NO_CONTENT) si se elimina exitosamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsistencia(@PathVariable Long id) {
        asisteService.eliminarAsistencia(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint para obtener un registro de asistencia con bloqueo pesimista.
     * Este endpoint es para casos específicos de alta concurrencia donde se necesita asegurar
     * la exclusividad sobre un registro durante una operación.
     * GET /api/asistencias/{id}/bloqueo
     * @param id El ID del registro de asistencia.
     * @return ResponseEntity con la entidad Asiste y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}/bloqueo")
    public ResponseEntity<Asiste> getAsistenciaWithLock(@PathVariable Long id) {
        Asiste asistencia = asisteService.obtenerAsistenciaConBloqueo(id);
        return new ResponseEntity<>(asistencia, HttpStatus.OK);
    }
}