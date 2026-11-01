package com.example.pf25tutorias.service;

import com.example.pf25tutorias.dto.SesionTutoriaRequestDTO;
import com.example.pf25tutorias.dto.SesionTutoriaResponseDTO;
import com.example.pf25tutorias.model.Materia;
import com.example.pf25tutorias.model.SesionTutoria;
import com.example.pf25tutorias.repository.MateriaRepository;
import com.example.pf25tutorias.repository.SesionTutoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SesionTutoriaService {

    public static final String ESTADO_POR_DEFECTO = "PENDIENTE";
    public static final String ESTADO_CANCELADA = "CANCELADA";
    public static final String MSG_HORARIO_OCUPADO =
            "Ya existe una sesión activa en esa fecha y hora. Las sesiones CANCELADAS no bloquean el horario.";

    private final SesionTutoriaRepository sesionRepository;
    private final MateriaRepository materiaRepository;

    public SesionTutoriaService(SesionTutoriaRepository sesionRepository, MateriaRepository materiaRepository) {
        this.sesionRepository = sesionRepository;
        this.materiaRepository = materiaRepository;
    }

    // ---------------------------------------------------------------- Consultas

    public List<SesionTutoriaResponseDTO> listarTodas() {
        return convertirLista(sesionRepository.findAll());
    }

    public SesionTutoriaResponseDTO obtenerPorId(Long id) {
        return convertirAResponseDTO(buscarEntidadPorId(id));
    }

    public SesionTutoria buscarEntidadPorId(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("La sesión con ID " + id + " no fue encontrada."));
    }

    public List<SesionTutoriaResponseDTO> obtenerPorMateria(Long materiaId) {
        if (!materiaRepository.existsById(materiaId)) {
            throw new NoSuchElementException("La materia con ID " + materiaId + " no fue encontrada.");
        }
        return convertirLista(sesionRepository.findByMateriaId(materiaId));
    }

    public List<SesionTutoriaResponseDTO> obtenerPendientes() {
        return convertirLista(sesionRepository.findByEstado(ESTADO_POR_DEFECTO));
    }

    public List<SesionTutoriaResponseDTO> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        validarRango(inicio, fin);
        return convertirLista(sesionRepository.findByFechaHoraBetween(inicio, fin));
    }

    /**
     * Indicador: total esperado de ingresos en el rango indicado.
     * Las sesiones CANCELADAS no se consideran ingreso esperado.
     */
    public BigDecimal calcularTotalDia(LocalDateTime inicio, LocalDateTime fin) {
        validarRango(inicio, fin);
        return sesionRepository.findByFechaHoraBetween(inicio, fin).stream()
                .filter(s -> !esCancelada(s.getEstado()))
                .map(s -> s.getCostoTotal() != null ? s.getCostoTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ------------------------------------------------------------ Operaciones

    @Transactional
    public SesionTutoriaResponseDTO crearSesion(Long materiaId, SesionTutoriaRequestDTO dto) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new NoSuchElementException("La materia con ID " + materiaId + " no fue encontrada."));

        validarDuracion(dto.getDuracion());
        validarFecha(dto.getFechaHora());

        String estado = normalizarEstado(dto.getEstado(), null);

        // Regla de disponibilidad: una sesión activa no puede compartir fecha y hora con otra activa.
        // Una sesión CANCELADA no ocupa el horario (ni lo bloquea ni se bloquea).
        if (!esCancelada(estado)
                && sesionRepository.existsByFechaHoraAndEstadoNot(dto.getFechaHora(), ESTADO_CANCELADA)) {
            throw new IllegalStateException(MSG_HORARIO_OCUPADO);
        }

        SesionTutoria sesion = new SesionTutoria();
        sesion.setFechaHora(dto.getFechaHora());
        sesion.setDuracion(dto.getDuracion());
        sesion.setEstudiante(dto.getEstudiante().trim());
        sesion.setEstado(estado);
        sesion.setMateria(materia);
        sesion.setCostoTotal(calcularCosto(materia, dto.getDuracion()));

        return convertirAResponseDTO(sesionRepository.save(sesion));
    }

    @Transactional
    public SesionTutoriaResponseDTO actualizarSesion(Long id, SesionTutoriaRequestDTO dto) {
        SesionTutoria sesion = buscarEntidadPorId(id);

        validarDuracion(dto.getDuracion());
        validarFecha(dto.getFechaHora());

        String estado = normalizarEstado(dto.getEstado(), sesion.getEstado());

        // Si la sesión queda activa (por ejemplo, se reactiva una cancelada), su horario no debe estar tomado
        if (!esCancelada(estado)
                && sesionRepository.existsByFechaHoraAndIdNotAndEstadoNot(dto.getFechaHora(), id, ESTADO_CANCELADA)) {
            throw new IllegalStateException(MSG_HORARIO_OCUPADO);
        }

        sesion.setFechaHora(dto.getFechaHora());
        sesion.setDuracion(dto.getDuracion());
        sesion.setEstudiante(dto.getEstudiante().trim());
        sesion.setEstado(estado);
        sesion.setCostoTotal(calcularCosto(sesion.getMateria(), dto.getDuracion()));

        return convertirAResponseDTO(sesionRepository.save(sesion));
    }

    @Transactional
    public void eliminarSesion(Long id) {
        if (!sesionRepository.existsById(id)) {
            throw new NoSuchElementException("No se puede eliminar. La sesión con ID " + id + " no existe.");
        }
        sesionRepository.deleteById(id);
    }

    // ------------------------------------------------------- Reglas auxiliares

    private boolean esCancelada(String estado) {
        return ESTADO_CANCELADA.equalsIgnoreCase(estado);
    }

    private void validarDuracion(Double duracion) {
        if (duracion == null || duracion <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 horas.");
        }
    }

    private void validarFecha(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias.");
        }
    }

    private void validarRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Debe indicar la fecha de inicio y la fecha de fin.");
        }
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
    }

    // Si no se indica estado: "PENDIENTE" al crear, o se conserva el actual al actualizar
    private String normalizarEstado(String estadoNuevo, String estadoActual) {
        if (estadoNuevo != null && !estadoNuevo.isBlank()) {
            return estadoNuevo.trim().toUpperCase();
        }
        return estadoActual != null ? estadoActual : ESTADO_POR_DEFECTO;
    }

    // Cálculo automático con BigDecimal: duración (horas) x tarifa por hora, redondeado a 2 decimales
    private BigDecimal calcularCosto(Materia materia, Double duracion) {
        if (materia.getTarifaHora() == null) {
            throw new IllegalStateException("La materia '" + materia.getNombre() + "' no tiene tarifa por hora definida.");
        }
        return materia.getTarifaHora()
                .multiply(BigDecimal.valueOf(duracion))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ------------------------------------------------------------ Conversión

    private List<SesionTutoriaResponseDTO> convertirLista(List<SesionTutoria> sesiones) {
        return sesiones.stream()
                .sorted(Comparator.comparing(SesionTutoria::getFechaHora,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    private SesionTutoriaResponseDTO convertirAResponseDTO(SesionTutoria sesion) {
        SesionTutoriaResponseDTO dto = new SesionTutoriaResponseDTO();
        dto.setId(sesion.getId());
        dto.setFechaHora(sesion.getFechaHora());
        dto.setDuracion(sesion.getDuracion());
        dto.setEstudiante(sesion.getEstudiante());
        dto.setEstado(sesion.getEstado());
        dto.setCostoTotal(sesion.getCostoTotal());
        if (sesion.getMateria() != null) {
            dto.setMateriaId(sesion.getMateria().getId());
            dto.setMateriaNombre(sesion.getMateria().getNombre());
        }
        return dto;
    }
}
