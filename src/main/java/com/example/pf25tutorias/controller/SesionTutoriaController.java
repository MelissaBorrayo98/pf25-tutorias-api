package com.example.pf25tutorias.controller;

import com.example.pf25tutorias.model.SesionTutoria;
import com.example.pf25tutorias.repository.SesionTutoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sesiones")
public class SesionTutoriaController {

    @Autowired
    private SesionTutoriaRepository sesionTutoriaRepository;

    @GetMapping
    public List<SesionTutoria> getAllSesiones() {
        return sesionTutoriaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<SesionTutoria> getSesionById(@PathVariable Long id) {
        return sesionTutoriaRepository.findById(id);
    }

    @PostMapping
    public SesionTutoria createSesion(@RequestBody SesionTutoria sesion) {
        return sesionTutoriaRepository.save(sesion);
    }

    @DeleteMapping("/{id}")
    public void deleteSesion(@PathVariable Long id) {
        sesionTutoriaRepository.deleteById(id);
    }
}
