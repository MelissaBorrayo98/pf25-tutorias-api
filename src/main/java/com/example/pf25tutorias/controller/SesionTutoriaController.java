package com.example.pf25tutorias.controller;

import com.example.pf25tutorias.dto.SesionTutoriaRequestDTO;
import com.example.pf25tutorias.dto.SesionTutoriaResponseDTO;
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
    public ResponseEntity<List<SesionTutoriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(sesionService.listarTodas());
    }

    // 2. Crear una nueva sesión usando DTO
    @PostMapping("/materia/{materiaId}")
    public ResponseEntity<?> crearSesion(@PathVariable Long materiaId, @RequestBody SesionTutoriaRequestDTO requestDTO) {
        try {
            SesionTutoriaResponseDTO nuevaSesion = sesionService.crearSesion(materiaId, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSesion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 3. Consultar sesiones por materia
    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<SesionTutoriaResponseDTO>> obtenerPorMateria(@PathVariable Long materiaId) {
        return ResponseEntity.ok(sesionService.obtenerPorMateria(materiaId));
    }

    // 4. Consultar sesiones pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<SesionTutoriaResponseDTO>> obtenerPendientes() {
        return ResponseEntity.ok(sesionService.obtenerPendientes());
    }

    // 5. Consultar sesiones por rango de fecha/hora
    @GetMapping("/fecha")
    public ResponseEntity<List<SesionTutoriaResponseDTO>> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(sesionService.obtenerPorFecha(inicio, fin));
    }

    // 6. Indicador: Total esperado por día
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
    // 8. Actualizar una sesión existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSesion(@PathVariable Long id, @RequestBody SesionTutoriaRequestDTO requestDTO) {
        try {
            SesionTutoriaResponseDTO sesionActualizada = sesionService.actualizarSesion(id, requestDTO);
            return ResponseEntity.ok(sesionActualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}