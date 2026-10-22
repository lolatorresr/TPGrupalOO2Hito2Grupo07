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

-- ---------- Personal (Persona es abstracta: cada fila necesita su subtipo) ----------
-- Responsable de unidad (vinculado al usuario responsable1)
INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, activo, user_id)
VALUES (1, 'Martín', 'García', 30111222, '1985-04-12', '2022-03-01', 'responsable1@epicentro.com', 1, 2)
    ON DUPLICATE KEY UPDATE nombre=nombre;
INSERT INTO cajero (id_persona, turno_trabajo, recaudacion_total)
VALUES (1, 'MAÑANA', 0)
    ON DUPLICATE KEY UPDATE turno_trabajo=turno_trabajo;

-- Cocinero del staff
INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, activo, user_id)
VALUES (2, 'Lucía', 'Pérez', 35222333, '1990-08-25', '2023-06-15', 'lucia.perez@epicentro.com', 1, NULL)
    ON DUPLICATE KEY UPDATE nombre=nombre;
INSERT INTO cocinero (id_persona, especialidad, categoria)
VALUES (2, 'Parrilla', 'COCINERO')
    ON DUPLICATE KEY UPDATE especialidad=especialidad;

-- ---------- Festivales ----------
INSERT INTO festival (id_festival, nombre, temporada, fecha_inicio, fecha_fin) VALUES
     (1, 'Lollapalooza 2026', 'Verano', '2026-03-15', '2026-03-18'),
    (2, 'Primavera Sound 2026', 'Primavera', '2026-10-20', '2026-10-23')
    ON DUPLICATE KEY UPDATE nombre=nombre;

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


-- ---------- Tablas intermedias ----------
INSERT IGNORE INTO unidad_plato (unidad_id, plato_id) VALUES (1, 1), (1, 2), (2, 3), (3, 1), (3, 3);
INSERT IGNORE INTO unidad_staff (unidad_id, persona_id) VALUES (1, 1), (1, 2), (3, 1);


-- Pedidos de prueba para el CU Plato Estrella

-- 1. Pedidos
INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id)
VALUES (1, NOW(), 1, 1)
    ON DUPLICATE KEY UPDATE id_pedido=id_pedido;

INSERT INTO pedido (id_pedido, fecha_transaccion, festival_id, unidad_venta_id)
VALUES (2, NOW(), 1, 1)
    ON DUPLICATE KEY UPDATE id_pedido=id_pedido;

-- 2. Ítems del pedido (SIN la columna 'precio_unitario')
INSERT INTO item_plato (id_item_plato, cantidad, plato_id, pedido_id)
VALUES (1, 5, 1, 1)
    ON DUPLICATE KEY UPDATE cantidad=cantidad;

INSERT INTO item_plato (id_item_plato, cantidad, plato_id, pedido_id)
VALUES (2, 2, 2, 2)
    ON DUPLICATE KEY UPDATE cantidad=cantidad;