package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.MateriaDTO;
import com.example.ProyectoTaw.service.IMateriaService;

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
@RequestMapping("/api/materias")
public class MateriaController {

    private final IMateriaService materiaService;
    private static final Logger logger = LoggerFactory.getLogger(MateriaController.class);

    @Autowired
    public MateriaController(IMateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @GetMapping
    public ResponseEntity<List<MateriaDTO>> listarMaterias() {
        long inicio = System.currentTimeMillis();
        logger.info("[MATERIA] Inicio listarMaterias: {}", inicio);
        List<MateriaDTO> materias = materiaService.listarMaterias();
        long fin = System.currentTimeMillis();
        logger.info("[MATERIA] Fin listarMaterias: {} (Duración: {} ms)", fin, (fin - inicio));
        return ResponseEntity.ok(materias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaDTO> obtenerMateriaPorId(@PathVariable Long id) {
        long inicio = System.currentTimeMillis();
        logger.info("[MATERIA] Inicio obtenerMateriaPorId: {}", inicio);
        MateriaDTO materia = materiaService.obtenerMateriaPorId(id);
        long fin = System.currentTimeMillis();
        logger.info("[MATERIA] Fin obtenerMateriaPorId: {} (Duración: {} ms)", fin, (fin - inicio));
        return ResponseEntity.ok(materia);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<MateriaDTO> crearMateria(@Valid @RequestBody MateriaDTO materiaDTO) {
        MateriaDTO nuevaMateria = materiaService.crearMateria(materiaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMateria);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<MateriaDTO> actualizarMateria(
            @PathVariable Long id,
            @Valid @RequestBody MateriaDTO materiaDTO) {
        MateriaDTO materiaActualizada = materiaService.actualizarMateria(id, materiaDTO);
        return ResponseEntity.ok(materiaActualizada);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarMateria(@PathVariable Long id) {
        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }
}