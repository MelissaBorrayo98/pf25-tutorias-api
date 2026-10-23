package com.example.pf25tutorias.dto;

import java.util.List;

public class MateriaResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double tarifaHora;
    private List<SesionTutoriaResponseDTO> sesiones;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<SesionTutoriaResponseDTO> getSesiones() {
        return sesiones;
    }

    public void setSesiones(List<SesionTutoriaResponseDTO> sesiones) {
        this.sesiones = sesiones;
    }
}