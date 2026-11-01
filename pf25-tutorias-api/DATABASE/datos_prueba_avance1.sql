-- 1. Insertar registros de prueba en la tabla tutor
INSERT INTO tutor (nombre, correo, especialidad, tarifa_hora) 
VALUES 
('Carlos Mendoza', 'carlos.mendoza@tutorias.com', 'Matemáticas Discretas', 150.00),
('Ana Lucía Gómez', 'ana.gomez@tutorias.com', 'Programación Orientada a Objetos', 175.50),
('Jorge Ramírez', 'jorge.ramirez@tutorias.com', 'Bases de Datos I', 160.00);

-- 2. Insertar registros de prueba en la tabla sesion_tutoria
INSERT INTO sesion_tutoria (fecha_hora, duracion_horas, materia, estado, tutor_id) 
VALUES 
('2026-10-15 10:00:00', 2, 'Álgebra y Lógica', 'PROGRAMADA', 1),
('2026-10-16 14:30:00', 1, 'Estructuras de Datos', 'CONFIRMADA', 2),
('2026-10-17 09:00:00', 3, 'Modelado Entidad-Relación', 'PENDIENTE', 3);