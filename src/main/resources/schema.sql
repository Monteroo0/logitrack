-- Tabla de roles
CREATE TABLE rol (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de usuarios
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    rol_id BIGINT,
    FOREIGN KEY (rol_id) REFERENCES rol(id)
);

-- Tabla de bodegas
CREATE TABLE bodega (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(150) NOT NULL,
    capacidad INT NOT NULL,
    encargado_id BIGINT,
    FOREIGN KEY (encargado_id) REFERENCES usuario(id)
);

-- Tabla de productos
CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    stock INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL
);

-- Tabla de movimientos de inventario
CREATE TABLE movimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    usuario_id BIGINT,
    bodega_origen_id BIGINT,
    bodega_destino_id BIGINT,
    observaciones TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (bodega_origen_id) REFERENCES bodega(id),
    FOREIGN KEY (bodega_destino_id) REFERENCES bodega(id)
);

-- Tabla intermedia para productos en movimientos
CREATE TABLE movimiento_producto (
    movimiento_id BIGINT,
    producto_id BIGINT,
    cantidad INT NOT NULL,
    PRIMARY KEY (movimiento_id, producto_id),
    FOREIGN KEY (movimiento_id) REFERENCES movimiento(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Tabla de auditoría
CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_operacion VARCHAR(20) NOT NULL,
    fecha DATETIME NOT NULL,
    usuario_id BIGINT,
    entidad VARCHAR(50) NOT NULL,
    valores_anteriores TEXT,
    valores_nuevos TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);