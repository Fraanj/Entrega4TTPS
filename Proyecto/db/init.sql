-- ============================================================
-- Script de inicialización / seed de la BD "mibd"
-- Se ejecuta con INSERT IGNORE para ser idempotente:
-- puede correr múltiples veces sin duplicar datos.
-- ============================================================

USE mibd;

-- ------------------------------------------------------------
-- 1. ROLES
-- Requeridos por UsuarioService: "USUARIO" y "ADMINISTRADOR"
-- ------------------------------------------------------------
INSERT IGNORE INTO roles (nombre) VALUES
  ('USUARIO'),
  ('ADMINISTRADOR');

-- ------------------------------------------------------------
-- 2. ESTADOS DE USUARIO
-- Requeridos por UsuarioService: "HABILITADO" y "DESHABILITADO"
-- ------------------------------------------------------------
INSERT IGNORE INTO estados_usuario (nombre) VALUES
  ('HABILITADO'),
  ('DESHABILITADO');

-- ------------------------------------------------------------
-- 3. TAMAÑOS DE MASCOTA
-- Requeridos por MascotaService: "PEQUENIO", "MEDIANO", "GRANDE"
-- ------------------------------------------------------------
INSERT IGNORE INTO tamanios_mascota (nombre) VALUES
  ('PEQUENIO'),
  ('MEDIANO'),
  ('GRANDE');

-- ------------------------------------------------------------
-- 4. MEDALLAS (catálogo base)
-- ------------------------------------------------------------
INSERT IGNORE INTO medallas (nombre, descripcion) VALUES
  ('Primer Reporte',    'Reportaste tu primera mascota perdida'),
  ('5 Avistamientos',  'Reportaste 5 avistamientos de mascotas'),
  ('10 Avistamientos', 'Reportaste 10 avistamientos de mascotas'),
  ('Héroe Animal',     'Ayudaste a recuperar una mascota');

-- ------------------------------------------------------------
-- 5. USUARIO ADMINISTRADOR
-- email:    admin@admin.com
-- password: 1234567!  (BCrypt $2a$10$...)
-- rol:      ADMINISTRADOR
-- estado:   HABILITADO
-- ------------------------------------------------------------
INSERT IGNORE INTO usuarios (nombre, apellido, email, password, telefono, ciudad, puntos, rol_id, estado_usuario_id)
SELECT
  'Admin',
  'Sistema',
  'admin@admin.com',
  '$2a$10$tBofTyFUpxcksm0se0jclu2q/G5MmzT6EJIMn4IQ4jskG.ZONbU0S',
  '0000000000',
  'La Plata',
  0,
  (SELECT id FROM roles         WHERE nombre = 'ADMINISTRADOR' LIMIT 1),
  (SELECT id FROM estados_usuario WHERE nombre = 'HABILITADO'    LIMIT 1);

-- ------------------------------------------------------------
-- 6. USUARIO SISTEMA (publicadorId=1 para el bot de Telegram)
-- Garantiza que el usuario con id=1 exista
-- ------------------------------------------------------------
INSERT IGNORE INTO usuarios (nombre, apellido, email, password, telefono, ciudad, puntos, rol_id, estado_usuario_id)
SELECT
  'Bot',
  'Telegram',
  'bot@sistema.com',
  '$2a$10$tBofTyFUpxcksm0se0jclu2q/G5MmzT6EJIMn4IQ4jskG.ZONbU0S',
  '0000000000',
  'La Plata',
  0,
  (SELECT id FROM roles           WHERE nombre = 'USUARIO'    LIMIT 1),
  (SELECT id FROM estados_usuario WHERE nombre = 'HABILITADO' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'bot@sistema.com');

