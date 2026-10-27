package com.example.pf25tutorias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * Datos que el cliente envía para registrar o actualizar una sesión.
 * El costo total NO se recibe: lo calcula el backend (duración x tarifaHora de la materia).
 */
public class SesionTutoriaRequestDTO {

    @NotNull(message = "La fecha y hora son obligatorias.")
    private LocalDateTime fechaHora;

    @NotNull(message = "La duración es obligatoria.")
    @Positive(message = "La duración debe ser mayor a 0 horas.")
    private Double duracion;

    @NotBlank(message = "El nombre del estudiante es obligatorio.")
    private String estudiante;

    // Opcional: si no se envía, se asigna "PENDIENTE"
    @Pattern(regexp = "(?i)PENDIENTE|PROGRAMADA|CONFIRMADA|COMPLETADA|CANCELADA",
            message = "El estado debe ser PENDIENTE, PROGRAMADA, CONFIRMADA, COMPLETADA o CANCELADA.")
    private String estado;

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Double getDuracion() {
        return duracion;
    }

    public void setDuracion(Double duracion) {
        this.duracion = duracion;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
