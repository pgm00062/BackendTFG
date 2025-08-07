-- Script para crear todas las tablas necesarias

-- Tabla users
DROP TABLE IF EXISTS times;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    dni VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Tabla projects
CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'EN_PROGRESO',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    budget DECIMAL(10,2) NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla times
CREATE TABLE times (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time DATETIME NOT NULL,
    end_time DATETIME NULL,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500) NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insertar datos de prueba
INSERT INTO users (name, surname, email, dni, password) 
VALUES 
('Usuario', 'Prueba', 'test@example.com', '12345678A', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('Diego', 'García', 'diego@example.com', '87654321B', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('María', 'López', 'maria@example.com', '11223344C', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');

INSERT INTO projects (name, description, type, status, start_date, end_date, budget, user_id) 
VALUES 
('Proyecto Test', 'Descripción del proyecto de prueba', 'DESARROLLO', 'EN_PROGRESO', '2025-01-01', '2025-12-31', 1000.00, 1),
('Aplicación Web', 'Desarrollo de aplicación web moderna', 'DESARROLLO', 'EN_PROGRESO', '2025-02-01', '2025-08-31', 2500.00, 2),
('Diseño UI/UX', 'Diseño de interfaces de usuario', 'DISENO', 'EN_PROGRESO', '2025-03-01', '2025-06-30', 1500.00, 3); 