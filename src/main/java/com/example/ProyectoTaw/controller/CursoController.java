package com.example.ProyectoTaw.controller;

import com.example.ProyectoTaw.dto.CursoDTO;
import com.example.ProyectoTaw.model.Curso;
import com.example.ProyectoTaw.service.ICursoService;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final ICursoService cursoService;

    @Autowired
    public CursoController(ICursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public ResponseEntity<List<CursoDTO>> getAllCursos() {
        List<CursoDTO> cursos = cursoService.obtenerTodosLosCursos();
        return new ResponseEntity<>(cursos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> getCursoById(@PathVariable Integer id) {
        CursoDTO curso = cursoService.obtenerCursoPorId(id);
        return new ResponseEntity<>(curso, HttpStatus.OK);
    }

    @GetMapping("/buscar/semestre")
    public ResponseEntity<List<CursoDTO>> searchCursosBySemestre(@RequestParam String valor) {
        List<CursoDTO> cursos = cursoService.buscarCursosPorSemestre(valor);
        return new ResponseEntity<>(cursos, HttpStatus.OK);
    }

    @GetMapping("/buscar/anio")
    public ResponseEntity<List<CursoDTO>> searchCursosByAnio(@RequestParam Integer valor) {
        List<CursoDTO> cursos = cursoService.buscarCursosPorAnio(valor);
        return new ResponseEntity<>(cursos, HttpStatus.OK);
    }

    @GetMapping("/buscar/dia")
    public ResponseEntity<List<CursoDTO>> searchCursosByDia(@RequestParam String valor) {
        List<CursoDTO> cursos = cursoService.buscarCursosPorDia(valor);
        return new ResponseEntity<>(cursos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CursoDTO> createCurso(@Valid @RequestBody CursoDTO cursoDTO) {
        CursoDTO createdCurso = cursoService.crearCurso(cursoDTO);
        return new ResponseEntity<>(createdCurso, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoDTO> updateCurso(@PathVariable Integer id,
                                                @Valid @RequestBody CursoDTO cursoDTO) {
        CursoDTO updatedCurso = cursoService.actualizarCurso(id, cursoDTO);
        return new ResponseEntity<>(updatedCurso, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurso(@PathVariable Integer id) {
        cursoService.eliminarCurso(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/bloqueo")
    public ResponseEntity<Curso> getCursoWithLock(@PathVariable Integer id) {
        Curso curso = cursoService.obtenerCursoConBloqueo(id);
        return new ResponseEntity<>(curso, HttpStatus.OK);
    }
}