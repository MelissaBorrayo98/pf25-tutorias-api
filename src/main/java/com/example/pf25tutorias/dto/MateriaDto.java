package com.example.pf25tutorias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MateriaDto {

    @NotBlank(message = "El nombre de la materia es obligatorio.")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La tarifa por hora es obligatoria.")
    @Positive(message = "La tarifa por hora debe ser mayor a 0.")
    private Double tarifaHora;

    // Constructor vacío
    public MateriaDto() {}

    // Constructores con parámetros (opcional pero útil)
    public MateriaDto(String nombre, String descripcion, Double tarifaHora) {
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

    public Double getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(Double tarifaHora) {
        this.tarifaHora = tarifaHora;
    }
}