package com.example.pf25tutorias.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MateriaDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void iniciar() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private boolean tieneErrorEn(Set<ConstraintViolation<MateriaDto>> errores, String campo) {
        return errores.stream().anyMatch(e -> e.getPropertyPath().toString().equals(campo));
    }

    @Test
    void materiaCorrecta_noTieneErrores() {
        MateriaDto dto = new MateriaDto("Bases de Datos I", "SQL", new BigDecimal("200.00"));
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void nombreEnBlanco_esInvalido() {
        MateriaDto dto = new MateriaDto("  ", "SQL", new BigDecimal("200.00"));
        assertTrue(tieneErrorEn(validator.validate(dto), "nombre"));
    }

    @Test
    void tarifaNula_esInvalida() {
        MateriaDto dto = new MateriaDto("Redes", "TCP/IP", null);
        assertTrue(tieneErrorEn(validator.validate(dto), "tarifaHora"));
    }

    @Test
    void tarifaCero_esInvalida() {
        MateriaDto dto = new MateriaDto("Redes", "TCP/IP", BigDecimal.ZERO);
        assertTrue(tieneErrorEn(validator.validate(dto), "tarifaHora"));
    }

    @Test
    void tarifaNegativa_esInvalida() {
        MateriaDto dto = new MateriaDto("Redes", "TCP/IP", new BigDecimal("-10"));
        assertTrue(tieneErrorEn(validator.validate(dto), "tarifaHora"));
    }

    @Test
    void tarifaConTresDecimales_esInvalida() {
        MateriaDto dto = new MateriaDto("Redes", "TCP/IP", new BigDecimal("150.555"));
        assertTrue(tieneErrorEn(validator.validate(dto), "tarifaHora"));
    }
}
