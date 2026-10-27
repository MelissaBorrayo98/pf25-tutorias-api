package com.example.pf25tutorias.controller;

import com.example.pf25tutorias.dto.SesionTutoriaRequestDTO;
import com.example.pf25tutorias.dto.SesionTutoriaResponseDTO;
import com.example.pf25tutorias.service.SesionTutoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba la capa HTTP (códigos de respuesta y formato de errores) sin base de datos.
 */
@WebMvcTest(SesionTutoriaController.class)
class SesionTutoriaControllerTest {

    private static final String JSON_VALIDO =
            "{\"fechaHora\":\"2026-11-10T16:00:00\",\"duracion\":2.0,\"estudiante\":\"Juan Pérez\",\"estado\":\"PENDIENTE\"}";

    private static final String JSON_DURACION_NEGATIVA =
            "{\"fechaHora\":\"2026-11-10T16:00:00\",\"duracion\":-1.5,\"estudiante\":\"Juan Pérez\",\"estado\":\"PENDIENTE\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SesionTutoriaService sesionService;

    @Test
    void crearSesion_valida_responde201ConCostoCalculado() throws Exception {
        SesionTutoriaResponseDTO respuesta = new SesionTutoriaResponseDTO();
        respuesta.setId(1L);
        respuesta.setCostoTotal(100.0);
        respuesta.setEstado("PENDIENTE");
        when(sesionService.crearSesion(eq(1L), any(SesionTutoriaRequestDTO.class))).thenReturn(respuesta);

        mockMvc.perform(post("/api/sesiones/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.costoTotal").value(100.0))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void crearSesion_conDuracionNegativa_responde400ConDetalleDelCampo() throws Exception {
        mockMvc.perform(post("/api/sesiones/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_DURACION_NEGATIVA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.duracion").value("La duración debe ser mayor a 0 horas."));
    }

    @Test
    void crearSesion_conHorarioOcupado_responde409() throws Exception {
        when(sesionService.crearSesion(eq(1L), any(SesionTutoriaRequestDTO.class)))
                .thenThrow(new IllegalStateException("Ya existe una sesión programada en esa fecha y hora."));

        mockMvc.perform(post("/api/sesiones/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ya existe una sesión programada en esa fecha y hora."));
    }

    @Test
    void crearSesion_conMateriaInexistente_responde404() throws Exception {
        when(sesionService.crearSesion(eq(99L), any(SesionTutoriaRequestDTO.class)))
                .thenThrow(new NoSuchElementException("La materia con ID 99 no fue encontrada."));

        mockMvc.perform(post("/api/sesiones/materia/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("La materia con ID 99 no fue encontrada."));
    }

    @Test
    void crearSesion_conJsonMalFormado_responde400() throws Exception {
        mockMvc.perform(post("/api/sesiones/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{esto no es json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerSesionInexistente_responde404() throws Exception {
        when(sesionService.obtenerPorId(99L))
                .thenThrow(new NoSuchElementException("La sesión con ID 99 no fue encontrada."));

        mockMvc.perform(get("/api/sesiones/99"))
                .andExpect(status().isNotFound());
    }
}
