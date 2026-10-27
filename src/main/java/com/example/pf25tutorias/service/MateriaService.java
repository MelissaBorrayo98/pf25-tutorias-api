package com.example.pf25tutorias.service;

import com.example.pf25tutorias.dto.MateriaDto;
import com.example.pf25tutorias.dto.MateriaResponseDTO;
import com.example.pf25tutorias.dto.SesionTutoriaResponseDTO;
import com.example.pf25tutorias.model.Materia;
import com.example.pf25tutorias.repository.MateriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MateriaService {

    private final MateriaRepository materiaRepository;

    public MateriaService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    public List<MateriaResponseDTO> listarTodas() {
        return materiaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public MateriaResponseDTO obtenerPorId(Long id) {
        return convertirAResponseDTO(buscarEntidadPorId(id));
    }

    @Transactional
    public MateriaResponseDTO crear(MateriaDto dto) {
        if (materiaRepository.existsByNombreIgnoreCase(dto.getNombre().trim())) {
            throw new IllegalStateException("Ya existe una materia con el nombre '" + dto.getNombre().trim() + "'.");
        }
        Materia materia = new Materia();
        aplicarDatos(materia, dto);
        return convertirAResponseDTO(materiaRepository.save(materia));
    }

    @Transactional
    public MateriaResponseDTO actualizar(Long id, MateriaDto dto) {
        Materia materia = buscarEntidadPorId(id);
        if (materiaRepository.existsByNombreIgnoreCaseAndIdNot(dto.getNombre().trim(), id)) {
            throw new IllegalStateException("Ya existe otra materia con el nombre '" + dto.getNombre().trim() + "'.");
        }
        aplicarDatos(materia, dto);
        return convertirAResponseDTO(materiaRepository.save(materia));
    }

    // Al eliminar una materia también se eliminan sus sesiones (cascade en la entidad)
    @Transactional
    public void eliminar(Long id) {
        Materia materia = buscarEntidadPorId(id);
        materiaRepository.delete(materia);
    }

    private Materia buscarEntidadPorId(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("La materia con ID " + id + " no fue encontrada."));
    }

    private void aplicarDatos(Materia materia, MateriaDto dto) {
        materia.setNombre(dto.getNombre().trim());
        materia.setDescripcion(dto.getDescripcion());
        materia.setTarifaHora(dto.getTarifaHora());
    }

    private MateriaResponseDTO convertirAResponseDTO(Materia materia) {
        MateriaResponseDTO dto = new MateriaResponseDTO();
        dto.setId(materia.getId());
        dto.setNombre(materia.getNombre());
        dto.setDescripcion(materia.getDescripcion());
        dto.setTarifaHora(materia.getTarifaHora());

        List<SesionTutoriaResponseDTO> sesionesDTO = materia.getSesiones().stream().map(sesion -> {
            SesionTutoriaResponseDTO s = new SesionTutoriaResponseDTO();
            s.setId(sesion.getId());
            s.setFechaHora(sesion.getFechaHora());
            s.setDuracion(sesion.getDuracion());
            s.setEstudiante(sesion.getEstudiante());
            s.setEstado(sesion.getEstado());
            s.setCostoTotal(sesion.getCostoTotal());
            s.setMateriaId(materia.getId());
            s.setMateriaNombre(materia.getNombre());
            return s;
        }).collect(Collectors.toList());

        dto.setSesiones(sesionesDTO);
        return dto;
    }
}
