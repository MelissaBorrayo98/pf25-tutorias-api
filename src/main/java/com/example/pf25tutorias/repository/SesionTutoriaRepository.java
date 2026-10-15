package com.example.pf25tutorias.repository;

import com.example.pf25tutorias.model.SesionTutoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SesionTutoriaRepository extends JpaRepository<SesionTutoria, Long> {

    List<SesionTutoria> findByMateriaId(Long materiaId);

    List<SesionTutoria> findByEstado(String estado);

    List<SesionTutoria> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}