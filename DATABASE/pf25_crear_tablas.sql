-- Creación de la tabla Tutor
CREATE TABLE tutor (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    especialidad VARCHAR(100) NOT NULL,
    tarifa_hora NUMERIC(10, 2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Creación de la tabla SesionTutoria
CREATE TABLE sesion_tutoria (
    id SERIAL PRIMARY KEY,
    fecha_hora TIMESTAMP NOT NULL,
    duracion_horas INT NOT NULL,
    materia VARCHAR(100) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    tutor_id INT NOT NULL,
    CONSTRAINT fk_tutor FOREIGN KEY (tutor_id) REFERENCES tutor(id) ON DELETE CASCADE
);