-- =====================================================================
-- Datos de prueba. Se ejecuta en cada arranque (spring.sql.init.mode=always);
-- todos los inserts son idempotentes gracias a ON DUPLICATE KEY UPDATE.
-- =====================================================================

-- ---------- Usuarios ----------
-- admin / admin123
INSERT INTO user (id, username, password, enabled, created_at, updated_at)
VALUES (1, 'admin', '$2a$10$YFUsBtgsmVCYDbs0DJfO3efGeZPgE3xcD5ektiqczlC5phrR6m8/e', 1, NOW(), NOW())
    ON DUPLICATE KEY UPDATE username=username;

INSERT INTO user_role (id, role, created_at, updated_at, user_id)
VALUES (1, 'ROLE_ADMIN', NOW(), NOW(), 1)
    ON DUPLICATE KEY UPDATE role=role;

-- responsable1 / admin123 (responsable de unidad de venta)
INSERT INTO user (id, username, password, enabled, created_at, updated_at)
VALUES (2, 'responsable1', '$2a$10$YFUsBtgsmVCYDbs0DJfO3efGeZPgE3xcD5ektiqczlC5phrR6m8/e', 1, NOW(), NOW())
    ON DUPLICATE KEY UPDATE username=username;

INSERT INTO user_role (id, role, created_at, updated_at, user_id)
VALUES (2, 'ROLE_RESPONSABLE', NOW(), NOW(), 2)
    ON DUPLICATE KEY UPDATE role=role;

-- ---------- Platos ----------
INSERT INTO plato (id_plato, nombre, precio, costo) VALUES
                                                        (1, 'Hamburguesa Completa', 8500.00, 3200.00),
                                                        (2, 'Papas Rústicas', 4500.00, 1500.00),
                                                        (3, 'Cerveza Artesanal', 3500.00, 1200.00)
    ON DUPLICATE KEY UPDATE nombre=nombre;

-- ---------- Festivales ----------
-- Festival 1 ya terminó (tiene los pedidos de prueba); el 2 está en curso para probar "Hoy trabajás en...".
-- ON DUPLICATE KEY actualiza nombre y fechas, así las bases que ya tenían otros festivales quedan iguales a este script.
INSERT INTO festival (id_festival, nombre, temporada, fecha_inicio, fecha_fin) VALUES
                                                                                   (1, 'Festival de Primavera', 'Primavera 2026', '2026-10-01', '2026-10-12'),
                                                                                   (2, 'Primavera Sound 2026',  'Primavera 2026', '2026-10-20', '2026-11-15')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), temporada=VALUES(temporada),
    fecha_inicio=VALUES(fecha_inicio), fecha_fin=VALUES(fecha_fin);

-- ---------- Personal (Persona es abstracta: cada fila necesita su subtipo) ----------
-- Responsable de unidad (vinculado al usuario responsable1)
INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, user_id)
VALUES (1, 'Martín', 'García', 30111222, '1985-04-12', '2022-03-01', 'responsable1@epicentro.com', 2)
    ON DUPLICATE KEY UPDATE nombre=nombre;

-- Limpieza preventiva para asegurar que el responsable no figure como cajero
DELETE FROM cajero WHERE id_persona = 1;

INSERT INTO responsable (id_persona)
VALUES (1)
    ON DUPLICATE KEY UPDATE id_persona=id_persona;

-- Cajero del staff
INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, user_id)
VALUES (3, 'Carlos', 'López', 28333444, '1982-11-03', '2021-09-10', 'carlos.lopez@epicentro.com', NULL)
    ON DUPLICATE KEY UPDATE nombre=nombre;

INSERT INTO cajero (id_persona, turno_trabajo, recaudacion_total)
VALUES (3, 'MAÑANA', 0)
    ON DUPLICATE KEY UPDATE turno_trabajo=turno_trabajo;

-- Cocinero del staff
INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, user_id)
VALUES (2, 'Lucía', 'Pérez', 35222333, '1990-08-25', '2023-06-15', 'lucia.perez@epicentro.com', NULL)
    ON DUPLICATE KEY UPDATE nombre=nombre;

INSERT INTO cocinero (id_persona, especialidad, categoria)
VALUES (2, 'Parrilla', 'COCINERO')
    ON DUPLICATE KEY UPDATE especialidad=especialidad;

-- Empleado de prueba: empleado1 / admin123 (lavaplatos)
INSERT INTO user (id, username, password, enabled, created_at, updated_at)
VALUES (100, 'empleado1', '$2a$10$YFUsBtgsmVCYDbs0DJfO3efGeZPgE3xcD5ektiqczlC5phrR6m8/e', 1, NOW(), NOW())
    ON DUPLICATE KEY UPDATE username=username;

INSERT INTO user_role (id, role, created_at, updated_at, user_id)
VALUES (100, 'ROLE_EMPLEADO', NOW(), NOW(), 100)
    ON DUPLICATE KEY UPDATE role=role;

INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, user_id)
VALUES (100, 'Diego', 'Fernández', 38444555, '1995-02-10', '2024-01-20', 'diego.fernandez@epicentro.com', 100)
    ON DUPLICATE KEY UPDATE nombre=nombre;

INSERT INTO cocinero (id_persona, especialidad, categoria)
VALUES (100, 'Lavado', 'LAVAPLATOS')
    ON DUPLICATE KEY UPDATE especialidad=especialidad;

-- ---------- Unidades de venta ----------
-- Food Truck con conexión eléctrica: canon = 12 * 500 + 2000 = 8000
INSERT INTO unidad_de_venta (id_unidad_de_venta, nombre_comercial, codigo, superficie, activo, festival_id, responsable_id)
VALUES (1, 'Burger Truck', 'FT-BRG0001', 12, 1, 1, 1)
    ON DUPLICATE KEY UPDATE nombre_comercial=nombre_comercial;
INSERT INTO food_truck (id_unidad_de_venta, patente, requiere_conexion_electrica)
VALUES (1, 'AB123CD', 1)
    ON DUPLICATE KEY UPDATE patente=patente;

-- Food Truck sin conexión eléctrica: canon = 8 * 500 = 4000
INSERT INTO unidad_de_venta (id_unidad_de_venta, nombre_comercial, codigo, superficie, activo, festival_id, responsable_id)
VALUES (2, 'Taco Móvil', 'FT-TAC0002', 8, 1, 1, 1)
    ON DUPLICATE KEY UPDATE nombre_comercial=nombre_comercial;
INSERT INTO food_truck (id_unidad_de_venta, patente, requiere_conexion_electrica)
VALUES (2, 'AC456EF', 0)
    ON DUPLICATE KEY UPDATE patente=patente;

