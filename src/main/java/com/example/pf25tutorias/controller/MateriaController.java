package com.example.pf25tutorias.controller;

import com.example.pf25tutorias.dto.MateriaResponseDTO;
import com.example.pf25tutorias.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @GetMapping
    public ResponseEntity<List<MateriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(materiaService.listarTodas());
    }
}