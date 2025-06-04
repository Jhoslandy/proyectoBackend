package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.RegistraNotaDTO;
import com.example.ProyectoTaw.service.IRegistraNotaService;

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
@RequestMapping("/api/notas")
public class RegistraNotaController {

    private final IRegistraNotaService registraNotaService;
    private static final Logger logger = LoggerFactory.getLogger(RegistraNotaController.class);

    @Autowired
    public RegistraNotaController(IRegistraNotaService registraNotaService) {
        this.registraNotaService = registraNotaService;
    }

    @GetMapping
    public ResponseEntity<List<RegistraNotaDTO>> listarNotas() {
        long inicio = System.currentTimeMillis();
        logger.info("[NOTA] Inicio listarNotas: {}", inicio);
        List<RegistraNotaDTO> notas = registraNotaService.listarNotas();
        long fin = System.currentTimeMillis();
        logger.info("[NOTA] Fin listarNotas: {} (Duración: {} ms)", fin, (fin - inicio));
        return ResponseEntity.ok(notas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistraNotaDTO> obtenerNotaPorId(@PathVariable Long id) {
        long inicio = System.currentTimeMillis();
        logger.info("[NOTA] Inicio obtenerNotaPorId: {}", inicio);
        RegistraNotaDTO nota = registraNotaService.obtenerNotaPorId(id);
        long fin = System.currentTimeMillis();
        logger.info("[NOTA] Fin obtenerNotaPorId: {} (Duración: {} ms)", fin, (fin - inicio));
        return ResponseEntity.ok(nota);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<RegistraNotaDTO> crearNota(@Valid @RequestBody RegistraNotaDTO notaDTO) {
        RegistraNotaDTO nuevaNota = registraNotaService.crearNota(notaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaNota);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<RegistraNotaDTO> actualizarNota(
            @PathVariable Long id,
            @Valid @RequestBody RegistraNotaDTO notaDTO) {
        RegistraNotaDTO notaActualizada = registraNotaService.actualizarNota(id, notaDTO);
        return ResponseEntity.ok(notaActualizada);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarNota(@PathVariable Long id) {
        registraNotaService.eliminarNota(id);
        return ResponseEntity.noContent().build();
    }
}
