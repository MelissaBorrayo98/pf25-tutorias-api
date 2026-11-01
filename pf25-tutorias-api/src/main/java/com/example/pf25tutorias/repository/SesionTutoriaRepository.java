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

    /**
     * Regla de disponibilidad: ¿existe una sesión en esa fecha y hora cuyo estado sea distinto
     * del indicado? Se usa con "CANCELADA": las sesiones canceladas NO ocupan el horario.
     * (En PostgreSQL la misma regla la aplica un índice único parcial: WHERE estado <> 'CANCELADA'.)
     */
    boolean existsByFechaHoraAndEstadoNot(LocalDateTime fechaHora, String estado);

    // Igual que la anterior pero ignorando la propia sesión (al actualizar)
    boolean existsByFechaHoraAndIdNotAndEstadoNot(LocalDateTime fechaHora, Long id, String estado);
}
