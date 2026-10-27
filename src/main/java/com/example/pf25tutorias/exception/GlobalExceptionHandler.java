package com.example.pf25tutorias.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Respuestas de error estandarizadas. Formato:
 * { "status": 400, "error": "mensaje", "errores": { "campo": "detalle" } }   (errores solo en validaciones)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: recurso inexistente (ID de sesión o de materia que no existe)
    @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(Exception ex) {
        String mensaje = (ex instanceof NoSuchElementException && ex.getMessage() != null)
                ? ex.getMessage()
                : "El recurso solicitado no fue encontrado.";
        return construir(HttpStatus.NOT_FOUND, mensaje, null);
    }

    // 400: fallan las anotaciones de validación del DTO (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = (error instanceof FieldError) ? ((FieldError) error).getField() : error.getObjectName();
            errores.put(campo, error.getDefaultMessage());
        });
        return construir(HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos.", errores);
    }

    // 400: JSON mal formado, fecha con formato incorrecto, texto donde va un número, etc.
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> handleFormato(Exception ex) {
        return construir(HttpStatus.BAD_REQUEST,
                "La petición tiene un formato incorrecto. Revise el JSON, las fechas (yyyy-MM-ddTHH:mm:ss) y los números.",
                null);
    }

    // 400: regla de negocio incumplida por datos inválidos (ej. duración <= 0, rango de fechas invertido)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleReglaDatos(IllegalArgumentException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // 409: conflicto con el estado actual (ej. horario ya ocupado, materia duplicada)
    @ExceptionHandler({IllegalStateException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, Object>> handleConflicto(Exception ex) {
        String mensaje = (ex instanceof IllegalStateException && ex.getMessage() != null)
                ? ex.getMessage()
                : "La operación viola una restricción de la base de datos (dato duplicado o relacionado).";
        return construir(HttpStatus.CONFLICT, mensaje, null);
    }

    private ResponseEntity<Map<String, Object>> construir(HttpStatus status, String mensaje, Map<String, String> errores) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("status", status.value());
        cuerpo.put("error", mensaje);
        if (errores != null) {
            cuerpo.put("errores", errores);
        }
        return ResponseEntity.status(status).body(cuerpo);
    }
}
