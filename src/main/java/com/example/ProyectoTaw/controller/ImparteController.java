package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.ImparteDTO;
import com.example.ProyectoTaw.model.Imparte;
import com.example.ProyectoTaw.service.IImparteService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las relaciones entre Materias y Docentes.
 */
@RestController
@RequestMapping("/api/materiadocente")
public class ImparteController {

    private final IImparteService imparteService;

    @Autowired
    public ImparteController(IImparteService imparteService) {
        this.imparteService = imparteService;
    }

    /**
     * Obtiene todas las relaciones Materia-Docente.
     */
    @GetMapping
    public ResponseEntity<List<ImparteDTO>> getAllRelaciones() {
        return ResponseEntity.ok(imparteService.obtenerTodasLasRelaciones());
    }

    /**
     * Obtiene una relación por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ImparteDTO> getRelacionById(@PathVariable Long id) {
        return ResponseEntity.ok(imparteService.obtenerRelacionPorId(id));
    }

    /**
     * Obtiene todas las relaciones de una materia específica.
     */
    @GetMapping("/materia/{materiaCodigoUnico}")
    public ResponseEntity<List<ImparteDTO>> getRelacionesByMateria(@PathVariable String materiaCodigoUnico) {
        return ResponseEntity.ok(imparteService.obtenerRelacionesPorMateria(materiaCodigoUnico));
    }

    /**
     * Obtiene todas las relaciones de un docente específico.
     */
    @GetMapping("/docente/{ciDocente}")
    public ResponseEntity<List<ImparteDTO>> getRelacionesByDocente(@PathVariable String ciDocente) {
        return ResponseEntity.ok(imparteService.obtenerRelacionesPorDocente(ciDocente));
    }

    /**
     * Obtiene una relación específica entre una materia y un docente.
     */
    @GetMapping("/materia/{materiaCodigoUnico}/docente/{ciDocente}")
    public ResponseEntity<ImparteDTO> getRelacionByMateriaAndDocente(
            @PathVariable String materiaCodigoUnico,
            @PathVariable String ciDocente) {
        return ResponseEntity.ok(imparteService.obtenerRelacionPorMateriaYDocente(materiaCodigoUnico, ciDocente));
    }

    /**
     * Crea una nueva relación Materia-Docente.
     */
    @PostMapping
    public ResponseEntity<ImparteDTO> createRelacion(@Valid @RequestBody ImparteDTO imparteDTO) {
        ImparteDTO creada = imparteService.crearRelacion(imparteDTO);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    /**
     * Actualiza una relación Materia-Docente existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ImparteDTO> updateRelacion(@PathVariable Long id,
                                                     @Valid @RequestBody ImparteDTO imparteDTO) {
        ImparteDTO actualizada = imparteService.actualizarRelacion(id, imparteDTO);
        return ResponseEntity.ok(actualizada);
    }

    /**
     * Elimina una relación Materia-Docente por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelacion(@PathVariable Long id) {
        imparteService.eliminarRelacion(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene una relación con bloqueo pesimista (simulación de concurrencia).
     */
    @GetMapping("/{id}/bloqueo")
    public ResponseEntity<Imparte> getRelacionWithLock(@PathVariable Long id) {
        Imparte bloqueada = imparteService.obtenerRelacionConBloqueo(id);
        return ResponseEntity.ok(bloqueada);
    }
}
