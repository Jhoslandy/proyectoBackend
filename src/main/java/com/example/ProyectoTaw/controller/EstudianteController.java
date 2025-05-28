package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;
import com.example.ProyectoTaw.service.IEstudianteService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/controller/estudiantes")
public class EstudianteController {

    private final IEstudianteService estudianteService;
    private static final Logger logger = LoggerFactory.getLogger(EstudianteController.class);

    @Autowired
    public EstudianteController(IEstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public ResponseEntity<List<EstudianteDTO>> obtenerTodosLosEstudiantes() {
        long inicio = System.currentTimeMillis();
        logger.info("[ESTUDIANTE] Inicio obtenerTodosLosEstudiantes: {}", inicio);
        List<EstudianteDTO> estudiantes = estudianteService.obtenerTodosLosEstudiantes();
        long fin = System.currentTimeMillis();
        logger.info("[ESTUDIANTE] Fin obtenerTodosLosEstudiantes: {} (Duracion: {} ms)", fin, (fin-inicio));
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/matricula/{nroMatricula}")
    public ResponseEntity<EstudianteDTO> obtenerEstudiantePorNroMatricula(
        @PathVariable String nroMatricula) {
        long inicio = System.currentTimeMillis();
        logger.info("[ESTUDIANTE] Inicio obtenerEstudiantePorNroMatricula: {}", inicio);
        EstudianteDTO estudiante = estudianteService.obtenerEstudiantePorNroMatricula(nroMatricula);
        long fin = System.currentTimeMillis();
        logger.info("[ESTUDIANTE] Fin obtenerEstudiantePorNroMatricula: {} (Duracion: {} ms)", fin, (fin-inicio));
        return ResponseEntity.ok(estudiante);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EstudianteDTO>> buscarEstudiantes(@RequestParam String query) {
        long inicio = System.currentTimeMillis();
        logger.info("[ESTUDIANTE] Inicio buscarEstudiantes por query: {}", query);
        List<EstudianteDTO> estudiantes = estudianteService.buscarEstudiantes(query);
        long fin = System.currentTimeMillis();
        logger.info("[ESTUDIANTE] Fin buscarEstudiantes: {} (Duracion: {} ms)", fin, (fin-inicio));
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/{id}/lock")
    public ResponseEntity<Estudiante> getEstudianteConBloqueo(
        @PathVariable Long id) {
        Estudiante estudiante = estudianteService.obtenerEstudianteConBloqueo(id);
        return ResponseEntity.ok(estudiante);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EstudianteDTO> crearEstudiante(@Valid @RequestBody EstudianteDTO estudianteDTO) {
        EstudianteDTO nuevoEstudiante = estudianteService.crearEstudiante(estudianteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEstudiante);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<EstudianteDTO> actualizarEstudiante(
        @PathVariable Long id,
        @Valid @RequestBody EstudianteDTO estudianteDTO) { 
        EstudianteDTO estudianteActualizado = estudianteService.actualizarEstudiante(id, estudianteDTO);
        return ResponseEntity.ok(estudianteActualizado);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarEstudiante(@PathVariable Long id) {
        estudianteService.eliminarEstudiante(id);
        return ResponseEntity.noContent().build();
    }
}