// Archivo: src/main/java/com/example/ProyectoTaw/controller/inscritoController.java

package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.InscritoDTO;
import com.example.ProyectoTaw.model.Inscrito;
import com.example.ProyectoTaw.service.IInscritoService;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; // <--- Importar
import org.slf4j.LoggerFactory; // <--- Importar

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@CrossOrigin(origins = "http://localhost:5500") // Asegúrate de que esta URL coincida con tu frontend
public class InscritoController {

    private final IInscritoService inscritoService;
    // <--- AGREGAR ESTO SI NO LO TIENES
    private static final Logger logger = LoggerFactory.getLogger(InscritoController.class);

    @Autowired
    public InscritoController(IInscritoService inscritoService) {
        this.inscritoService = inscritoService;
    }

    @GetMapping
    public ResponseEntity<List<InscritoDTO>> getAllInscripciones() {
        return new ResponseEntity<>(inscritoService.obtenerTodasLasInscripciones(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscritoDTO> getInscripcionById(@PathVariable Long id) {
        return new ResponseEntity<>(inscritoService.obtenerInscripcionPorId(id), HttpStatus.OK);
    }

    @GetMapping("/estudiante/{estudianteCi}")
    public ResponseEntity<List<InscritoDTO>> getInscripcionesByEstudiante(@PathVariable String estudianteCi) {
        return new ResponseEntity<>(inscritoService.obtenerInscripcionesPorEstudiante(estudianteCi), HttpStatus.OK);
    }

    @GetMapping("/materia/{materiaCodigoUnico}")
    public ResponseEntity<List<InscritoDTO>> getInscripcionesByMateria(@PathVariable String materiaCodigoUnico) {
        return new ResponseEntity<>(inscritoService.obtenerInscripcionesPorMateria(materiaCodigoUnico), HttpStatus.OK);
    }

    @GetMapping("/estudiante/{estudianteCi}/materia/{materiaCodigoUnico}/fecha/{fechaInscripcion}")
    public ResponseEntity<InscritoDTO> getInscripcionByEstudianteMateriaAndFecha(
            @PathVariable String estudianteCi,
            @PathVariable String materiaCodigoUnico,
            @PathVariable LocalDate fechaInscripcion) {
        return new ResponseEntity<>(inscritoService.obtenerInscripcionPorEstudianteMateriaYFecha(estudianteCi, materiaCodigoUnico, fechaInscripcion), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<InscritoDTO> createInscripcion(@Valid @RequestBody InscritoDTO inscritoDTO) {
        InscritoDTO createdInscripcion = inscritoService.crearInscripcion(inscritoDTO);
        return new ResponseEntity<>(createdInscripcion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InscritoDTO> updateInscripcion(@PathVariable Long id, @Valid @RequestBody InscritoDTO inscritoDTO) {
        InscritoDTO updatedInscripcion = inscritoService.actualizarInscripcion(id, inscritoDTO);
        return new ResponseEntity<>(updatedInscripcion, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInscripcion(@PathVariable Long id) {
        inscritoService.eliminarInscripcion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/bloqueo")
    public ResponseEntity<Inscrito> getInscripcionWithLock(@PathVariable Long id) {
        Inscrito inscripcion = inscritoService.obtenerInscripcionConBloqueo(id);
        return new ResponseEntity<>(inscripcion, HttpStatus.OK);
    }

    // *******************************************************************
    // ****** AGREGAR ESTE NUEVO MÉTODO DELETE EN InscritoController.java ******
    // *******************************************************************
    @DeleteMapping("/estudiante/{estudianteCi}/materia/{materiaCodigoUnico}")
    public ResponseEntity<Void> eliminarInscripcionPorEstudianteYMateria(
            @PathVariable String estudianteCi,
            @PathVariable String materiaCodigoUnico) {
        long inicio = System.currentTimeMillis();
        logger.info("[INSCRIPCION] Inicio eliminarInscripcionPorEstudianteYMateria para estudiante CI: {} y materia código: {}", estudianteCi, materiaCodigoUnico);
        inscritoService.eliminarInscripcionPorEstudianteYMateria(estudianteCi, materiaCodigoUnico); // Llama al servicio
        long fin = System.currentTimeMillis();
        logger.info("[INSCRIPCION] Fin eliminarInscripcionPorEstudianteYMateria (Duración: {} ms)", (fin - inicio));
        return ResponseEntity.noContent().build(); // 204 No Content es la respuesta estándar para DELETE exitoso
    }
}