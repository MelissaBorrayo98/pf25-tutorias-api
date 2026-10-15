package com.example.pf25tutorias.controller;

import com.example.pf25tutorias.dto.MateriaDto;
import com.example.pf25tutorias.model.Materia;
import com.example.pf25tutorias.service.MateriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @PostMapping
    public ResponseEntity<Materia> crearMateria(@Valid @RequestBody MateriaDto materiaDto) {
        Materia nuevaMateria = materiaService.guardarMateriaDto(materiaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMateria);
    }

    @GetMapping
    public ResponseEntity<List<Materia>> listarMaterias() {
        List<Materia> materias = materiaService.listarMaterias();
        return ResponseEntity.ok(materias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Materia> obtenerMateriaPorId(@PathVariable Long id) {
        Optional<Materia> materia = materiaService.obtenerMateriaPorId(id);
        return materia.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Materia> actualizarMateria(@PathVariable Long id, @Valid @RequestBody MateriaDto materiaDto) {
        try {
            Materia materiaActualizada = materiaService.actualizarMateriaDto(id, materiaDto);
            return ResponseEntity.ok(materiaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMateria(@PathVariable Long id) {
        try {
            materiaService.eliminarMateria(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}