-- Puesto Desarmable: canon = 20 * 500 - 90 * 10 = 9100
INSERT INTO unidad_de_venta (id_unidad_de_venta, nombre_comercial, codigo, superficie, activo, festival_id, responsable_id)
VALUES (3, 'Choripanes del Sur', 'PD-CHO0003', 20, 1, 2, 1)
    ON DUPLICATE KEY UPDATE nombre_comercial=nombre_comercial;
INSERT INTO puesto_desarmable (id_unidad_de_venta, cantidad_carpas, tiempo_montaje_minutos)
VALUES (3, 2, 90)
    ON DUPLICATE KEY UPDATE cantidad_carpas=cantidad_carpas;

-- Platos ofrecidos y staff de cada unidad
INSERT IGNORE INTO unidad_plato (unidad_id, plato_id) VALUES (1, 1), (1, 2), (2, 3), (3, 1), (3, 3);
INSERT IGNORE INTO unidad_staff (unidad_id, persona_id) VALUES (1, 1), (1, 2), (1, 3), (3, 1), (1, 100), (3, 100);

-- ---------- Pedidos con sus ítems ----------
-- Sub_total = precio del plato * cantidad (Hamburguesa 8500, Papas 4500, Cerveza 3500).
-- Todos del Festival 1 (01/10 al 12/10/2026), unidades 1 y 2. Sirven para recaudación, ranking y plato estrella.
INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id) VALUES
                                                                                    (1, '2026-10-02', 1, 1),
                                                                                    (2, '2026-10-02', 1, 2),
                                                                                    (3, '2026-10-05', 1, 1),
                                                                                    (4, '2026-10-08', 1, 1),
                                                                                    (5, '2026-10-10', 1, 2),
                                                                                    (6, '2026-10-11', 1, 1)
    ON DUPLICATE KEY UPDATE fecha_transaccion=VALUES(fecha_transaccion), festival_id=VALUES(festival_id), unidad_venta_id=VALUES(unidad_venta_id);

INSERT INTO item_plato (id_item_plato, plato_id, cantidad, sub_total, pedido_id) VALUES
                                                                                     (1, 1, 2, 17000.00, 1),   -- 2 hamburguesas
                                                                                     (2, 2, 1,  4500.00, 1),   -- 1 papas
                                                                                     (3, 3, 3, 10500.00, 2),   -- 3 cervezas
                                                                                     (4, 1, 1,  8500.00, 3),
                                                                                     (5, 2, 2,  9000.00, 3),
                                                                                     (6, 1, 4, 34000.00, 4),
                                                                                     (7, 3, 2,  7000.00, 5),
                                                                                     (8, 1, 3, 25500.00, 6),
                                                                                     (9, 2, 3, 13500.00, 6)
    ON DUPLICATE KEY UPDATE plato_id=VALUES(plato_id), cantidad=VALUES(cantidad), sub_total=VALUES(sub_total), pedido_id=VALUES(pedido_id);

-- ---------- Más datos para el festival en curso (Primavera Sound 2026) ----------
-- Segundo responsable: responsable2 / admin123
-- Usuario responsable2
INSERT INTO user (id, username, password, enabled, created_at, updated_at)
VALUES (101, 'responsable2', '$2a$10$YFUsBtgsmVCYDbs0DJfO3efGeZPgE3xcD5ektiqczlC5phrR6m8/e', 1, NOW(), NOW())
    ON DUPLICATE KEY UPDATE username=username;

INSERT INTO user_role (id, role, created_at, updated_at, user_id)
VALUES (101, 'ROLE_RESPONSABLE', NOW(), NOW(), 101)
    ON DUPLICATE KEY UPDATE role=role;

INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, user_id)
VALUES (101, 'Ana', 'Martínez', 32555666, '1988-07-19', '2020-05-04', 'responsable2@epicentro.com', 101)
    ON DUPLICATE KEY UPDATE nombre=nombre;

INSERT INTO responsable (id_persona)
VALUES (101)
    ON DUPLICATE KEY UPDATE id_persona=id_persona;

-- Platos de las unidades nuevas
INSERT INTO plato (id_plato, nombre, precio, costo) VALUES
    (101, 'Pizza Muzzarella', 7000.00, 2500.00),
    (102, 'Gaseosa', 2000.00, 700.00),
    (103, 'Empanada de Carne', 1500.00, 500.00)
    ON DUPLICATE KEY UPDATE nombre=nombre;

-- Food Truck con conexión eléctrica: canon = 15 * 500 + 2000 = 9500
INSERT INTO unidad_de_venta (id_unidad_de_venta, nombre_comercial, codigo, superficie, activo, festival_id, responsable_id)
VALUES (101, 'Pizza Nostra', 'FT-PIZ0101', 15, 1, 2, 101)
    ON DUPLICATE KEY UPDATE nombre_comercial=nombre_comercial;
INSERT INTO food_truck (id_unidad_de_venta, patente, requiere_conexion_electrica)
VALUES (101, 'AD789GH', 1)
    ON DUPLICATE KEY UPDATE patente=patente;

-- Puesto Desarmable: canon = 10 * 500 - 45 * 10 = 4550
INSERT INTO unidad_de_venta (id_unidad_de_venta, nombre_comercial, codigo, superficie, activo, festival_id, responsable_id)
VALUES (102, 'Empanadas del Norte', 'PD-EMP0102', 10, 1, 2, 101)
    ON DUPLICATE KEY UPDATE nombre_comercial=nombre_comercial;
INSERT INTO puesto_desarmable (id_unidad_de_venta, cantidad_carpas, tiempo_montaje_minutos)
VALUES (102, 1, 45)
    ON DUPLICATE KEY UPDATE cantidad_carpas=cantidad_carpas;

INSERT IGNORE INTO unidad_plato (unidad_id, plato_id) VALUES (101, 101), (101, 102), (102, 103), (102, 102);
INSERT IGNORE INTO unidad_staff (unidad_id, persona_id) VALUES (101, 101), (102, 101), (101, 3), (102, 2);

-- Pedidos del festival en curso (ya ocurridos)
INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id) VALUES
    (101, '2026-10-21', 2, 3),
    (102, '2026-10-22', 2, 101),
    (103, '2026-10-23', 2, 102)
    ON DUPLICATE KEY UPDATE fecha_transaccion=VALUES(fecha_transaccion), festival_id=VALUES(festival_id), unidad_venta_id=VALUES(unidad_venta_id);
INSERT INTO item_plato (id_item_plato, plato_id, cantidad, sub_total, pedido_id) VALUES
    (101, 1,   2, 17000.00, 101),  -- 2 hamburguesas (Choripanes del Sur)
    (102, 3,   4, 14000.00, 101),  -- 4 cervezas
    (103, 101, 3, 21000.00, 102),  -- 3 pizzas (Pizza Nostra)
    (104, 102, 3,  6000.00, 102),  -- 3 gaseosas
    (105, 103, 12, 18000.00, 103)  -- 12 empanadas (Empanadas del Norte)
    ON DUPLICATE KEY UPDATE plato_id=VALUES(plato_id), cantidad=VALUES(cantidad), sub_total=VALUES(sub_total), pedido_id=VALUES(pedido_id);

