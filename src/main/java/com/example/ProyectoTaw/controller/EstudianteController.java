package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante; // Necesario para el método de bloqueo
import com.example.ProyectoTaw.service.IEstudianteService;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta ruta sea correcta

import jakarta.validation.Valid; // Para habilitar la validación en el DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@CrossOrigin(origins = "http://localhost:5500") // Permite solicitudes CORS desde el origen especificado
@RequestMapping("/api/estudiantes") // Define la URL base para todos los endpoints de este controlador
public class EstudianteController {

    private final IEstudianteService estudianteService;

    @Autowired // Inyección de dependencias del servicio
    public EstudianteController(IEstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    /**
     * Endpoint para obtener todos los estudiantes.
     * GET /api/estudiantes
     * @return ResponseEntity con una lista de EstudianteDTO y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<EstudianteDTO>> getAllEstudiantes() {
        List<EstudianteDTO> estudiantes = estudianteService.obtenerTodosLosEstudiantes();
        return new ResponseEntity<>(estudiantes, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener un estudiante por su CI.
     * GET /api/estudiantes/{ci}
     * @param ci El Carnet de Identidad del estudiante.
     * @return ResponseEntity con el EstudianteDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/{ci}")
    public ResponseEntity<EstudianteDTO> getEstudianteByCi(@PathVariable String ci) {
        // El servicio lanzará BusinessException si no se encuentra, manejado por GlobalExceptionHandler
        EstudianteDTO estudiante = estudianteService.obtenerEstudiantePorCi(ci);
        return new ResponseEntity<>(estudiante, HttpStatus.OK);
    }

    /**
     * Endpoint para buscar estudiantes por nombre o apellido.
     * GET /api/estudiantes/buscar?query={cadena}
     * @param query La cadena de búsqueda.
     * @return ResponseEntity con una lista de EstudianteDTO y estado HTTP 200 (OK).
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<EstudianteDTO>> searchEstudiantes(@RequestParam String query) {
        List<EstudianteDTO> estudiantes = estudianteService.buscarEstudiantes(query);
        return new ResponseEntity<>(estudiantes, HttpStatus.OK);
    }

    /**
     * Endpoint para crear un nuevo estudiante.
     * POST /api/estudiantes
     * @param estudianteDTO Los datos del estudiante a crear. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el EstudianteDTO creado y estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<EstudianteDTO> createEstudiante(@Valid @RequestBody EstudianteDTO estudianteDTO) {
        EstudianteDTO createdEstudiante = estudianteService.crearEstudiante(estudianteDTO);
        return new ResponseEntity<>(createdEstudiante, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar un estudiante existente por su CI.
     * PUT /api/estudiantes/{ci}
     * @param ci El Carnet de Identidad del estudiante a actualizar.
     * @param estudianteDTO Los nuevos datos del estudiante. Se valida automáticamente con @Valid.
     * @return ResponseEntity con el EstudianteDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{ci}")
    public ResponseEntity<EstudianteDTO> updateEstudiante(@PathVariable String ci, 
                                                           @Valid @RequestBody EstudianteDTO estudianteDTO) {
        // La validación @Valid en el DTO se ejecuta antes de llamar al servicio
        EstudianteDTO updatedEstudiante = estudianteService.actualizarEstudiante(ci, estudianteDTO);
        return new ResponseEntity<>(updatedEstudiante, HttpStatus.OK);
    }

    /**
     * Endpoint para eliminar un estudiante por su CI.
     * DELETE /api/estudiantes/{ci}
     * @param ci El Carnet de Identidad del estudiante a eliminar.
     * @return ResponseEntity con estado HTTP 204 (NO_CONTENT) si se elimina exitosamente.
     */
    @DeleteMapping("/{ci}")
    public ResponseEntity<Void> deleteEstudiante(@PathVariable String ci) {
        estudianteService.eliminarEstudiante(ci);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint para obtener un estudiante con bloqueo pesimista.
     * Este endpoint es para casos específicos de alta concurrencia donde se necesita asegurar
     * la exclusividad sobre un registro durante una operación.
     * GET /api/estudiantes/{ci}/bloqueo
     * @param ci El Carnet de Identidad del estudiante.
     * @return ResponseEntity con la entidad Estudiante y estado HTTP 200 (OK).
     */
    @GetMapping("/{ci}/bloqueo")
    public ResponseEntity<Estudiante> getEstudianteWithLock(@PathVariable String ci) {
        Estudiante estudiante = estudianteService.obtenerEstudianteConBloqueo(ci);
        return new ResponseEntity<>(estudiante, HttpStatus.OK);
    }
    // Inside EstudianteController class
    /**
     * Endpoint para obtener un estudiante por su email.
     * GET /api/estudiantes/by-email/{email}
     * @param email El email del estudiante.
     * @return ResponseEntity con el EstudianteDTO y estado HTTP 200 (OK), o 404 (NOT_FOUND) si no existe.
     */
    @GetMapping("/by-email/{email}")
    public ResponseEntity<EstudianteDTO> getEstudianteByEmail(@PathVariable String email) {
        EstudianteDTO estudiante = estudianteService.obtenerEstudiantePorEmail(email);
        return new ResponseEntity<>(estudiante, HttpStatus.OK);
    }
}