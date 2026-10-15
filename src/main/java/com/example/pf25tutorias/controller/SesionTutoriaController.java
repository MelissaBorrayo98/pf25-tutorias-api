package com.example.pf25tutorias.controller;

import com.example.pf25tutorias.model.SesionTutoria;
import com.example.pf25tutorias.service.SesionTutoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sesiones")
public class SesionTutoriaController {

    @Autowired
    private SesionTutoriaService sesionService;

    // 1. Listar todas las sesiones
    @GetMapping
    public ResponseEntity<List<SesionTutoria>> listarTodas() {
        return ResponseEntity.ok(sesionService.listarTodas());
    }

    // 2. Crear una nueva sesión asociada a una materia (valida horarios y calcula costo)
    @PostMapping("/materia/{materiaId}")
    public ResponseEntity<?> crearSesion(@PathVariable Long materiaId, @RequestBody SesionTutoria sesion) {
        try {
            SesionTutoria nuevaSesion = sesionService.crearSesion(materiaId, sesion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSesion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 3. Consultar sesiones por materia
    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<SesionTutoria>> obtenerPorMateria(@PathVariable Long materiaId) {
        return ResponseEntity.ok(sesionService.obtenerPorMateria(materiaId));
    }

    // 4. Consultar sesiones pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<SesionTutoria>> obtenerPendientes() {
        return ResponseEntity.ok(sesionService.obtenerPendientes());
    }

    // 5. Consultar sesiones por rango de fecha/hora
    @GetMapping("/fecha")
    public ResponseEntity<List<SesionTutoria>> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(sesionService.obtenerPorFecha(inicio, fin));
    }

    // 6. Indicador: Total esperado por día (en un rango de fecha/hora)
    @GetMapping("/total-dia")
    public ResponseEntity<Double> calcularTotalDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        Double total = sesionService.calcularTotalDia(inicio, fin);
        return ResponseEntity.ok(total);
    }

    // 7. Eliminar sesión
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSesion(@PathVariable Long id) {
        sesionService.eliminarSesion(id);
        return ResponseEntity.noContent().build();
    }
}