-- Roles
INSERT INTO rol (nombre) VALUES ('ADMIN'), ('EMPLEADO');

-- Usuarios
INSERT INTO usuario (username, password, nombre, rol_id) VALUES
('admin', '$2a$10$2qzzjfXa//.8ywmueOIdNueZ/ETC.wHkNQAVVAPbjwJxJ84kam64K', 'Administrador', 1),
('empleado', '$2a$10$yuRBKlJ9jVs1oRQNRj8cjOApwDZM3Vc84MKbGMxMF7jU43dzU5NFe', 'Empleado', 2);

-- Bodegas
INSERT INTO bodega (nombre, ubicacion, capacidad, encargado_id) VALUES
('Central', 'Av. Principal 123', 1000, 1),
('Sucursal Norte', 'Calle Norte 456', 500, 2);

-- Productos
INSERT INTO producto (nombre, categoria, stock, precio) VALUES
('Laptop', 'Electrónica', 15, 1200.00),
('Mouse', 'Electrónica', 8, 25.50),
('Caja', 'Embalaje', 50, 2.00),
('Monitor', 'Electrónica', 5, 300.00);