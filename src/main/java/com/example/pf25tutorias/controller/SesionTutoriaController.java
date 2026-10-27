package com.example.pf25tutorias.controller;

import com.example.pf25tutorias.dto.SesionTutoriaRequestDTO;
import com.example.pf25tutorias.dto.SesionTutoriaResponseDTO;
import com.example.pf25tutorias.service.SesionTutoriaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sesiones")
public class SesionTutoriaController {

    private final SesionTutoriaService sesionService;

    public SesionTutoriaController(SesionTutoriaService sesionService) {
        this.sesionService = sesionService;
    }

    // 1. Listar todas las sesiones
    @GetMapping
    public ResponseEntity<List<SesionTutoriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(sesionService.listarTodas());
    }

    // 2. Consultar una sesión por ID
    @GetMapping("/{id}")
    public ResponseEntity<SesionTutoriaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sesionService.obtenerPorId(id));
    }

    // 3. Registrar una sesión (las validaciones y errores los maneja GlobalExceptionHandler)
    @PostMapping("/materia/{materiaId}")
    public ResponseEntity<SesionTutoriaResponseDTO> crearSesion(
            @PathVariable Long materiaId,
            @Valid @RequestBody SesionTutoriaRequestDTO requestDTO) {
        SesionTutoriaResponseDTO nueva = sesionService.crearSesion(materiaId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // 4. Consultar sesiones por materia
    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<SesionTutoriaResponseDTO>> obtenerPorMateria(@PathVariable Long materiaId) {
        return ResponseEntity.ok(sesionService.obtenerPorMateria(materiaId));
    }

    // 5. Consultar sesiones pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<SesionTutoriaResponseDTO>> obtenerPendientes() {
        return ResponseEntity.ok(sesionService.obtenerPendientes());
    }

    // 6. Consultar sesiones por rango de fecha/hora
    @GetMapping("/fecha")
    public ResponseEntity<List<SesionTutoriaResponseDTO>> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(sesionService.obtenerPorFecha(inicio, fin));
    }

    // 7. Indicador: total esperado de ingresos en un rango
    @GetMapping("/total-dia")
    public ResponseEntity<Double> calcularTotalDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(sesionService.calcularTotalDia(inicio, fin));
    }

    // 8. Actualizar una sesión existente
    @PutMapping("/{id}")
    public ResponseEntity<SesionTutoriaResponseDTO> actualizarSesion(
            @PathVariable Long id,
            @Valid @RequestBody SesionTutoriaRequestDTO requestDTO) {
        return ResponseEntity.ok(sesionService.actualizarSesion(id, requestDTO));
    }

    // 9. Eliminar una sesión
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSesion(@PathVariable Long id) {
        sesionService.eliminarSesion(id);
        return ResponseEntity.noContent().build();
    }
}
