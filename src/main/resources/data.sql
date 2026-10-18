-- user admin inicial (admin), clave (admin123)

INSERT INTO user (id, username, password, enabled, createdat, updatedat)
VALUES (1, 'admin', '$2a$10$eACCpP0iL3N2X48NTh6T4.K9X4lG1o6W0p.a.x7F0H7o2M.e2C1KG', 1, NOW(), NOW())
    ON DUPLICATE KEY UPDATE username=username;

-- asignación rol admin
INSERT INTO user_role (id, role, createdat, updatedat, user_id)
VALUES (1, 'ROLE_ADMIN', NOW(), NOW(), 1)
    ON DUPLICATE KEY UPDATE role=role;

--datos iniciales plato prueba
INSERT INTO plato (id_plato, nombre, precio, costo) VALUES
    (1, 'Hamburguesa Completa', 8500.00, 3200.00),
     (2, 'Papas Rústicas', 4500.00, 1500.00),
     (3, 'Cerveza Artesanal', 3500.00, 1200.00)
    ON DUPLICATE KEY UPDATE nombre=nombre;