-- ---------- Más datos de prueba: platos, staff y pedidos (ids desde 200) ----------
-- Platos nuevos para las unidades existentes
INSERT INTO plato (id_plato, nombre, precio, costo) VALUES
    (200, 'Choripán',             4000.00, 1200.00),
    (201, 'Bondiola al pan',      6500.00, 2500.00),
    (202, 'Papas con cheddar',    5500.00, 1800.00),
    (203, 'Pizza Napolitana',     8500.00, 3200.00),
    (204, 'Pizza Fugazzeta',      8000.00, 2800.00),
    (205, 'Empanada de Pollo',    1500.00,  450.00),
    (206, 'Empanada de Humita',   1400.00,  400.00),
    (207, 'Agua Mineral',         1500.00,  500.00),
    (208, 'Limonada',             3000.00,  900.00),
    (209, 'Tacos al Pastor (x3)', 7500.00, 2800.00)
    ON DUPLICATE KEY UPDATE nombre=nombre;

INSERT IGNORE INTO unidad_plato (unidad_id, plato_id) VALUES
    (1, 202), (1, 208),
    (2, 209), (2, 207),
    (3, 200), (3, 201), (3, 202), (3, 207),
    (101, 203), (101, 204), (101, 207),
    (102, 205), (102, 206), (102, 208);

-- Staff nuevo (sin usuario): cajeros y cocineros para que cada unidad tenga personal a sueldo
INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, user_id) VALUES
    (200, 'Sofía',     'Romero',  40111222, '1997-03-14', '2025-03-01', 'sofia.romero@epicentro.com',     NULL),
    (201, 'Matías',    'Giménez', 39222333, '1996-09-02', '2024-08-12', 'matias.gimenez@epicentro.com',   NULL),
    (202, 'Valentina', 'Sosa',    41333444, '1998-12-20', '2023-02-06', 'valentina.sosa@epicentro.com',   NULL),
    (203, 'Tomás',     'Acosta',  36444555, '1993-05-30', '2019-11-18', 'tomas.acosta@epicentro.com',     NULL),
    (204, 'Camila',    'Herrera', 42555666, '1999-07-08', '2024-06-03', 'camila.herrera@epicentro.com',   NULL)
    ON DUPLICATE KEY UPDATE nombre=nombre;

INSERT INTO cajero (id_persona, turno_trabajo, recaudacion_total) VALUES
    (200, 'NOCHE',  0),
    (202, 'MAÑANA', 0),
    (204, 'NOCHE',  0)
    ON DUPLICATE KEY UPDATE turno_trabajo=turno_trabajo;

INSERT INTO cocinero (id_persona, especialidad, categoria) VALUES
    (201, 'Horno',    'COCINERO_AYUDANTE'),
    (203, 'Plancha',  'COCINERO')
    ON DUPLICATE KEY UPDATE especialidad=especialidad;

INSERT IGNORE INTO unidad_staff (unidad_id, persona_id) VALUES
    (3, 200), (101, 201), (102, 202), (1, 203), (2, 204);

-- Pedidos: festival 1 (unidades 1 y 2) y festival 2 (unidades 3, 101 y 102), con fechas ya ocurridas.
-- Sub_total = precio del plato * cantidad.
INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id) VALUES
    (200, '2026-10-01', 1, 1),
    (201, '2026-10-03', 1, 1),
    (202, '2026-10-04', 1, 2),
    (203, '2026-10-06', 1, 2),
    (204, '2026-10-07', 1, 1),
    (205, '2026-10-09', 1, 2),
    (206, '2026-10-10', 1, 1),
    (207, '2026-10-12', 1, 1),
    (208, '2026-10-12', 1, 2),
    (224, '2026-10-21', 2, 102),
    (225, '2026-10-23', 2, 102),
    (226, '2026-10-25', 2, 102),
    (227, '2026-10-27', 2, 102)
    ON DUPLICATE KEY UPDATE fecha_transaccion=VALUES(fecha_transaccion), festival_id=VALUES(festival_id), unidad_venta_id=VALUES(unidad_venta_id);
INSERT INTO item_plato (id_item_plato, plato_id, cantidad, sub_total, pedido_id) VALUES
    (200, 1, 5, 42500.00, 200),
    (201, 2, 4, 18000.00, 200),
    (202, 208, 3, 9000.00, 200),
    (203, 1, 6, 51000.00, 201),
    (204, 202, 3, 16500.00, 201),
    (205, 209, 4, 30000.00, 202),
    (206, 3, 6, 21000.00, 202),
    (207, 209, 6, 45000.00, 203),
    (208, 207, 4, 6000.00, 203),
    (209, 1, 8, 68000.00, 204),
    (210, 2, 5, 22500.00, 204),
    (211, 202, 2, 11000.00, 204),
    (212, 209, 5, 37500.00, 205),
    (213, 3, 8, 28000.00, 205),
    (214, 207, 3, 4500.00, 205),
    (215, 1, 7, 59500.00, 206),
    (216, 208, 5, 15000.00, 206),
    (217, 1, 10, 85000.00, 207),
    (218, 2, 6, 27000.00, 207),
    (219, 202, 4, 22000.00, 207),
    (220, 209, 8, 60000.00, 208),
    (221, 3, 10, 35000.00, 208),
    (270, 103, 24, 36000.00, 224),
    (271, 205, 12, 18000.00, 224),
    (272, 208, 6, 18000.00, 224),
    (273, 103, 30, 45000.00, 225),
    (274, 206, 10, 14000.00, 225),
    (275, 102, 12, 24000.00, 225),
    (276, 103, 48, 72000.00, 226),
    (277, 205, 24, 36000.00, 226),
    (278, 206, 12, 16800.00, 226),
    (279, 208, 10, 30000.00, 226),
    (280, 103, 36, 54000.00, 227),
    (281, 205, 18, 27000.00, 227),
    (282, 102, 15, 30000.00, 227)
    ON DUPLICATE KEY UPDATE plato_id=VALUES(plato_id), cantidad=VALUES(cantidad), sub_total=VALUES(sub_total), pedido_id=VALUES(pedido_id);

-- ---------- Unidades adicionales con su responsable y staff ----------
-- responsable3 / admin123
INSERT INTO user (id, username, password, enabled, created_at, updated_at)
VALUES (200, 'responsable3', '$2a$10$YFUsBtgsmVCYDbs0DJfO3efGeZPgE3xcD5ektiqczlC5phrR6m8/e', 1, NOW(), NOW())
    ON DUPLICATE KEY UPDATE username=username;
