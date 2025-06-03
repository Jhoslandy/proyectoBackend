package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.MateriaPrerequisitoDTO;
import com.example.ProyectoTaw.service.IMateriaPrerequisitoService;

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
@RequestMapping("/api/materias-prerequisitos")
public class MateriaPrerequisitoController {

    private final IMateriaPrerequisitoService prerequisitoService;
    private static final Logger logger = LoggerFactory.getLogger(MateriaPrerequisitoController.class);

    @Autowired
    public MateriaPrerequisitoController(IMateriaPrerequisitoService prerequisitoService) {
        this.prerequisitoService = prerequisitoService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<MateriaPrerequisitoDTO> crearRelacion(@Valid @RequestBody MateriaPrerequisitoDTO dto) {
        MateriaPrerequisitoDTO nuevaRelacion = prerequisitoService.crearRelacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaRelacion);
    }

    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<MateriaPrerequisitoDTO>> listarPorMateria(@PathVariable Long materiaId) {
        List<MateriaPrerequisitoDTO> relaciones = prerequisitoService.listarPorMateriaId(materiaId);
        return ResponseEntity.ok(relaciones);
    }

    @GetMapping("/prerequisito/{prerequisitoId}")
    public ResponseEntity<List<MateriaPrerequisitoDTO>> listarPorPrerequisito(@PathVariable Long prerequisitoId) {
        List<MateriaPrerequisitoDTO> relaciones = prerequisitoService.listarPorPrerequisitoId(prerequisitoId);
        return ResponseEntity.ok(relaciones);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarRelacion(@PathVariable Long id) {
        prerequisitoService.eliminarRelacion(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MateriaPrerequisitoDTO> actualizarRelacion(
            @PathVariable Long id,
            @Valid @RequestBody MateriaPrerequisitoDTO dto) {
        MateriaPrerequisitoDTO actualizada = prerequisitoService.actualizarRelacion(id, dto);
        return ResponseEntity.ok(actualizada);
    }

}
