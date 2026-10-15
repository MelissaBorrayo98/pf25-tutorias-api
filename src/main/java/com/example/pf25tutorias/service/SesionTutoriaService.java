package com.example.pf25tutorias.service;

import com.example.pf25tutorias.model.Materia;
import com.example.pf25tutorias.model.SesionTutoria;
import com.example.pf25tutorias.repository.MateriaRepository;
import com.example.pf25tutorias.repository.SesionTutoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SesionTutoriaService {

    @Autowired
    private SesionTutoriaRepository sesionTutoriaRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    // Método que el controlador llama como listarTodas()
    public List<SesionTutoria> listarTodas() {
        return sesionTutoriaRepository.findAll();
    }

    public List<SesionTutoria> obtenerTodas() {
        return listarTodas();
    }

    // Método que el controlador llama como crearSesion(...)
    public SesionTutoria crearSesion(Long materiaId, SesionTutoria sesion) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + materiaId));

        sesion.setMateria(materia);
        sesion.setMateriaNombre(materia.getNombre());

        if (sesion.getDuracion() != null && materia.getTarifaHora() != null) {
            sesion.setCostoTotal(sesion.getDuracion() * materia.getTarifaHora());
        }

        return sesionTutoriaRepository.save(sesion);
    }

    public SesionTutoria crearSesionParaMateria(Long materiaId, SesionTutoria sesion) {
        return crearSesion(materiaId, sesion);
    }

    public List<SesionTutoria> obtenerPorMateria(Long materiaId) {
        return sesionTutoriaRepository.findByMateriaId(materiaId);
    }

    public List<SesionTutoria> obtenerPendientes() {
        return sesionTutoriaRepository.findByEstado("PENDIENTE");
    }

    public List<SesionTutoria> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return sesionTutoriaRepository.findByFechaHoraBetween(inicio, fin);
    }

    public Double calcularTotalDia(LocalDateTime inicio, LocalDateTime fin) {
        List<SesionTutoria> sesiones = obtenerPorFecha(inicio, fin);
        return sesiones.stream()
                .mapToDouble(s -> s.getCostoTotal() != null ? s.getCostoTotal() : 0.0)
                .sum();
    }

    public void eliminarSesion(Long id) {
        sesionTutoriaRepository.deleteById(id);
    }
}