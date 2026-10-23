
-- ==========================================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - PF-25 TUTORÍAS
-- ==========================================================

-- 1. Eliminar tablas en orden inverso si ya existen (para evitar errores de llaves foráneas)
DROP TABLE IF EXISTS sesion_tutoria CASCADE;
DROP TABLE IF EXISTS materia CASCADE;

-- 2. Crear tabla Materia
CREATE TABLE materia (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tarifa_hora NUMERIC(10, 2)
);

-- 3. Crear tabla Sesion Tutoria vinculada a Materia
CREATE TABLE sesion_tutoria (
    id SERIAL PRIMARY KEY,
    fecha_hora TIMESTAMP NOT NULL,
    duracion_horas DOUBLE PRECISION NOT NULL,
    materia_nombre VARCHAR(100),
    estudiante VARCHAR(100),
    estado VARCHAR(50),
    costo_total NUMERIC(10, 2),
    materia_id BIGINT NOT NULL,
    CONSTRAINT fk_materia FOREIGN KEY (materia_id) REFERENCES materia(id)
);

-- ==========================================================
-- 4. DATOS DE PRUEBA 
-- ==========================================================

-- Insertar materias de ejemplo
INSERT INTO materia (nombre, descripcion, tarifa_hora) 
VALUES ('Matemáticas II', 'Cálculo y álgebra lineal para ingeniería', 50.00);

INSERT INTO materia (nombre, descripcion, tarifa_hora) 
VALUES ('Programación I', 'Fundamentos de Java y POO', 60.00);

-- Insertar sesiones de tutoría de prueba asociadas a las materias
INSERT INTO sesion_tutoria (fecha_hora, duracion_horas, materia_nombre, estudiante, estado, costo_total, materia_id) 
VALUES ('2026-11-05 10:00:00', 2.0, 'Matemáticas II', 'Juan Pérez', 'PENDIENTE', 100.00, 1);

INSERT INTO sesion_tutoria (fecha_hora, duracion_horas, materia_nombre, estudiante, estado, costo_total, materia_id) 
VALUES ('2026-11-06 14:00:00', 1.5, 'Programación I', 'María Gómez', 'PENDIENTE', 90.00, 2);