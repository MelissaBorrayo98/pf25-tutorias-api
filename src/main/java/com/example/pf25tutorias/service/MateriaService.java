package com.example.pf25tutorias.service;

import com.example.pf25tutorias.dto.MateriaResponseDTO;
import com.example.pf25tutorias.dto.SesionTutoriaResponseDTO;
import com.example.pf25tutorias.model.Materia;
import com.example.pf25tutorias.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    public List<MateriaResponseDTO> listarTodas() {
        return materiaRepository.findAll().stream().map(materia -> {
            MateriaResponseDTO dto = new MateriaResponseDTO();
            dto.setId(materia.getId());
            dto.setNombre(materia.getNombre());
            dto.setDescripcion(materia.getDescripcion());
            dto.setTarifaHora(materia.getTarifaHora());

            // Mapear las sesiones de la materia usando el ResponseDTO de sesiones y asignando el nombre correcto
            List<SesionTutoriaResponseDTO> sesionesDTO = materia.getSesiones().stream().map(sesion -> {
                SesionTutoriaResponseDTO sesionDto = new SesionTutoriaResponseDTO();
                sesionDto.setId(sesion.getId());
                sesionDto.setFechaHora(sesion.getFechaHora());
                sesionDto.setDuracion(sesion.getDuracion());
                sesionDto.setEstudiante(sesion.getEstudiante());
                sesionDto.setEstado(sesion.getEstado());
                sesionDto.setCostoTotal(sesion.getCostoTotal());
                sesionDto.setMateriaNombre(materia.getNombre()); // Aquí asigna el nombre de la materia correctamente
                return sesionDto;
            }).collect(Collectors.toList());

            dto.setSesiones(sesionesDTO);
            return dto;
        }).collect(Collectors.toList());
    }

    public Materia guardar(Materia materia) {
        return materiaRepository.save(materia);
    }
}