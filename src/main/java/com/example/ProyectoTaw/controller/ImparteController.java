package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.ImparteDTO;
import com.example.ProyectoTaw.model.Imparte; // Necesario para el método de bloqueo
import com.example.ProyectoTaw.service.IImparteService;

import jakarta.validation.Valid; // Para habilitar la validación en el DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/materiadocente") // Define la URL base para todos los endpoints de este controlador

public class ImparteController {
    private final IImparteService imparteService;

    @Autowired // Inyección de dependencias del servicio
    public ImparteController(IImparteService imparteService) {
        this.imparteService = imparteService;
    }

    /**
     * Endpoint para obtener todas las relaciones Materia-Docente.
     * GET /api/materiaDocente
     * @return ResponseEntity con una lista de ImparteDTO y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<ImparteDTO>> getAllRelaciones() {
        List<ImparteDTO> relaciones = imparteService.obtenerTodasLasRelaciones();
        return new ResponseEntity<>(relaciones, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener una relación Materia-Docente por su ID.
     * GET /api/materiaDocente/{id}
     * @param id El ID del registro de la relación.
     * @return ResponseEntity con el ImparteDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ImparteDTO> getRelacionById(@PathVariable Long id) {
        // El servicio lanzará BusinessException si no se encuentra, manejado por GlobalExceptionHandler
        ImparteDTO relacion = imparteService.obtenerRelacionPorId(id);
        return new ResponseEntity<>(relacion, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todas las relaciones de una materia por su código único.
     * GET /api/materiaDocente/materia/{materiaCodigoUnico}
     * @param materiaCodigoUnico El código único de la materia.
     * @return ResponseEntity con una lista de ImparteDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/materia/{materiaCodigoUnico}")
    public ResponseEntity<List<ImparteDTO>> getRelacionesByMateria(@PathVariable String materiaCodigoUnico) {
        List<ImparteDTO> relaciones = imparteService.obtenerRelacionesPorMateria(materiaCodigoUnico);
        return new ResponseEntity<>(relaciones, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todas las relaciones de un Docente por su ID.
     * GET /api/materiadocente/docente/{Cidocente}
     * @param ciDocente El ID del docente.
     * @return ResponseEntity con una lista de ImparteDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/docente/{ciDocente}")
    public ResponseEntity<List<ImparteDTO>> getRelacionesByDocente(@PathVariable String ciDocente) {
        List<ImparteDTO> relaciones = imparteService.obtenerRelacionesPorDocente(ciDocente);
        return new ResponseEntity<>(relaciones, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener una relación específica por la combinación de código de materia y ID de docente.
     * GET /api/materiadocente/materia/{materiaCodigoUnico}/docente/{ciDocente}
     * @param materiaCodigoUnico El código único de la materia.
     * @param ciDocente El ID del docente.
     * @return ResponseEntity con el ImparteDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/materia/{materiaCodigoUnico}/docente/{ciDocente}")
    public ResponseEntity<ImparteDTO> getRelacionByMateriaAndDocente(
            @PathVariable String materiaCodigoUnico,
            @PathVariable String ciDocente) {
        ImparteDTO relacion = imparteService.obtenerRelacionPorMateriaYDocente(materiaCodigoUnico, ciDocente);
        return new ResponseEntity<>(relacion, HttpStatus.OK);
    }
    

    /**
     * Endpoint para crear un nuevo registro de relación Materia-docente.
     * POST /api/materiadocente
     * @param imparteDTO Los datos de la relación a crear. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el ImparteDTO creado y estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<ImparteDTO> createRelacion(@Valid @RequestBody ImparteDTO imparteDTO) {
        ImparteDTO createdRelacion = imparteService.crearRelacion(imparteDTO);
        return new ResponseEntity<>(createdRelacion, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un registro de relación Materia-docente existente por su ID.
     * PUT /api/materiadocente/{id}
     * @param id El ID del registro de la relación a actualizar.
     * @param imparteDTO Los nuevos datos de la relación. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el ImparteDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ImparteDTO> updateRelacion(@PathVariable Long id,
                                                      @Valid @RequestBody ImparteDTO imparteDTO) {
        // La validación @Valid en el DTO se ejecuta antes de llamar al servicio
        ImparteDTO updatedRelacion = imparteService.actualizarRelacion(id, imparteDTO);
        return new ResponseEntity<>(updatedRelacion, HttpStatus.OK);
    }

    /**
     * Endpoint para eliminar un registro de relación Materia-docente por su ID.
     * DELETE /api/materiadocente/{id}
     * @param id El ID del registro de la relación a eliminar.
     * @return ResponseEntity con estado HTTP 204 (NO_CONTENT) si se elimina exitosamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelacion(@PathVariable Long id) {
        imparteService.eliminarRelacion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint para obtener un registro de relación Materia-docente con bloqueo pesimista.
     * Este endpoint es para casos específicos de alta concurrencia donde se necesita asegurar
     * la exclusividad sobre un registro durante una operación.
     * GET /api/materiadocente/{id}/bloqueo
     * @param id El ID del registro de la relación.
     * @return ResponseEntity con la entidad Imparte y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}/bloqueo")
    public ResponseEntity<Imparte> getRelacionWithLock(@PathVariable Long id) {
        Imparte relacion = imparteService.obtenerRelacionConBloqueo(id);
        return new ResponseEntity<>(relacion, HttpStatus.OK);
    }

}
