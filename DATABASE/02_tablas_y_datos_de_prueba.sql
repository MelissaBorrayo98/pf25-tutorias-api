-- ==========================================================
-- PF-25 TUTORÍAS - Script oficial de tablas y datos de prueba
-- Ejecutar conectado a la base "pf25_tutorias_db".
-- Los nombres de tablas y columnas coinciden con las entidades Java:
--   Materia        -> materia
--   SesionTutoria  -> sesion_tutoria
-- ATENCIÓN: borra y recrea las tablas (se pierden los datos existentes).
-- ==========================================================

DROP TABLE IF EXISTS sesion_tutoria CASCADE;
DROP TABLE IF EXISTS materia CASCADE;

CREATE TABLE materia (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    tarifa_hora DOUBLE PRECISION
);

CREATE TABLE sesion_tutoria (
    id             BIGSERIAL PRIMARY KEY,
    fecha_hora     TIMESTAMP        NOT NULL,
    duracion_horas DOUBLE PRECISION NOT NULL,
    estudiante     VARCHAR(255)     NOT NULL,
    estado         VARCHAR(50)      NOT NULL DEFAULT 'PENDIENTE',
    costo_total    DOUBLE PRECISION NOT NULL,
    materia_id     BIGINT           NOT NULL,
    CONSTRAINT fk_sesion_materia FOREIGN KEY (materia_id) REFERENCES materia (id) ON DELETE CASCADE,
    CONSTRAINT uq_sesion_fecha_hora UNIQUE (fecha_hora),          -- sin choque de horarios
    CONSTRAINT ck_sesion_duracion CHECK (duracion_horas > 0)      -- duración mayor a 0
);

-- ---------------------------- Datos de prueba ----------------------------
INSERT INTO materia (nombre, descripcion, tarifa_hora) VALUES
    ('Matemáticas Discretas', 'Lógica y conjuntos para ingeniería', 150.00),
    ('Bases de Datos I', 'Modelado relacional y SQL en PostgreSQL', 200.00),
    ('Programación I', 'Fundamentos de Java y POO', 160.00);

-- costo_total = duracion_horas x tarifa_hora de la materia
INSERT INTO sesion_tutoria (fecha_hora, duracion_horas, estudiante, estado, costo_total, materia_id)
SELECT '2026-11-05 10:00:00', 2.0, 'Juan Pérez', 'PENDIENTE', 2.0 * tarifa_hora, id FROM materia WHERE nombre = 'Matemáticas Discretas';

INSERT INTO sesion_tutoria (fecha_hora, duracion_horas, estudiante, estado, costo_total, materia_id)
SELECT '2026-11-06 14:00:00', 1.5, 'María Gómez', 'PENDIENTE', 1.5 * tarifa_hora, id FROM materia WHERE nombre = 'Bases de Datos I';

INSERT INTO sesion_tutoria (fecha_hora, duracion_horas, estudiante, estado, costo_total, materia_id)
SELECT '2026-11-07 09:00:00', 1.0, 'Luis Alvarado', 'COMPLETADA', 1.0 * tarifa_hora, id FROM materia WHERE nombre = 'Programación I';
