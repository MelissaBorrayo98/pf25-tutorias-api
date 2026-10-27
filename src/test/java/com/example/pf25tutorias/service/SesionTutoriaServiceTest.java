package com.example.pf25tutorias.service;

import com.example.pf25tutorias.dto.SesionTutoriaRequestDTO;
import com.example.pf25tutorias.dto.SesionTutoriaResponseDTO;
import com.example.pf25tutorias.model.Materia;
import com.example.pf25tutorias.model.SesionTutoria;
import com.example.pf25tutorias.repository.MateriaRepository;
import com.example.pf25tutorias.repository.SesionTutoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SesionTutoriaServiceTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 11, 10, 16, 0);

    @Mock
    private SesionTutoriaRepository sesionRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @InjectMocks
    private SesionTutoriaService service;

    private Materia materia(Long id, String nombre, double tarifa) {
        return new Materia(id, nombre, "descripción", tarifa);
    }

    private SesionTutoriaRequestDTO request(double duracion, String estado) {
        SesionTutoriaRequestDTO dto = new SesionTutoriaRequestDTO();
        dto.setFechaHora(FECHA);
        dto.setDuracion(duracion);
        dto.setEstudiante("Juan Pérez");
        dto.setEstado(estado);
        return dto;
    }

    private SesionTutoria sesion(double costo, String estado) {
        SesionTutoria s = new SesionTutoria();
        s.setCostoTotal(costo);
        s.setEstado(estado);
        s.setFechaHora(FECHA);
        return s;
    }

    @Test
    void crearSesion_calculaElCostoAutomaticamenteYAsignaEstadoPorDefecto() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", 50.0)));
        when(sesionRepository.existsByFechaHora(FECHA)).thenReturn(false);
        when(sesionRepository.save(any(SesionTutoria.class))).thenAnswer(inv -> inv.getArgument(0));

        SesionTutoriaResponseDTO resultado = service.crearSesion(1L, request(2.0, null));

        assertEquals(100.0, resultado.getCostoTotal());      // 2 horas x Q50
        assertEquals("PENDIENTE", resultado.getEstado());      // estado por defecto
        assertEquals("Matemáticas", resultado.getMateriaNombre());
    }

    @Test
    void crearSesion_conDuracionCero_lanzaIllegalArgumentYNoGuarda() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", 50.0)));

        assertThrows(IllegalArgumentException.class, () -> service.crearSesion(1L, request(0.0, null)));

        verify(sesionRepository, never()).save(any(SesionTutoria.class));
    }

    @Test
    void crearSesion_conDuracionNegativa_lanzaIllegalArgumentYNoGuarda() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", 50.0)));

        assertThrows(IllegalArgumentException.class, () -> service.crearSesion(1L, request(-1.5, null)));

        verify(sesionRepository, never()).save(any(SesionTutoria.class));
    }

    @Test
    void crearSesion_enHorarioOcupado_lanzaIllegalStateYNoGuarda() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", 50.0)));
        when(sesionRepository.existsByFechaHora(FECHA)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.crearSesion(1L, request(2.0, null)));

        verify(sesionRepository, never()).save(any(SesionTutoria.class));
    }

    @Test
    void crearSesion_conMateriaInexistente_lanzaNoSuchElement() {
        when(materiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.crearSesion(99L, request(2.0, null)));
    }

    @Test
    void actualizarSesion_recalculaElCostoYNormalizaElEstado() {
        SesionTutoria existente = sesion(100.0, "PENDIENTE");
        existente.setId(5L);
        existente.setMateria(materia(2L, "Programación", 60.0));

        when(sesionRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(sesionRepository.existsByFechaHoraAndIdNot(FECHA, 5L)).thenReturn(false);
        when(sesionRepository.save(any(SesionTutoria.class))).thenAnswer(inv -> inv.getArgument(0));

        SesionTutoriaResponseDTO resultado = service.actualizarSesion(5L, request(3.0, "completada"));

        assertEquals(180.0, resultado.getCostoTotal());        // 3 horas x Q60
        assertEquals("COMPLETADA", resultado.getEstado());
    }

    @Test
    void actualizarSesion_enHorarioDeOtraSesion_lanzaIllegalState() {
        SesionTutoria existente = sesion(100.0, "PENDIENTE");
        existente.setId(5L);
        existente.setMateria(materia(2L, "Programación", 60.0));

        when(sesionRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(sesionRepository.existsByFechaHoraAndIdNot(FECHA, 5L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.actualizarSesion(5L, request(2.0, null)));
    }

    @Test
    void calcularTotalDia_sumaLasSesionesYExcluyeLasCanceladas() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 10, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 10, 23, 59);
        when(sesionRepository.findByFechaHoraBetween(inicio, fin)).thenReturn(List.of(
                sesion(100.0, "PENDIENTE"),
                sesion(50.0, "COMPLETADA"),
                sesion(70.0, "CANCELADA")));

        assertEquals(150.0, service.calcularTotalDia(inicio, fin));
    }

    @Test
    void obtenerPorFecha_conRangoInvertido_lanzaIllegalArgument() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 11, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 10, 0, 0);

        assertThrows(IllegalArgumentException.class, () -> service.obtenerPorFecha(inicio, fin));
    }

    @Test
    void eliminarSesion_inexistente_lanzaNoSuchElement() {
        when(sesionRepository.existsById(77L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.eliminarSesion(77L));
        verify(sesionRepository, never()).deleteById(any(Long.class));
    }
}
