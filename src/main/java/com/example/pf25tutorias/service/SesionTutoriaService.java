package com.example.pf25tutorias.service;

import com.example.pf25tutorias.dto.SesionTutoriaRequestDTO;
import com.example.pf25tutorias.dto.SesionTutoriaResponseDTO;
import com.example.pf25tutorias.model.Materia;
import com.example.pf25tutorias.model.SesionTutoria;
import com.example.pf25tutorias.repository.MateriaRepository;
import com.example.pf25tutorias.repository.SesionTutoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class SesionTutoriaService {

    @Autowired
    private SesionTutoriaRepository sesionRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    // 1. Listar todas
    public List<SesionTutoriaResponseDTO> listarTodas() {
        return sesionRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar entidad pura por ID (Lanza NoSuchElementException -> Activa HTTP 404)
    public SesionTutoria buscarEntidadPorId(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("La sesión con ID " + id + " no fue encontrada."));
    }

    // 2. Crear sesión usando DTO y Materia ID
    public SesionTutoriaResponseDTO crearSesion(Long materiaId, SesionTutoriaRequestDTO requestDTO) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new NoSuchElementException("La materia con ID " + materiaId + " no fue encontrada."));

        SesionTutoria sesion = new SesionTutoria();
        sesion.setFechaHora(requestDTO.getFechaHora());
        sesion.setDuracion(requestDTO.getDuracion());
        sesion.setEstudiante(requestDTO.getEstudiante());
        sesion.setEstado(requestDTO.getEstado());
        sesion.setMateria(materia);

        SesionTutoria guardada = sesionRepository.save(sesion);
        return convertirAResponseDTO(guardada);
    }

    // 3. Obtener por Materia
    public List<SesionTutoriaResponseDTO> obtenerPorMateria(Long materiaId) {
        return sesionRepository.findAll().stream()
                .filter(s -> s.getMateria() != null && s.getMateria().getId().equals(materiaId))
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // 4. Obtener pendientes
    public List<SesionTutoriaResponseDTO> obtenerPendientes() {
        return sesionRepository.findAll().stream()
                .filter(s -> "PENDIENTE".equalsIgnoreCase(s.getEstado()))
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // 5. Obtener por rango de fecha
    public List<SesionTutoriaResponseDTO> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return sesionRepository.findAll().stream()
                .filter(s -> s.getFechaHora() != null &&
                        !s.getFechaHora().isBefore(inicio) &&
                        !s.getFechaHora().isAfter(fin))
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // 6. Calcular total del día
    public Double calcularTotalDia(LocalDateTime inicio, LocalDateTime fin) {
        List<SesionTutoriaResponseDTO> sesiones = obtenerPorFecha(inicio, fin);
        return sesiones.stream()
                .mapToDouble(s -> s.getCostoTotal() != null ? s.getCostoTotal() : 0.0)
                .sum();
    }

    // 7. Eliminar sesión (Valida existencia -> HTTP 404)
    public void eliminarSesion(Long id) {
        if (!sesionRepository.existsById(id)) {
            throw new NoSuchElementException("No se puede eliminar. La sesión con ID " + id + " no existe.");
        }
        sesionRepository.deleteById(id);
    }

    // 8. Actualizar sesión (Valida existencia -> HTTP 404)
    public SesionTutoriaResponseDTO actualizarSesion(Long id, SesionTutoriaRequestDTO requestDTO) {
        SesionTutoria sesionExistente = buscarEntidadPorId(id); // Lanza NoSuchElementException si no existe -> 404

        sesionExistente.setFechaHora(requestDTO.getFechaHora());
        sesionExistente.setDuracion(requestDTO.getDuracion());
        sesionExistente.setEstudiante(requestDTO.getEstudiante());
        sesionExistente.setEstado(requestDTO.getEstado());

        SesionTutoria actualizada = sesionRepository.save(sesionExistente);
        return convertirAResponseDTO(actualizada);
    }

    // Método auxiliar privado para convertir Entidad a ResponseDTO
    private SesionTutoriaResponseDTO convertirAResponseDTO(SesionTutoria sesion) {
        SesionTutoriaResponseDTO dto = new SesionTutoriaResponseDTO();
        dto.setId(sesion.getId());
        dto.setFechaHora(sesion.getFechaHora());
        dto.setDuracion(sesion.getDuracion());
        dto.setEstudiante(sesion.getEstudiante());
        dto.setEstado(sesion.getEstado());
        dto.setCostoTotal(sesion.getCostoTotal());
        return dto;
    }
}