-- ============================================================================
-- PF-25 TUTORÍAS - Script oficial de tablas y datos de prueba
-- Ejecutar conectado a la base "pf25_tutorias_db" (creada con 01_crear_base_de_datos.sql).
-- Los nombres de tablas y columnas coinciden con las entidades Java:
--   Materia        -> materia          (tabla de apoyo)
--   SesionTutoria  -> sesion_tutoria   (tabla principal)
-- ATENCIÓN: borra y recrea las tablas (se pierden los datos existentes).
--
-- REGLAS QUE TAMBIÉN VALIDA LA BASE DE DATOS (coinciden con el servicio Java):
--   * duración mayor a 0                       -> ck_sesion_duracion
--   * estados permitidos                       -> ck_sesion_estado
--   * importes en NUMERIC(10,2)                -> equivalen a BigDecimal en Java
--   * sin choque de horarios entre sesiones ACTIVAS: las sesiones CANCELADAS liberan
--     el horario                               -> uq_sesion_horario_activo (índice único parcial)
-- ============================================================================

DROP TABLE IF EXISTS sesion_tutoria CASCADE;
DROP TABLE IF EXISTS materia CASCADE;

-- ------------------------------- Tabla de apoyo -------------------------------
CREATE TABLE materia (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(255)  NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    tarifa_hora NUMERIC(10,2) NOT NULL,
    CONSTRAINT ck_materia_tarifa CHECK (tarifa_hora > 0)
);

-- ----------------------------- Tabla principal --------------------------------
CREATE TABLE sesion_tutoria (
    id             BIGSERIAL PRIMARY KEY,
    fecha_hora     TIMESTAMP        NOT NULL,
    duracion_horas DOUBLE PRECISION NOT NULL,
    estudiante     VARCHAR(255)     NOT NULL,
    estado         VARCHAR(50)      NOT NULL DEFAULT 'PENDIENTE',
    costo_total    NUMERIC(10,2)    NOT NULL,
    materia_id     BIGINT           NOT NULL,
    CONSTRAINT fk_sesion_materia FOREIGN KEY (materia_id) REFERENCES materia (id) ON DELETE CASCADE,
    CONSTRAINT ck_sesion_duracion CHECK (duracion_horas > 0),
    CONSTRAINT ck_sesion_estado CHECK (estado IN ('PENDIENTE', 'PROGRAMADA', 'CONFIRMADA', 'COMPLETADA', 'CANCELADA'))
);

-- Disponibilidad de horarios: solo UNA sesión activa por fecha y hora.
-- Las CANCELADAS quedan fuera del índice, así que no bloquean ni son bloqueadas.
CREATE UNIQUE INDEX uq_sesion_horario_activo
    ON sesion_tutoria (fecha_hora)
    WHERE estado <> 'CANCELADA';

-- ------------------------ Datos de prueba: 5 materias -------------------------
INSERT INTO materia (nombre, descripcion, tarifa_hora) VALUES
    ('Matemáticas Discretas', 'Lógica, conjuntos y grafos para ingeniería', 150.00),
    ('Bases de Datos I',      'Modelado relacional y SQL en PostgreSQL',     200.00),
    ('Programación I',        'Fundamentos de Java y POO',                   160.00),
    ('Estructuras de Datos',  'Listas, pilas, colas, árboles y grafos',      180.00),
    ('Redes de Computadoras', 'Modelo OSI y TCP/IP',                         175.00);

-- ------------------------ Datos de prueba: 12 sesiones ------------------------
-- costo_total = duración (horas) x tarifa_hora de la materia, redondeado a 2 decimales.
-- Las sesiones 9 (CANCELADA) y 10 (PROGRAMADA) comparten fecha y hora a propósito:
-- demuestran que una sesión cancelada libera el horario.
INSERT INTO sesion_tutoria (fecha_hora, duracion_horas, estudiante, estado, costo_total, materia_id)
SELECT v.fecha_hora::timestamp,
       v.duracion,
       v.estudiante,
       v.estado,
       ROUND(v.duracion::numeric * m.tarifa_hora, 2),
       m.id
FROM (VALUES
    ('2026-11-05 10:00:00', 2.0, 'Juan Pérez',     'PENDIENTE',  'Matemáticas Discretas'),
    ('2026-11-05 14:00:00', 1.5, 'María Gómez',    'PENDIENTE',  'Bases de Datos I'),
    ('2026-11-06 09:00:00', 1.0, 'Luis Alvarado',  'COMPLETADA', 'Programación I'),
    ('2026-11-06 11:00:00', 2.5, 'Ana Morales',    'CONFIRMADA', 'Estructuras de Datos'),
    ('2026-11-07 15:00:00', 1.0, 'Carlos Méndez',  'PENDIENTE',  'Redes de Computadoras'),
    ('2026-11-09 08:00:00', 2.0, 'Sofía Ramírez',  'COMPLETADA', 'Bases de Datos I'),
    ('2026-11-09 10:00:00', 1.5, 'Diego Castillo', 'PROGRAMADA', 'Programación I'),
    ('2026-11-10 16:00:00', 3.0, 'Valeria Ortiz',  'PENDIENTE',  'Matemáticas Discretas'),
    ('2026-11-12 10:00:00', 1.0, 'Pedro Solís',    'CANCELADA',  'Estructuras de Datos'),
    ('2026-11-12 10:00:00', 2.0, 'Karla Juárez',   'PROGRAMADA', 'Redes de Computadoras'),
    ('2026-11-13 13:00:00', 1.5, 'Andrés López',   'COMPLETADA', 'Matemáticas Discretas'),
    ('2026-11-14 09:30:00', 2.0, 'Lucía Herrera',  'PENDIENTE',  'Bases de Datos I')
) AS v(fecha_hora, duracion, estudiante, estado, materia)
JOIN materia m ON m.nombre = v.materia;

-- Verificación rápida (opcional):
-- SELECT COUNT(*) FROM materia;          -- 5
-- SELECT COUNT(*) FROM sesion_tutoria;   -- 12