INSERT INTO user_role (id, role, created_at, updated_at, user_id)
VALUES (200, 'ROLE_RESPONSABLE', NOW(), NOW(), (SELECT id FROM user WHERE username = 'responsable3'))
    ON DUPLICATE KEY UPDATE role=role;
INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, user_id)
VALUES (205, 'Julián', 'Pereyra', 33666777, '1989-01-25', '2021-04-19', 'responsable3@epicentro.com', (SELECT id FROM user WHERE username = 'responsable3'))
    ON DUPLICATE KEY UPDATE nombre=nombre;
INSERT INTO responsable (id_persona) VALUES (205)
    ON DUPLICATE KEY UPDATE id_persona=id_persona;

-- Food Truck con conexión eléctrica (festival 1): canon = 14 * 500 + 2000 = 9000
INSERT INTO unidad_de_venta (id_unidad_de_venta, nombre_comercial, codigo, superficie, activo, festival_id, responsable_id)
VALUES (200, 'Pizza Truck', 'FT-PZT0200', 14, 1, 1, 205)
    ON DUPLICATE KEY UPDATE nombre_comercial=nombre_comercial;
INSERT INTO food_truck (id_unidad_de_venta, patente, requiere_conexion_electrica)
VALUES (200, 'AE321JK', 1)
    ON DUPLICATE KEY UPDATE patente=patente;

-- Puesto Desarmable (festival 2): canon = 6 * 500 - 30 * 10 = 2700
INSERT INTO unidad_de_venta (id_unidad_de_venta, nombre_comercial, codigo, superficie, activo, festival_id, responsable_id)
VALUES (201, 'Cositas Dulces', 'PD-DUL0201', 6, 1, 2, 205)
    ON DUPLICATE KEY UPDATE nombre_comercial=nombre_comercial;
INSERT INTO puesto_desarmable (id_unidad_de_venta, cantidad_carpas, tiempo_montaje_minutos)
VALUES (201, 1, 30)
    ON DUPLICATE KEY UPDATE cantidad_carpas=cantidad_carpas;

INSERT INTO plato (id_plato, nombre, precio, costo) VALUES
    (210, 'Pizza Pepperoni',             8500.00, 3000.00),
    (211, 'Fainá',                       2000.00,  600.00),
    (212, 'Churros con dulce de leche',  2500.00,  700.00),
    (213, 'Brownie',                     3000.00,  900.00),
    (215, 'Pastafrola de batata',        3000.00,  900.00),
    (216, 'Pastafrola de membrillo',     3000.00,  900.00),
    (217, 'Cookies',                     1800.00,  500.00),
    (214, 'Café',                        2000.00,  500.00)
    ON DUPLICATE KEY UPDATE nombre=nombre;
INSERT IGNORE INTO unidad_plato (unidad_id, plato_id) VALUES
    (200, 210), (200, 211), (200, 207),
    (201, 212), (201, 213), (201, 214), (201, 215), (201, 216), (201, 217), (201, 208);

-- Dos empleados por unidad (un cajero y un cocinero)
INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, user_id) VALUES
    (206, 'Lautaro',  'Molina',  43777888, '2000-10-05', '2024-02-19', 'lautaro.molina@epicentro.com',  NULL),
    (207, 'Florencia','Castro',  37888999, '1994-06-17', '2020-09-07', 'florencia.castro@epicentro.com', NULL),
    (208, 'Bruno',    'Ledesma', 44999000, '2001-03-23', '2025-01-13', 'bruno.ledesma@epicentro.com',    NULL),
    (209, 'Martina',  'Ríos',    41000111, '1998-11-11', '2022-07-04', 'martina.rios@epicentro.com',     NULL)
    ON DUPLICATE KEY UPDATE nombre=nombre;
INSERT INTO cajero (id_persona, turno_trabajo, recaudacion_total) VALUES
    (206, 'MAÑANA', 0),
    (208, 'NOCHE',  0)
    ON DUPLICATE KEY UPDATE turno_trabajo=turno_trabajo;
INSERT INTO cocinero (id_persona, especialidad, categoria) VALUES
    (207, 'Horno',      'COCINERO'),
    (209, 'Pastelería', 'COCINERO_AYUDANTE')
    ON DUPLICATE KEY UPDATE especialidad=especialidad;
INSERT IGNORE INTO unidad_staff (unidad_id, persona_id) VALUES
    (200, 205), (200, 206), (200, 207),
    (201, 205), (201, 208), (201, 209);

-- Más ventas de Empanadas del Norte
INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id) VALUES
    (237, '2026-10-22', 2, 102),
    (238, '2026-10-26', 2, 102),
    (239, '2026-10-28', 2, 102)
    ON DUPLICATE KEY UPDATE fecha_transaccion=VALUES(fecha_transaccion), festival_id=VALUES(festival_id), unidad_venta_id=VALUES(unidad_venta_id);
INSERT INTO item_plato (id_item_plato, plato_id, cantidad, sub_total, pedido_id) VALUES
    (311, 103, 40, 60000.00, 237),
    (312, 205, 20, 30000.00, 237),
    (313, 102, 20, 40000.00, 237),
    (314, 103, 35, 52500.00, 238),
    (315, 206, 20, 28000.00, 238),
    (316, 208, 12, 36000.00, 238),
    (317, 103, 30, 45000.00, 239),
    (318, 205, 15, 22500.00, 239),
    (319, 102, 10, 20000.00, 239)
    ON DUPLICATE KEY UPDATE plato_id=VALUES(plato_id), cantidad=VALUES(cantidad), sub_total=VALUES(sub_total), pedido_id=VALUES(pedido_id);

-- Pedidos de Pizza Truck (festival 1): muchos pedidos chicos, de una o dos pizzas
INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id) VALUES
    (240, '2026-10-01', 1, 200),
    (241, '2026-10-01', 1, 200),
    (242, '2026-10-02', 1, 200),
    (243, '2026-10-02', 1, 200),
    (244, '2026-10-03', 1, 200),
    (245, '2026-10-03', 1, 200),
    (246, '2026-10-04', 1, 200),
    (247, '2026-10-04', 1, 200),
    (248, '2026-10-04', 1, 200),
    (249, '2026-10-05', 1, 200),
    (250, '2026-10-05', 1, 200),
    (251, '2026-10-06', 1, 200),
    (252, '2026-10-06', 1, 200),
    (253, '2026-10-06', 1, 200),
    (254, '2026-10-07', 1, 200),
    (255, '2026-10-07', 1, 200),
    (256, '2026-10-08', 1, 200),
    (257, '2026-10-08', 1, 200),
    (258, '2026-10-08', 1, 200),
    (259, '2026-10-09', 1, 200),
    (260, '2026-10-09', 1, 200),
    (261, '2026-10-09', 1, 200),
    (262, '2026-10-10', 1, 200),
    (263, '2026-10-10', 1, 200),
    (264, '2026-10-11', 1, 200),
    (265, '2026-10-11', 1, 200),
    (266, '2026-10-11', 1, 200),
    (267, '2026-10-12', 1, 200),
    (268, '2026-10-12', 1, 200),
    (269, '2026-10-12', 1, 200)
    ON DUPLICATE KEY UPDATE fecha_transaccion=VALUES(fecha_transaccion), festival_id=VALUES(festival_id), unidad_venta_id=VALUES(unidad_venta_id);
