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

import java.math.BigDecimal;
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
    private static final String CANCELADA = "CANCELADA";

    @Mock
    private SesionTutoriaRepository sesionRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @InjectMocks
    private SesionTutoriaService service;

    private Materia materia(Long id, String nombre, String tarifa) {
        return new Materia(id, nombre, "descripción", new BigDecimal(tarifa));
    }

    private SesionTutoriaRequestDTO request(double duracion, String estado) {
        SesionTutoriaRequestDTO dto = new SesionTutoriaRequestDTO();
        dto.setFechaHora(FECHA);
        dto.setDuracion(duracion);
        dto.setEstudiante("Juan Pérez");
        dto.setEstado(estado);
        return dto;
    }

    private SesionTutoria sesion(String costo, String estado) {
        SesionTutoria s = new SesionTutoria();
        s.setCostoTotal(new BigDecimal(costo));
        s.setEstado(estado);
        s.setFechaHora(FECHA);
        return s;
    }

    // Compara importes sin depender de la escala (100.0 == 100.00)
    private void assertMonto(String esperado, BigDecimal actual) {
        assertEquals(0, new BigDecimal(esperado).compareTo(actual),
                "Se esperaba " + esperado + " pero fue " + actual);
    }

    // ------------------------------------------------------------ Crear

    @Test
    void crearSesion_calculaElCostoConBigDecimalYAsignaEstadoPorDefecto() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", "50.00")));
        when(sesionRepository.existsByFechaHoraAndEstadoNot(FECHA, CANCELADA)).thenReturn(false);
        when(sesionRepository.save(any(SesionTutoria.class))).thenAnswer(inv -> inv.getArgument(0));

        SesionTutoriaResponseDTO resultado = service.crearSesion(1L, request(2.0, null));

        assertMonto("100.00", resultado.getCostoTotal());   // 2 horas x Q50.00
        assertEquals(2, resultado.getCostoTotal().scale());  // siempre 2 decimales
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("Matemáticas", resultado.getMateriaNombre());
        // La validación de horario ignora las sesiones canceladas
        verify(sesionRepository).existsByFechaHoraAndEstadoNot(FECHA, CANCELADA);
    }

    @Test
    void crearSesion_redondeaElCostoADosDecimales() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", "33.33")));
        when(sesionRepository.existsByFechaHoraAndEstadoNot(FECHA, CANCELADA)).thenReturn(false);
        when(sesionRepository.save(any(SesionTutoria.class))).thenAnswer(inv -> inv.getArgument(0));

        SesionTutoriaResponseDTO resultado = service.crearSesion(1L, request(1.5, null));

        assertMonto("50.00", resultado.getCostoTotal());     // 33.33 x 1.5 = 49.995 -> 50.00
    }

    @Test
    void crearSesion_conDuracionCero_lanzaIllegalArgumentYNoGuarda() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", "50.00")));

        assertThrows(IllegalArgumentException.class, () -> service.crearSesion(1L, request(0.0, null)));

        verify(sesionRepository, never()).save(any(SesionTutoria.class));
    }

    @Test
    void crearSesion_conDuracionNegativa_lanzaIllegalArgumentYNoGuarda() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", "50.00")));

        assertThrows(IllegalArgumentException.class, () -> service.crearSesion(1L, request(-1.5, null)));

        verify(sesionRepository, never()).save(any(SesionTutoria.class));
    }

    @Test
    void crearSesion_enHorarioOcupadoPorSesionActiva_lanzaIllegalStateYNoGuarda() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", "50.00")));
        when(sesionRepository.existsByFechaHoraAndEstadoNot(FECHA, CANCELADA)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.crearSesion(1L, request(2.0, null)));

        verify(sesionRepository, never()).save(any(SesionTutoria.class));
    }

    @Test
    void crearSesion_canceladaNoValidaChoqueDeHorario() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia(1L, "Matemáticas", "50.00")));
        when(sesionRepository.save(any(SesionTutoria.class))).thenAnswer(inv -> inv.getArgument(0));

        SesionTutoriaResponseDTO resultado = service.crearSesion(1L, request(2.0, "cancelada"));

        assertEquals("CANCELADA", resultado.getEstado());
        verify(sesionRepository, never()).existsByFechaHoraAndEstadoNot(any(LocalDateTime.class), any(String.class));
    }

    @Test
    void crearSesion_conMateriaInexistente_lanzaNoSuchElement() {
        when(materiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.crearSesion(99L, request(2.0, null)));
    }

    // -------------------------------------------------------- Actualizar

    @Test
    void actualizarSesion_recalculaElCostoYNormalizaElEstado() {
        SesionTutoria existente = sesion("100.00", "PENDIENTE");
        existente.setId(5L);
        existente.setMateria(materia(2L, "Programación", "60.00"));

        when(sesionRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(sesionRepository.existsByFechaHoraAndIdNotAndEstadoNot(FECHA, 5L, CANCELADA)).thenReturn(false);
        when(sesionRepository.save(any(SesionTutoria.class))).thenAnswer(inv -> inv.getArgument(0));

        SesionTutoriaResponseDTO resultado = service.actualizarSesion(5L, request(3.0, "completada"));

        assertMonto("180.00", resultado.getCostoTotal());    // 3 horas x Q60.00
        assertEquals("COMPLETADA", resultado.getEstado());
    }

    @Test
    void actualizarSesion_enHorarioDeOtraSesionActiva_lanzaIllegalState() {
        SesionTutoria existente = sesion("100.00", "PENDIENTE");
        existente.setId(5L);
        existente.setMateria(materia(2L, "Programación", "60.00"));

        when(sesionRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(sesionRepository.existsByFechaHoraAndIdNotAndEstadoNot(FECHA, 5L, CANCELADA)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.actualizarSesion(5L, request(2.0, null)));
    }

    @Test
    void actualizarSesion_reactivarCanceladaConHorarioTomado_lanzaIllegalState() {
        SesionTutoria cancelada = sesion("100.00", "CANCELADA");
        cancelada.setId(5L);
        cancelada.setMateria(materia(2L, "Programación", "60.00"));

        when(sesionRepository.findById(5L)).thenReturn(Optional.of(cancelada));
        when(sesionRepository.existsByFechaHoraAndIdNotAndEstadoNot(FECHA, 5L, CANCELADA)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.actualizarSesion(5L, request(2.0, "PENDIENTE")));
    }

    @Test
    void actualizarSesion_aCanceladaNoValidaChoqueDeHorario() {
        SesionTutoria existente = sesion("100.00", "PENDIENTE");
        existente.setId(5L);
        existente.setMateria(materia(2L, "Programación", "60.00"));

        when(sesionRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(sesionRepository.save(any(SesionTutoria.class))).thenAnswer(inv -> inv.getArgument(0));

        SesionTutoriaResponseDTO resultado = service.actualizarSesion(5L, request(2.0, "CANCELADA"));

        assertEquals("CANCELADA", resultado.getEstado());
        verify(sesionRepository, never()).existsByFechaHoraAndIdNotAndEstadoNot(
                any(LocalDateTime.class), any(Long.class), any(String.class));
    }

    // ------------------------------------------------ Consultas y eliminar

    @Test
    void calcularTotalDia_sumaLasSesionesYExcluyeLasCanceladas() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 10, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 10, 23, 59);
        when(sesionRepository.findByFechaHoraBetween(inicio, fin)).thenReturn(List.of(
                sesion("100.00", "PENDIENTE"),
                sesion("50.50", "COMPLETADA"),
                sesion("70.00", "CANCELADA")));

        assertMonto("150.50", service.calcularTotalDia(inicio, fin));
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
