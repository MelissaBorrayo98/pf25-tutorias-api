package com.example.pf25tutorias.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "sesion_tutoria")
public class SesionTutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "duracion_horas", nullable = false)
    private Double duracion;

    @Column(nullable = false)
    private String estudiante;

    @Column(nullable = false, length = 50)
    private String estado;

    @Column(name = "costo_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoTotal;

    // El nombre de la materia se obtiene de esta relación (no se duplica en la tabla)
    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    public SesionTutoria() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Double getDuracion() { return duracion; }
    public void setDuracion(Double duracion) { this.duracion = duracion; }

    public String getEstudiante() { return estudiante; }
    public void setEstudiante(String estudiante) { this.estudiante = estudiante; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public Materia getMateria() { return materia; }
    public void setMateria(Materia materia) { this.materia = materia; }
}