INSERT INTO item_plato (id_item_plato, plato_id, cantidad, sub_total, pedido_id) VALUES
    (320, 210, 1, 8500.00, 240),
    (321, 211, 1, 2000.00, 240),
    (322, 207, 1, 1500.00, 240),
    (323, 210, 1, 8500.00, 241),
    (324, 211, 1, 2000.00, 241),
    (325, 207, 2, 3000.00, 241),
    (326, 210, 1, 8500.00, 242),
    (327, 211, 2, 4000.00, 242),
    (328, 207, 1, 1500.00, 242),
    (329, 210, 1, 8500.00, 243),
    (330, 210, 2, 17000.00, 244),
    (331, 211, 1, 2000.00, 244),
    (332, 210, 1, 8500.00, 245),
    (333, 211, 1, 2000.00, 245),
    (334, 207, 1, 1500.00, 245),
    (335, 210, 1, 8500.00, 246),
    (336, 211, 1, 2000.00, 246),
    (337, 207, 1, 1500.00, 246),
    (338, 210, 1, 8500.00, 247),
    (339, 211, 1, 2000.00, 247),
    (340, 210, 2, 17000.00, 248),
    (341, 211, 2, 4000.00, 248),
    (342, 210, 2, 17000.00, 249),
    (343, 207, 1, 1500.00, 249),
    (344, 210, 1, 8500.00, 250),
    (345, 207, 2, 3000.00, 250),
    (346, 210, 1, 8500.00, 251),
    (347, 210, 1, 8500.00, 252),
    (348, 207, 2, 3000.00, 252),
    (349, 210, 1, 8500.00, 253),
    (350, 207, 2, 3000.00, 253),
    (351, 210, 1, 8500.00, 254),
    (352, 207, 2, 3000.00, 254),
    (353, 210, 1, 8500.00, 255),
    (354, 210, 1, 8500.00, 256),
    (355, 211, 1, 2000.00, 256),
    (356, 210, 1, 8500.00, 257),
    (357, 207, 2, 3000.00, 257),
    (358, 210, 2, 17000.00, 258),
    (359, 211, 2, 4000.00, 258),
    (360, 207, 2, 3000.00, 258),
    (361, 210, 1, 8500.00, 259),
    (362, 211, 2, 4000.00, 259),
    (363, 207, 1, 1500.00, 259),
    (364, 210, 1, 8500.00, 260),
    (365, 211, 2, 4000.00, 260),
    (366, 207, 1, 1500.00, 260),
    (367, 210, 1, 8500.00, 261),
    (368, 211, 2, 4000.00, 261),
    (369, 207, 2, 3000.00, 261),
    (370, 210, 2, 17000.00, 262),
    (371, 211, 1, 2000.00, 262),
    (372, 210, 2, 17000.00, 263),
    (373, 211, 2, 4000.00, 263),
    (374, 210, 1, 8500.00, 264),
    (375, 207, 1, 1500.00, 264),
    (376, 210, 1, 8500.00, 265),
    (377, 211, 1, 2000.00, 265),
    (378, 207, 1, 1500.00, 265),
    (379, 210, 1, 8500.00, 266),
    (380, 211, 1, 2000.00, 266),
    (381, 207, 2, 3000.00, 266),
    (382, 210, 2, 17000.00, 267),
    (383, 211, 1, 2000.00, 267),
    (384, 210, 2, 17000.00, 268),
    (385, 210, 2, 17000.00, 269),
    (386, 211, 2, 4000.00, 269),
    (387, 207, 2, 3000.00, 269)
    ON DUPLICATE KEY UPDATE plato_id=VALUES(plato_id), cantidad=VALUES(cantidad), sub_total=VALUES(sub_total), pedido_id=VALUES(pedido_id);

-- Pedidos de Choripanes del Sur (festival 2): pedidos chicos, hasta 5 choripanes más bebida
INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id) VALUES
    (275, '2026-10-20', 2, 3),
    (279, '2026-10-20', 2, 3),
    (280, '2026-10-20', 2, 3),
    (289, '2026-10-20', 2, 3),
    (297, '2026-10-20', 2, 3),
    (299, '2026-10-20', 2, 3),
    (307, '2026-10-20', 2, 3),
    (273, '2026-10-21', 2, 3),
    (277, '2026-10-21', 2, 3),
    (283, '2026-10-21', 2, 3),
    (293, '2026-10-21', 2, 3),
    (300, '2026-10-21', 2, 3),
    (302, '2026-10-21', 2, 3),
    (312, '2026-10-21', 2, 3),
    (317, '2026-10-21', 2, 3),
    (292, '2026-10-22', 2, 3),
    (304, '2026-10-22', 2, 3),
    (287, '2026-10-23', 2, 3),
    (288, '2026-10-23', 2, 3),
    (291, '2026-10-23', 2, 3),
    (306, '2026-10-23', 2, 3),
    (308, '2026-10-23', 2, 3),
    (313, '2026-10-23', 2, 3),
    (276, '2026-10-24', 2, 3),
    (295, '2026-10-24', 2, 3),
    (296, '2026-10-24', 2, 3),
    (310, '2026-10-24', 2, 3),
    (318, '2026-10-24', 2, 3),
    (274, '2026-10-25', 2, 3),
    (284, '2026-10-25', 2, 3),
    (285, '2026-10-25', 2, 3),
    (319, '2026-10-25', 2, 3),
    (272, '2026-10-26', 2, 3),
    (278, '2026-10-26', 2, 3),
    (282, '2026-10-26', 2, 3),
    (301, '2026-10-26', 2, 3),
    (305, '2026-10-26', 2, 3),
    (315, '2026-10-26', 2, 3),
    (270, '2026-10-27', 2, 3),
    (271, '2026-10-27', 2, 3),
    (286, '2026-10-27', 2, 3),
    (290, '2026-10-27', 2, 3),
    (298, '2026-10-27', 2, 3),
    (303, '2026-10-27', 2, 3),
    (309, '2026-10-27', 2, 3),
    (311, '2026-10-27', 2, 3),
    (281, '2026-10-28', 2, 3),
    (294, '2026-10-28', 2, 3),
    (314, '2026-10-28', 2, 3),
    (316, '2026-10-28', 2, 3)
    ON DUPLICATE KEY UPDATE fecha_transaccion=VALUES(fecha_transaccion), festival_id=VALUES(festival_id), unidad_venta_id=VALUES(unidad_venta_id);
