package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.SeDaDTO;
import com.example.ProyectoTaw.model.SeDa; // Necesario para el método de bloqueo
import com.example.ProyectoTaw.service.ISeDaService;

import jakarta.validation.Valid; // Para habilitar la validación en el DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/materiacurso") // Define la URL base para todos los endpoints de este controlador
public class SeDaController {

    private final ISeDaService seDaService;

    @Autowired // Inyección de dependencias del servicio
    public SeDaController(ISeDaService seDaService) {
        this.seDaService = seDaService;
    }

    /**
     * Endpoint para obtener todas las relaciones Materia-Curso.
     * GET /api/materiacurso
     * @return ResponseEntity con una lista de SeDaDTO y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<SeDaDTO>> getAllRelaciones() {
        List<SeDaDTO> relaciones = seDaService.obtenerTodasLasRelaciones();
        return new ResponseEntity<>(relaciones, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener una relación Materia-Curso por su ID.
     * GET /api/materiacurso/{id}
     * @param id El ID del registro de la relación.
     * @return ResponseEntity con el SeDaDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SeDaDTO> getRelacionById(@PathVariable Long id) {
        // El servicio lanzará BusinessException si no se encuentra, manejado por GlobalExceptionHandler
        SeDaDTO relacion = seDaService.obtenerRelacionPorId(id);
        return new ResponseEntity<>(relacion, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todas las relaciones de una materia por su código único.
     * GET /api/materiacurso/materia/{materiaCodigoUnico}
     * @param materiaCodigoUnico El código único de la materia.
     * @return ResponseEntity con una lista de SeDaDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/materia/{materiaCodigoUnico}")
    public ResponseEntity<List<SeDaDTO>> getRelacionesByMateria(@PathVariable String materiaCodigoUnico) {
        List<SeDaDTO> relaciones = seDaService.obtenerRelacionesPorMateria(materiaCodigoUnico);
        return new ResponseEntity<>(relaciones, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todas las relaciones de un curso por su ID.
     * GET /api/materiacurso/curso/{cursoIdCurso}
     * @param cursoIdCurso El ID del curso.
     * @return ResponseEntity con una lista de SeDaDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/curso/{cursoIdCurso}")
    public ResponseEntity<List<SeDaDTO>> getRelacionesByCurso(@PathVariable Integer cursoIdCurso) {
        List<SeDaDTO> relaciones = seDaService.obtenerRelacionesPorCurso(cursoIdCurso);
        return new ResponseEntity<>(relaciones, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener una relación específica por la combinación de código de materia y ID de curso.
     * GET /api/materiacurso/materia/{materiaCodigoUnico}/curso/{cursoIdCurso}
     * @param materiaCodigoUnico El código único de la materia.
     * @param cursoIdCurso El ID del curso.
     * @return ResponseEntity con el SeDaDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/materia/{materiaCodigoUnico}/curso/{cursoIdCurso}")
    public ResponseEntity<SeDaDTO> getRelacionByMateriaAndCurso(
            @PathVariable String materiaCodigoUnico,
            @PathVariable Integer cursoIdCurso) {
        SeDaDTO relacion = seDaService.obtenerRelacionPorMateriaYCurso(materiaCodigoUnico, cursoIdCurso);
        return new ResponseEntity<>(relacion, HttpStatus.OK);
    }

    /**
     * Endpoint para crear un nuevo registro de relación Materia-Curso.
     * POST /api/materiacurso
     * @param seDaDTO Los datos de la relación a crear. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el SeDaDTO creado y estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<SeDaDTO> createRelacion(@Valid @RequestBody SeDaDTO seDaDTO) {
        SeDaDTO createdRelacion = seDaService.crearRelacion(seDaDTO);
        return new ResponseEntity<>(createdRelacion, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un registro de relación Materia-Curso existente por su ID.
     * PUT /api/materiacurso/{id}
     * @param id El ID del registro de la relación a actualizar.
     * @param seDaDTO Los nuevos datos de la relación. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el SeDaDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<SeDaDTO> updateRelacion(@PathVariable Long id,
                                                      @Valid @RequestBody SeDaDTO seDaDTO) {
        // La validación @Valid en el DTO se ejecuta antes de llamar al servicio
        SeDaDTO updatedRelacion = seDaService.actualizarRelacion(id, seDaDTO);
        return new ResponseEntity<>(updatedRelacion, HttpStatus.OK);
    }

    /**
     * Endpoint para eliminar un registro de relación Materia-Curso por su ID.
     * DELETE /api/materiacurso/{id}
     * @param id El ID del registro de la relación a eliminar.
     * @return ResponseEntity con estado HTTP 204 (NO_CONTENT) si se elimina exitosamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelacion(@PathVariable Long id) {
        seDaService.eliminarRelacion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint para obtener un registro de relación Materia-Curso con bloqueo pesimista.
     * Este endpoint es para casos específicos de alta concurrencia donde se necesita asegurar
     * la exclusividad sobre un registro durante una operación.
     * GET /api/materiacurso/{id}/bloqueo
     * @param id El ID del registro de la relación.
     * @return ResponseEntity con la entidad SeDa y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}/bloqueo")
    public ResponseEntity<SeDa> getRelacionWithLock(@PathVariable Long id) {
        SeDa relacion = seDaService.obtenerRelacionConBloqueo(id);
        return new ResponseEntity<>(relacion, HttpStatus.OK);
    }
}