package com.example.pf25tutorias.service;

import com.example.pf25tutorias.dto.MateriaDto;
import com.example.pf25tutorias.model.Materia;
import com.example.pf25tutorias.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    // Listar todas las materias
    public List<Materia> listarMaterias() {
        return materiaRepository.findAll();
    }

    // Buscar materia por ID
    public Optional<Materia> obtenerMateriaPorId(Long id) {
        return materiaRepository.findById(id);
    }

    // Guardar materia usando DTO
    public Materia guardarMateriaDto(MateriaDto dto) {
        Materia materia = new Materia();
        materia.setNombre(dto.getNombre());
        materia.setDescripcion(dto.getDescripcion());
        materia.setTarifaHora(dto.getTarifaHora());
        return materiaRepository.save(materia);
    }

    // Actualizar materia usando DTO
    public Materia actualizarMateriaDto(Long id, MateriaDto dto) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + id));

        materia.setNombre(dto.getNombre());
        materia.setDescripcion(dto.getDescripcion());
        materia.setTarifaHora(dto.getTarifaHora());

        return materiaRepository.save(materia);
    }

    // Eliminar materia
    public void eliminarMateria(Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new RuntimeException("Materia no encontrada");
        }
        materiaRepository.deleteById(id);
    }
}