INSERT INTO item_plato (id_item_plato, plato_id, cantidad, sub_total, pedido_id) VALUES
    (420, 200, 3, 12000.00, 275),
    (421, 3, 3, 10500.00, 275),
    (422, 201, 5, 32500.00, 279),
    (423, 3, 1, 3500.00, 279),
    (424, 201, 5, 32500.00, 280),
    (425, 3, 2, 7000.00, 280),
    (426, 200, 2, 8000.00, 289),
    (427, 207, 3, 4500.00, 289),
    (428, 200, 5, 20000.00, 297),
    (429, 3, 3, 10500.00, 297),
    (430, 201, 3, 19500.00, 299),
    (431, 1, 2, 17000.00, 299),
    (432, 3, 1, 3500.00, 299),
    (433, 200, 2, 8000.00, 307),
    (434, 207, 2, 3000.00, 307),
    (435, 200, 1, 4000.00, 273),
    (436, 202, 1, 5500.00, 273),
    (437, 200, 3, 12000.00, 277),
    (438, 202, 1, 5500.00, 277),
    (439, 3, 2, 7000.00, 277),
    (440, 200, 2, 8000.00, 283),
    (441, 1, 1, 8500.00, 283),
    (442, 200, 2, 8000.00, 293),
    (443, 200, 2, 8000.00, 300),
    (444, 201, 1, 6500.00, 302),
    (445, 202, 2, 11000.00, 302),
    (446, 201, 1, 6500.00, 312),
    (447, 207, 1, 1500.00, 312),
    (448, 200, 2, 8000.00, 317),
    (449, 200, 2, 8000.00, 292),
    (450, 200, 4, 16000.00, 304),
    (451, 3, 2, 7000.00, 304),
    (452, 200, 2, 8000.00, 287),
    (453, 201, 3, 19500.00, 288),
    (454, 3, 1, 3500.00, 288),
    (455, 200, 4, 16000.00, 291),
    (456, 1, 2, 17000.00, 291),
    (457, 3, 1, 3500.00, 291),
    (458, 200, 3, 12000.00, 306),
    (459, 1, 1, 8500.00, 306),
    (460, 207, 2, 3000.00, 306),
    (461, 201, 3, 19500.00, 308),
    (462, 207, 3, 4500.00, 308),
    (463, 200, 5, 20000.00, 313),
    (464, 3, 2, 7000.00, 313),
    (465, 200, 5, 20000.00, 276),
    (466, 1, 1, 8500.00, 276),
    (467, 3, 2, 7000.00, 276),
    (468, 200, 1, 4000.00, 295),
    (469, 3, 3, 10500.00, 295),
    (470, 200, 2, 8000.00, 296),
    (471, 207, 3, 4500.00, 296),
    (472, 200, 2, 8000.00, 310),
    (473, 3, 2, 7000.00, 310),
    (474, 200, 2, 8000.00, 318),
    (475, 201, 3, 19500.00, 274),
    (476, 207, 2, 3000.00, 274),
    (477, 200, 3, 12000.00, 284),
    (478, 202, 1, 5500.00, 284),
    (479, 3, 1, 3500.00, 284),
    (480, 200, 3, 12000.00, 285),
    (481, 200, 1, 4000.00, 319),
    (482, 207, 2, 3000.00, 319),
    (483, 201, 4, 26000.00, 272),
    (484, 3, 3, 10500.00, 272),
    (485, 201, 3, 19500.00, 278),
    (486, 202, 2, 11000.00, 278),
    (487, 207, 2, 3000.00, 278),
    (488, 201, 2, 13000.00, 282),
    (489, 1, 1, 8500.00, 282),
    (490, 207, 3, 4500.00, 282),
    (491, 201, 3, 19500.00, 301),
    (492, 3, 2, 7000.00, 301),
    (493, 200, 2, 8000.00, 305),
    (494, 201, 2, 13000.00, 315),
    (495, 3, 2, 7000.00, 315),
    (496, 201, 3, 19500.00, 270),
    (497, 3, 3, 10500.00, 270),
    (498, 200, 1, 4000.00, 271),
    (499, 3, 3, 10500.00, 271),
    (500, 200, 5, 20000.00, 286),
    (501, 207, 1, 1500.00, 286),
    (502, 200, 3, 12000.00, 290),
    (503, 202, 1, 5500.00, 290),
    (504, 3, 2, 7000.00, 290),
    (505, 200, 5, 20000.00, 298),
    (506, 200, 1, 4000.00, 303),
    (507, 3, 3, 10500.00, 303),
    (508, 200, 1, 4000.00, 309),
    (509, 3, 3, 10500.00, 309),
    (510, 200, 1, 4000.00, 311),
    (511, 3, 1, 3500.00, 311),
    (512, 200, 4, 16000.00, 281),
    (513, 207, 1, 1500.00, 281),
    (514, 200, 3, 12000.00, 294),
    (515, 202, 2, 11000.00, 294),
    (516, 200, 2, 8000.00, 314),
    (517, 1, 1, 8500.00, 314),
    (518, 3, 1, 3500.00, 314),
    (519, 200, 3, 12000.00, 316),
    (520, 202, 1, 5500.00, 316)
    ON DUPLICATE KEY UPDATE plato_id=VALUES(plato_id), cantidad=VALUES(cantidad), sub_total=VALUES(sub_total), pedido_id=VALUES(pedido_id);

