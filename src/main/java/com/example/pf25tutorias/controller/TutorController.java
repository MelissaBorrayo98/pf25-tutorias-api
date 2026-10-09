package com.example.pf25tutorias.controller;

import com.example.pf25tutorias.model.Tutor;
import com.example.pf25tutorias.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    @Autowired
    private TutorRepository tutorRepository;

    @GetMapping
    public List<Tutor> getAllTutores() {
        return tutorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Tutor> getTutorById(@PathVariable Long id) {
        return tutorRepository.findById(id);
    }

    @PostMapping
    public Tutor createTutor(@RequestBody Tutor tutor) {
        return tutorRepository.save(tutor);
    }

    @DeleteMapping("/{id}")
    public void deleteTutor(@PathVariable Long id) {
        tutorRepository.deleteById(id);
    }
}