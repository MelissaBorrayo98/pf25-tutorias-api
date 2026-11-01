package com.example.pf25tutorias.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SesionTutoriaRequestDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void iniciar() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private SesionTutoriaRequestDTO dtoValido() {
        SesionTutoriaRequestDTO dto = new SesionTutoriaRequestDTO();
        dto.setFechaHora(LocalDateTime.of(2026, 11, 10, 16, 0));
        dto.setDuracion(2.0);
        dto.setEstudiante("Juan Pérez");
        dto.setEstado("PENDIENTE");
        return dto;
    }

    private boolean tieneErrorEn(Set<ConstraintViolation<SesionTutoriaRequestDTO>> errores, String campo) {
        return errores.stream().anyMatch(e -> e.getPropertyPath().toString().equals(campo));
    }

    @Test
    void dtoCorrecto_noTieneErrores() {
        assertTrue(validator.validate(dtoValido()).isEmpty());
    }

    @Test
    void duracionNegativa_esInvalida() {
        SesionTutoriaRequestDTO dto = dtoValido();
        dto.setDuracion(-1.5);
        assertTrue(tieneErrorEn(validator.validate(dto), "duracion"));
    }

    @Test
    void duracionCero_esInvalida() {
        SesionTutoriaRequestDTO dto = dtoValido();
        dto.setDuracion(0.0);
        assertTrue(tieneErrorEn(validator.validate(dto), "duracion"));
    }

    @Test
    void fechaVacia_esInvalida() {
        SesionTutoriaRequestDTO dto = dtoValido();
        dto.setFechaHora(null);
        assertTrue(tieneErrorEn(validator.validate(dto), "fechaHora"));
    }

    @Test
    void estudianteEnBlanco_esInvalido() {
        SesionTutoriaRequestDTO dto = dtoValido();
        dto.setEstudiante("   ");
        assertTrue(tieneErrorEn(validator.validate(dto), "estudiante"));
    }

    @Test
    void estadoDesconocido_esInvalido() {
        SesionTutoriaRequestDTO dto = dtoValido();
        dto.setEstado("INVENTADO");
        assertTrue(tieneErrorEn(validator.validate(dto), "estado"));
    }

    @Test
    void estadoVacio_esValidoPorqueSeAsignaPendiente() {
        SesionTutoriaRequestDTO dto = dtoValido();
        dto.setEstado(null);
        assertTrue(validator.validate(dto).isEmpty());
    }
}