-- Pedidos de Pizza Nostra (festival 2): pedidos chicos, una a cinco pizzas más bebida
INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id) VALUES
    (320, '2026-10-20', 2, 101),
    (331, '2026-10-20', 2, 101),
    (332, '2026-10-20', 2, 101),
    (350, '2026-10-20', 2, 101),
    (352, '2026-10-20', 2, 101),
    (359, '2026-10-20', 2, 101),
    (326, '2026-10-21', 2, 101),
    (343, '2026-10-21', 2, 101),
    (347, '2026-10-21', 2, 101),
    (322, '2026-10-22', 2, 101),
    (324, '2026-10-22', 2, 101),
    (325, '2026-10-22', 2, 101),
    (336, '2026-10-22', 2, 101),
    (340, '2026-10-22', 2, 101),
    (346, '2026-10-22', 2, 101),
    (355, '2026-10-22', 2, 101),
    (335, '2026-10-23', 2, 101),
    (337, '2026-10-23', 2, 101),
    (339, '2026-10-23', 2, 101),
    (342, '2026-10-23', 2, 101),
    (344, '2026-10-23', 2, 101),
    (357, '2026-10-23', 2, 101),
    (333, '2026-10-24', 2, 101),
    (354, '2026-10-24', 2, 101),
    (358, '2026-10-24', 2, 101),
    (338, '2026-10-25', 2, 101),
    (351, '2026-10-25', 2, 101),
    (321, '2026-10-26', 2, 101),
    (327, '2026-10-26', 2, 101),
    (329, '2026-10-26', 2, 101),
    (330, '2026-10-26', 2, 101),
    (349, '2026-10-26', 2, 101),
    (353, '2026-10-26', 2, 101),
    (323, '2026-10-27', 2, 101),
    (334, '2026-10-27', 2, 101),
    (348, '2026-10-27', 2, 101),
    (328, '2026-10-28', 2, 101),
    (341, '2026-10-28', 2, 101),
    (345, '2026-10-28', 2, 101),
    (356, '2026-10-28', 2, 101)
    ON DUPLICATE KEY UPDATE fecha_transaccion=VALUES(fecha_transaccion), festival_id=VALUES(festival_id), unidad_venta_id=VALUES(unidad_venta_id);
INSERT INTO item_plato (id_item_plato, plato_id, cantidad, sub_total, pedido_id) VALUES
    (521, 101, 5, 35000.00, 320),
    (522, 203, 2, 17000.00, 331),
    (523, 101, 2, 14000.00, 332),
    (524, 102, 3, 6000.00, 332),
    (525, 101, 2, 14000.00, 350),
    (526, 207, 3, 4500.00, 350),
    (527, 203, 1, 8500.00, 352),
    (528, 101, 2, 14000.00, 359),
    (529, 101, 3, 21000.00, 326),
    (530, 102, 3, 6000.00, 326),
    (531, 203, 3, 25500.00, 343),
    (532, 101, 2, 14000.00, 347),
    (533, 204, 3, 24000.00, 322),
    (534, 102, 1, 2000.00, 322),
    (535, 203, 2, 17000.00, 324),
    (536, 102, 2, 4000.00, 324),
    (537, 203, 3, 25500.00, 325),
    (538, 207, 3, 4500.00, 325),
    (539, 101, 5, 35000.00, 336),
    (540, 102, 3, 6000.00, 336),
    (541, 101, 2, 14000.00, 340),
    (542, 207, 2, 3000.00, 340),
    (543, 101, 2, 14000.00, 346),
    (544, 204, 2, 16000.00, 355),
    (545, 204, 1, 8000.00, 335),
    (546, 207, 3, 4500.00, 335),
    (547, 101, 2, 14000.00, 337),
    (548, 101, 2, 14000.00, 339),
    (549, 203, 5, 42500.00, 342),
    (550, 101, 3, 21000.00, 344),
    (551, 102, 3, 6000.00, 344),
    (552, 101, 1, 7000.00, 357),
    (553, 203, 4, 34000.00, 333),
    (554, 207, 1, 1500.00, 333),
    (555, 101, 4, 28000.00, 354),
    (556, 204, 1, 8000.00, 358),
    (557, 207, 3, 4500.00, 358),
    (558, 101, 2, 14000.00, 338),
    (559, 102, 1, 2000.00, 338),
    (560, 204, 2, 16000.00, 351),
    (561, 102, 3, 6000.00, 351),
    (562, 101, 2, 14000.00, 321),
    (563, 102, 2, 4000.00, 321),
    (564, 101, 3, 21000.00, 327),
    (565, 102, 1, 2000.00, 327),
    (566, 203, 2, 17000.00, 329),
    (567, 102, 2, 4000.00, 329),
    (568, 204, 3, 24000.00, 330),
    (569, 102, 2, 4000.00, 330),
    (570, 203, 3, 25500.00, 349),
    (571, 207, 1, 1500.00, 349),
    (572, 101, 3, 21000.00, 353),
    (573, 207, 2, 3000.00, 353),
    (574, 203, 5, 42500.00, 323),
    (575, 101, 5, 35000.00, 334),
    (576, 207, 3, 4500.00, 334),
    (577, 101, 5, 35000.00, 348),
    (578, 207, 2, 3000.00, 348),
    (579, 204, 3, 24000.00, 328),
    (580, 101, 2, 14000.00, 341),
    (581, 102, 1, 2000.00, 341),
    (582, 204, 2, 16000.00, 345),
    (583, 102, 1, 2000.00, 345),
    (584, 203, 5, 42500.00, 356),
    (585, 102, 2, 4000.00, 356)
    ON DUPLICATE KEY UPDATE plato_id=VALUES(plato_id), cantidad=VALUES(cantidad), sub_total=VALUES(sub_total), pedido_id=VALUES(pedido_id);

-- Pedidos de Cositas Dulces (festival 2): pedidos chicos con dos o tres cafés como mucho
INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id) VALUES
    (365, '2026-10-20', 2, 201),
    (372, '2026-10-20', 2, 201),
    (375, '2026-10-20', 2, 201),
    (378, '2026-10-20', 2, 201),
    (385, '2026-10-20', 2, 201),
    (387, '2026-10-20', 2, 201),
    (411, '2026-10-20', 2, 201),
    (373, '2026-10-21', 2, 201),
    (406, '2026-10-21', 2, 201),
    (409, '2026-10-21', 2, 201),
    (362, '2026-10-22', 2, 201),
    (364, '2026-10-22', 2, 201),
    (367, '2026-10-22', 2, 201),
    (370, '2026-10-22', 2, 201),
    (379, '2026-10-22', 2, 201),
    (380, '2026-10-22', 2, 201),
    (382, '2026-10-22', 2, 201),
    (394, '2026-10-22', 2, 201),
    (399, '2026-10-22', 2, 201),
    (401, '2026-10-22', 2, 201),
    (363, '2026-10-23', 2, 201),
    (396, '2026-10-23', 2, 201),
    (407, '2026-10-23', 2, 201),
    (360, '2026-10-24', 2, 201),
    (366, '2026-10-24', 2, 201),
    (376, '2026-10-24', 2, 201),
    (377, '2026-10-24', 2, 201),
    (386, '2026-10-24', 2, 201),
    (392, '2026-10-24', 2, 201),
    (408, '2026-10-24', 2, 201),
    (410, '2026-10-24', 2, 201),
    (369, '2026-10-25', 2, 201),
    (371, '2026-10-25', 2, 201),
    (381, '2026-10-25', 2, 201),
    (389, '2026-10-25', 2, 201),
    (402, '2026-10-25', 2, 201),
    (368, '2026-10-26', 2, 201),
    (391, '2026-10-26', 2, 201),
    (395, '2026-10-26', 2, 201),
    (403, '2026-10-26', 2, 201),
    (404, '2026-10-26', 2, 201),
    (374, '2026-10-27', 2, 201),
    (383, '2026-10-27', 2, 201),
    (384, '2026-10-27', 2, 201),
    (393, '2026-10-27', 2, 201),
    (400, '2026-10-27', 2, 201),
    (405, '2026-10-27', 2, 201),
    (413, '2026-10-27', 2, 201),
    (414, '2026-10-27', 2, 201),
    (361, '2026-10-28', 2, 201),
    (388, '2026-10-28', 2, 201),
    (390, '2026-10-28', 2, 201),
    (397, '2026-10-28', 2, 201),
    (398, '2026-10-28', 2, 201),
    (412, '2026-10-28', 2, 201)
    ON DUPLICATE KEY UPDATE fecha_transaccion=VALUES(fecha_transaccion), festival_id=VALUES(festival_id), unidad_venta_id=VALUES(unidad_venta_id);
