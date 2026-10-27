 
DROP TABLE IF EXISTS tutor CASCADE;


CREATE TABLE materia (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    tarifa_hora DOUBLE PRECISION NOT NULL
);


ALTER TABLE sesion_tutoria DROP COLUMN IF EXISTS tutor_id;
ALTER TABLE sesion_tutoria ADD COLUMN IF NOT EXISTS materia_id BIGINT;


ALTER TABLE sesion_tutoria 
ADD CONSTRAINT fk_sesion_materia 
FOREIGN KEY (materia_id) REFERENCES materia(id);

ALTER TABLE materia ADD COLUMN descripcion VARCHAR(255);
SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'materia';


INSERT INTO materia (nombre, descripcion, tarifa_hora) VALUES 
('Matemáticas Discretas', 'Lógica y conjuntos para ingeniería', 150.00),
('Bases de Datos I', 'Modelado relacional y SQL en PostgreSQL', 200.00),
('Física Aplicada', 'Mecánica vectorial y cinemática', 160.00);

INSERT INTO sesion_tutoria (fecha_hora, duracion_horas, materia, estado, materia_id) VALUES 
('2026-10-21 10:00:00', 2, 'Matemáticas Discretas', 'PROGRAMADA', 19),
('2026-10-22 11:30:00', 1, 'Bases de Datos I', 'COMPLETADA', 20),
('2026-10-23 15:00:00', 3, 'Física Aplicada', 'PENDIENTE', 21),
('2026-10-24 09:00:00', 2, 'Programación II', 'PROGRAMADA', 18),
('2026-10-25 14:00:00', 2, 'Matemáticas Discretas', 'PROGRAMADA', 19),
('2026-10-26 16:30:00', 1, 'Bases de Datos I', 'COMPLETADA', 20);