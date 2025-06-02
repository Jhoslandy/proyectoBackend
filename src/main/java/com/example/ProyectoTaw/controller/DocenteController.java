package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.DocenteDTO;
import com.example.ProyectoTaw.model.Docente; // Necesario para el método de bloqueo
import com.example.ProyectoTaw.service.IDocenteService;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta ruta sea correcta

import jakarta.validation.Valid; // Para habilitar la validación en el DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/docentes") // Define la URL base para todos los endpoints de este controlador
public class DocenteController {

    private final IDocenteService docenteService;

    @Autowired // Inyección de dependencias del servicio
    public DocenteController(IDocenteService docenteService) {
        this.docenteService = docenteService;
    }

    /**
     * Endpoint para obtener todos los docentes.
     * GET /api/docentes
     * @return ResponseEntity con una lista de docenteDTO y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<DocenteDTO>> getAllDocentes() {
        List<DocenteDTO> docentes = docenteService.obtenerTodosLosDocentes();
        return new ResponseEntity<>(docentes, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener un Docente por su CI.
     * GET /api/docentes/{ci}
     * @param ci El Carnet de Identidad del Docente.
     * @return ResponseEntity con el DocenteDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/{ci}")
    public ResponseEntity<DocenteDTO> getDocenteByCi(@PathVariable String ci) {
        // El servicio lanzará BusinessException si no se encuentra, manejado por GlobalExceptionHandler
        DocenteDTO docente = docenteService.obtenerDocentePorCi(ci);
        return new ResponseEntity<>(docente, HttpStatus.OK);
    }

    /**
     * Endpoint para buscar Docentes por nombre o apellido.
     * GET /api/docentes/buscar?query={cadena}
     * @param query La cadena de búsqueda.
     * @return ResponseEntity con una lista de DocenteDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<DocenteDTO>> searchDocentes(@RequestParam String query) {
        List<DocenteDTO> docentes = docenteService.buscarDocentes(query);
        return new ResponseEntity<>(docentes, HttpStatus.OK);
    }

    /**
     * Endpoint para crear un nuevo Docente.
     * POST /api/docentes
     * @param docenteDTO Los datos del Docente a crear. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el DocenteDTO creado y estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<DocenteDTO> createDocente(@Valid @RequestBody DocenteDTO docenteDTO) {
        DocenteDTO createdDocente = docenteService.crearDocente(docenteDTO);
        return new ResponseEntity<>(createdDocente, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un Docente existente por su CI.
     * PUT /api/docentes/{ci}
     * @param ci El Carnet de Identidad del Docente a actualizar.
     * @param docenteDTO Los nuevos datos del Docente. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el DocenteDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{ci}")
    public ResponseEntity<DocenteDTO> updateDocente(@PathVariable String ci, 
                                                           @Valid @RequestBody DocenteDTO docenteDTO) {
        // La validación @Valid en el DTO se ejecuta antes de llamar al servicio
        DocenteDTO updatedDocente = docenteService.actualizarDocente(ci, docenteDTO);
        return new ResponseEntity<>(updatedDocente, HttpStatus.OK);
    }

    /**
     * Endpoint para eliminar un Docente por su CI.
     * DELETE /api/docentes/{ci}
     * @param ci El Carnet de Identidad del Docente a eliminar.
     * @return ResponseEntity con estado HTTP 204 (NO_CONTENT) si se elimina exitosamente.
     */
    @DeleteMapping("/{ci}")
    public ResponseEntity<Void> deleteDocente(@PathVariable String ci) {
        docenteService.eliminarDocente(ci);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint para obtener un Docente con bloqueo pesimista.
     * Este endpoint es para casos específicos de alta concurrencia donde se necesita asegurar
     * la exclusividad sobre un registro durante una operación.
     * GET /api/docentes/{ci}/bloqueo
     * @param ci El Carnet de Identidad del Docente.
     * @return ResponseEntity con la entidad Docente y estado HTTP 200 (OK).
     */
    @GetMapping("/{ci}/bloqueo")
    public ResponseEntity<Docente> getDocenteWithLock(@PathVariable String ci) {
        Docente docente = docenteService.obtenerDocenteConBloqueo(ci);
        return new ResponseEntity<>(docente, HttpStatus.OK);
    }
}