INSERT INTO item_plato (id_item_plato, plato_id, cantidad, sub_total, pedido_id) VALUES
    (600, 212, 2, 5000.00, 365),
    (601, 208, 1, 3000.00, 365),
    (602, 215, 1, 3000.00, 372),
    (603, 214, 2, 4000.00, 372),
    (604, 215, 1, 3000.00, 375),
    (605, 216, 1, 3000.00, 375),
    (606, 214, 1, 2000.00, 375),
    (607, 212, 5, 12500.00, 378),
    (608, 214, 3, 6000.00, 378),
    (609, 213, 2, 6000.00, 385),
    (610, 214, 3, 6000.00, 385),
    (611, 215, 2, 6000.00, 387),
    (612, 214, 2, 4000.00, 387),
    (613, 212, 3, 7500.00, 411),
    (614, 214, 1, 2000.00, 411),
    (615, 212, 4, 10000.00, 373),
    (616, 214, 2, 4000.00, 373),
    (617, 217, 4, 7200.00, 406),
    (618, 212, 1, 2500.00, 406),
    (619, 208, 2, 6000.00, 406),
    (620, 212, 2, 5000.00, 409),
    (621, 214, 2, 4000.00, 409),
    (622, 212, 3, 7500.00, 362),
    (623, 214, 1, 2000.00, 362),
    (624, 212, 2, 5000.00, 364),
    (625, 214, 2, 4000.00, 364),
    (626, 217, 2, 3600.00, 367),
    (627, 208, 2, 6000.00, 367),
    (628, 215, 2, 6000.00, 370),
    (629, 214, 1, 2000.00, 370),
    (630, 212, 1, 2500.00, 379),
    (631, 214, 2, 4000.00, 379),
    (632, 217, 2, 3600.00, 380),
    (633, 208, 2, 6000.00, 380),
    (634, 216, 2, 6000.00, 382),
    (635, 214, 2, 4000.00, 382),
    (636, 212, 2, 5000.00, 394),
    (637, 215, 2, 6000.00, 394),
    (638, 214, 1, 2000.00, 394),
    (639, 215, 2, 6000.00, 399),
    (640, 214, 2, 4000.00, 399),
    (641, 216, 1, 3000.00, 401),
    (642, 208, 1, 3000.00, 401),
    (643, 212, 2, 5000.00, 363),
    (644, 214, 3, 6000.00, 363),
    (645, 217, 3, 5400.00, 396),
    (646, 216, 2, 6000.00, 396),
    (647, 215, 1, 3000.00, 407),
    (648, 216, 1, 3000.00, 407),
    (649, 214, 2, 4000.00, 407),
    (650, 217, 3, 5400.00, 360),
    (651, 213, 1, 3000.00, 366),
    (652, 214, 2, 4000.00, 366),
    (653, 217, 3, 5400.00, 376),
    (654, 215, 1, 3000.00, 377),
    (655, 214, 3, 6000.00, 377),
    (656, 217, 3, 5400.00, 386),
    (657, 214, 2, 4000.00, 386),
    (658, 216, 2, 6000.00, 392),
    (659, 214, 2, 4000.00, 392),
    (660, 215, 2, 6000.00, 408),
    (661, 217, 1, 1800.00, 410),
    (662, 214, 2, 4000.00, 410),
    (663, 212, 3, 7500.00, 369),
    (664, 214, 2, 4000.00, 369),
    (665, 215, 1, 3000.00, 371),
    (666, 214, 3, 6000.00, 371),
    (667, 212, 1, 2500.00, 381),
    (668, 214, 3, 6000.00, 381),
    (669, 212, 4, 10000.00, 389),
    (670, 216, 1, 3000.00, 402),
    (671, 212, 2, 5000.00, 368),
    (672, 216, 2, 6000.00, 368),
    (673, 212, 5, 12500.00, 391),
    (674, 214, 3, 6000.00, 391),
    (675, 215, 2, 6000.00, 395),
    (676, 214, 2, 4000.00, 395),
    (677, 215, 1, 3000.00, 403),
    (678, 214, 3, 6000.00, 403),
    (679, 212, 5, 12500.00, 404),
    (680, 214, 2, 4000.00, 404),
    (681, 212, 5, 12500.00, 374),
    (682, 213, 2, 6000.00, 383),
    (683, 208, 1, 3000.00, 383),
    (684, 213, 1, 3000.00, 384),
    (685, 216, 2, 6000.00, 384),
    (686, 214, 2, 4000.00, 384),
    (687, 216, 1, 3000.00, 393),
    (688, 213, 1, 3000.00, 393),
    (689, 212, 2, 5000.00, 400),
    (690, 214, 2, 4000.00, 400),
    (691, 213, 2, 6000.00, 405),
    (692, 216, 2, 6000.00, 405),
    (693, 208, 1, 3000.00, 405),
    (694, 215, 2, 6000.00, 413),
    (695, 208, 2, 6000.00, 413),
    (696, 212, 3, 7500.00, 414),
    (697, 214, 3, 6000.00, 414),
    (698, 212, 4, 10000.00, 361),
    (699, 214, 1, 2000.00, 361),
    (700, 217, 3, 5400.00, 388),
    (701, 214, 3, 6000.00, 388),
    (702, 212, 2, 5000.00, 390),
    (703, 208, 1, 3000.00, 390),
    (704, 213, 1, 3000.00, 397),
    (705, 214, 2, 4000.00, 397),
    (706, 216, 2, 6000.00, 398),
    (707, 214, 2, 4000.00, 398),
    (708, 212, 1, 2500.00, 412)
    ON DUPLICATE KEY UPDATE plato_id=VALUES(plato_id), cantidad=VALUES(cantidad), sub_total=VALUES(sub_total), pedido_id=VALUES(pedido_id);
