-- DROP DATABASE IF EXISTS asistencia_rfid;  -- descomentar al reimportar
CREATE DATABASE IF NOT EXISTS asistencia_rfid
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE asistencia_rfid;

CREATE TABLE IF NOT EXISTS carrera (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS periodo (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(60) NOT NULL UNIQUE,
  fecha_inicio DATE NOT NULL,
  fecha_fin DATE NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS usuario (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(30) NOT NULL UNIQUE,
  password_hash CHAR(64) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  perfil ENUM('ADMIN','COORDINADOR','DOCENTE','REGISTRO') NOT NULL,
  carrera_id INT NULL COMMENT 'solo para COORDINADOR',
  activo TINYINT(1) NOT NULL DEFAULT 1,
  FOREIGN KEY (carrera_id) REFERENCES carrera(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS estudiante (
  id INT AUTO_INCREMENT PRIMARY KEY,
  codigo_rfid VARCHAR(20) NOT NULL UNIQUE,
  nombre VARCHAR(60) NOT NULL,
  apellido VARCHAR(60) NOT NULL,
  correo VARCHAR(80),
  carrera_id INT NOT NULL,
  FOREIGN KEY (carrera_id) REFERENCES carrera(id)
);

CREATE TABLE IF NOT EXISTS asignatura (
  id INT AUTO_INCREMENT PRIMARY KEY,
  carrera_id INT NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  nivel TINYINT NOT NULL,
  FOREIGN KEY (carrera_id) REFERENCES carrera(id) ON DELETE CASCADE,
  UNIQUE KEY uq_asignatura (carrera_id, nombre)
);

CREATE TABLE IF NOT EXISTS grupo (
  id INT AUTO_INCREMENT PRIMARY KEY,
  periodo_id INT NOT NULL,
  asignatura_id INT NOT NULL,
  paralelo VARCHAR(5) NOT NULL,
  docente_id INT NULL,
  FOREIGN KEY (periodo_id) REFERENCES periodo(id) ON DELETE CASCADE,
  FOREIGN KEY (asignatura_id) REFERENCES asignatura(id) ON DELETE CASCADE,
  FOREIGN KEY (docente_id) REFERENCES usuario(id) ON DELETE SET NULL,
  UNIQUE KEY uq_grupo (periodo_id, asignatura_id, paralelo)
);

CREATE TABLE IF NOT EXISTS horario (
  id INT AUTO_INCREMENT PRIMARY KEY,
  grupo_id INT NOT NULL,
  dia_semana TINYINT NOT NULL COMMENT '1=lunes ... 7=domingo',
  hora_inicio TIME NOT NULL,
  hora_fin TIME NOT NULL,
  aula VARCHAR(20),
  FOREIGN KEY (grupo_id) REFERENCES grupo(id) ON DELETE CASCADE,
  UNIQUE KEY uq_horario (grupo_id, dia_semana, hora_inicio)
);

CREATE TABLE IF NOT EXISTS matricula (
  estudiante_id INT NOT NULL,
  grupo_id INT NOT NULL,
  PRIMARY KEY (estudiante_id, grupo_id),
  FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE,
  FOREIGN KEY (grupo_id) REFERENCES grupo(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS asistencia (
  id INT AUTO_INCREMENT PRIMARY KEY,
  estudiante_id INT NOT NULL,
  horario_id INT NOT NULL,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE,
  FOREIGN KEY (horario_id) REFERENCES horario(id) ON DELETE CASCADE,
  UNIQUE KEY uq_asistencia (estudiante_id, horario_id, fecha)
);

CREATE TABLE IF NOT EXISTS feriado (
  fecha DATE PRIMARY KEY,
  descripcion VARCHAR(80)
);

-- ===== DATOS DE PRUEBA (ajustar fechas del periodo) =====
INSERT IGNORE INTO carrera (nombre) VALUES ('Software'), ('Telecomunicaciones');

INSERT IGNORE INTO periodo (nombre, fecha_inicio, fecha_fin, activo)
VALUES ('Periodo de prueba', '2026-09-28', '2027-02-26', 1);

INSERT IGNORE INTO usuario (username, password_hash, nombre, perfil, carrera_id) VALUES
('admin',    SHA2('admin123',256),    'Administrador', 'ADMIN', NULL),
('coordsw',  SHA2('coord123',256),    'Coordinador Software', 'COORDINADOR',
   (SELECT id FROM carrera WHERE nombre='Software')),
('docente1', SHA2('docente123',256),  'Docente Prueba', 'DOCENTE', NULL),
('registro', SHA2('registro123',256), 'Puesto de lectura', 'REGISTRO', NULL);

INSERT IGNORE INTO asignatura (carrera_id, nombre, nivel)
SELECT c.id, x.nombre, x.nivel FROM carrera c
JOIN (SELECT 'Cálculo' nombre, 1 nivel
      UNION SELECT 'Programación Orientada a Objetos', 2
      UNION SELECT 'Base de Datos', 2) x
WHERE c.nombre='Software';

INSERT IGNORE INTO grupo (periodo_id, asignatura_id, paralelo, docente_id)
SELECT p.id, a.id, 'A', (SELECT id FROM usuario WHERE username='docente1')
FROM periodo p, asignatura a
WHERE p.activo=1 AND a.carrera_id=(SELECT id FROM carrera WHERE nombre='Software');

-- Lunes: Cálculo 08-10 (nivel 1, mañana); POO 16-18 y BD 18-20 (nivel 2, tarde)
INSERT IGNORE INTO horario (grupo_id, dia_semana, hora_inicio, hora_fin, aula)
SELECT g.id, 1, '08:00:00', '10:00:00', 'A-101' FROM grupo g
JOIN asignatura a ON a.id=g.asignatura_id WHERE a.nombre='Cálculo';
INSERT IGNORE INTO horario (grupo_id, dia_semana, hora_inicio, hora_fin, aula)
SELECT g.id, 1, '16:00:00', '18:00:00', 'A-102' FROM grupo g
JOIN asignatura a ON a.id=g.asignatura_id WHERE a.nombre='Programación Orientada a Objetos';
INSERT IGNORE INTO horario (grupo_id, dia_semana, hora_inicio, hora_fin, aula)
SELECT g.id, 1, '18:00:00', '20:00:00', 'A-102' FROM grupo g
JOIN asignatura a ON a.id=g.asignatura_id WHERE a.nombre='Base de Datos';

INSERT IGNORE INTO estudiante (codigo_rfid, nombre, apellido, correo, carrera_id)
SELECT x.cod, x.nom, x.ape, x.mail, c.id FROM carrera c
JOIN (SELECT '0001234567' cod, 'María' nom, 'Torres' ape, 'maria@correo.com' mail
      UNION SELECT '0007654321', 'Luis', 'Gómez', 'luis@correo.com') x
WHERE c.nombre='Software';

-- María (nivel 2) repite Cálculo de nivel 1: está en los 3 grupos. Luis solo en los 2 de la tarde.
INSERT IGNORE INTO matricula (estudiante_id, grupo_id)
SELECT e.id, g.id FROM estudiante e, grupo g WHERE e.nombre='María';
INSERT IGNORE INTO matricula (estudiante_id, grupo_id)
SELECT e.id, g.id FROM estudiante e, grupo g
JOIN asignatura a ON a.id=g.asignatura_id
WHERE e.nombre='Luis' AND a.nombre IN ('Programación Orientada a Objetos','Base de Datos');
