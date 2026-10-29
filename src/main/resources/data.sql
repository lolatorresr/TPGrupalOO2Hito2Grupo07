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

-- Cajero del staff: cajero1 / admin123
INSERT INTO user (id, username, password, enabled, created_at, updated_at)
VALUES (102, 'cajero1', '$2a$10$YFUsBtgsmVCYDbs0DJfO3efGeZPgE3xcD5ektiqczlC5phrR6m8/e', 1, NOW(), NOW())
    ON DUPLICATE KEY UPDATE username=username;
INSERT INTO user_role (id, role, created_at, updated_at, user_id)
VALUES (102, 'ROLE_EMPLEADO', NOW(), NOW(), 102)
    ON DUPLICATE KEY UPDATE role=role;

INSERT INTO persona (id_persona, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, user_id)
VALUES (3, 'Carlos', 'López', 28333444, '1982-11-03', '2021-09-10', 'carlos.lopez@epicentro.com', 102)
    ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);

INSERT INTO cajero (id_persona, turno_trabajo)
VALUES (3, 'MAÑANA')
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

-- ---------- Cierre de caja de ejemplo ----------
-- Pizza Nostra (unidad 101) cerró el 22/10 con el pedido 102: 3 pizzas + 3 gaseosas = 27.000.
-- Cerró Carlos López (cajero1). Sirve para que el historial de cierres no aparezca vacío.
INSERT INTO cierre_caja (id_cierre_caja, fecha, unidad_venta_id, festival_id, cajero_id, cantidad_pedidos, monto_total, created_at)
VALUES (1, '2026-10-22', 101, 2, 3, 1, 27000.00, NOW())
    ON DUPLICATE KEY UPDATE monto_total=VALUES(monto_total);

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

-- Parametros de negocio


