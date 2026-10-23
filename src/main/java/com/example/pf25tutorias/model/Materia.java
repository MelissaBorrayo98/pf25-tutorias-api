package com.example.pf25tutorias.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "materia")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @Column(name = "tarifa_hora")
    private Double tarifaHora;

    // Relación bidireccional con SesionTutoria
    // JsonIgnoreProperties evita bucles infinitos en la serialización de JSON
    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("materia")
    private List<SesionTutoria> sesiones = new ArrayList<>();

    // Constructor vacío
    public Materia() {
    }

    // Constructor con parámetros
    public Materia(Long id, String nombre, String descripcion, Double tarifaHora) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tarifaHora = tarifaHora;
    }

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

    public List<SesionTutoria> getSesiones() {
        return sesiones;
    }

    public void setSesiones(List<SesionTutoria> sesiones) {
        this.sesiones = sesiones;
    }
}