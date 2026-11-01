package com.example.pf25tutorias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

public class MateriaDto {

    @NotBlank(message = "El nombre de la materia es obligatorio.")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La tarifa por hora es obligatoria.")
    @Positive(message = "La tarifa por hora debe ser mayor a 0.")
    @Digits(integer = 8, fraction = 2, message = "La tarifa admite como máximo 2 decimales.")
    private BigDecimal tarifaHora;

    // Constructor vacío
    public MateriaDto() {}

    // Constructores con parámetros (opcional pero útil)
    public MateriaDto(String nombre, String descripcion, BigDecimal tarifaHora) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tarifaHora = tarifaHora;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(BigDecimal tarifaHora) {
        this.tarifaHora = tarifaHora;
    }
}