










-- 1. Insertar Catálogos (Tablas maestras)

INSERT INTO ESTADO_HABITACION (descripcion, estado, fechacreacion)
VALUES 
('DISPONIBLE', true, NOW()),
('OCUPADO', true, NOW()),
('MANTENIMIENTO', true, NOW());

INSERT INTO CATEGORIA (descripcion, estado, fechacreacion)
VALUES 
('Matrimonial', true, NOW()),
('Doble', true, NOW()),
('Individual', true, NOW());

INSERT INTO PISO (descripcion, estado, fechacreacion)
VALUES 
('PRIMERO', true, NOW()),
('SEGUNDO', true, NOW()),
('TERCERO', true, NOW());

-- 2. Insertar Habitaciones
INSERT INTO HABITACION (numero, detalle, precio, idestadohabitacion, idpiso, idcategoria, estado, fechacreacion)
VALUES
('001', 'WIFI + BAÑO + TV + CABLE', 70.00, 1, 1, 3, true, NOW()),
('002', 'WIFI + BAÑO + TV + CABLE', 80.00, 1, 1, 2, true, NOW()),
('003', 'BAÑO + TV + CABLE', 60.00, 1, 1, 3, true, NOW()),
('004', 'WIFI + BAÑO + TV + CABLE', 80.00, 1, 1, 2, true, NOW()),
('005', 'WIFI + BAÑO', 50.00, 1, 1, 3, true, NOW()),
('006', 'WIFI + BAÑO + TV 4K + CABLE', 80.00, 1, 2, 3, true, NOW()),
('007', 'WIFI + BAÑO + TV 4K + CABLE', 90.00, 1, 2, 2, true, NOW()),
('008', 'WIFI + BAÑO + TV + CABLE', 70.00, 1, 2, 3, true, NOW()),
('009', 'WIFI + BAÑO + TV + CABLE', 80.00, 1, 2, 2, true, NOW()),
('010', 'WIFI + BAÑO + TV + CABLE', 70.00, 1, 2, 3, true, NOW());



INSERT INTO PRODUCTO (nombre, detalle, precio, cantidad, estado, imagen_url, fechacreacion)
VALUES
('Agua Mineral 500ml', 'Botella de agua sin gas, cortesía', 3.50, 100, TRUE, 'https://goo.su/tUKXkxv', '2026-06-15 08:00:00'),
('Pack Snacks Salados', 'Maní tostado y papas fritas', 8.00, 50, TRUE, 'https://goo.su/RXVpS', '2026-06-15 08:30:00'),
('Kit de Aseo Dental', 'Cepillo de dientes y pasta dental de viaje', 5.00, 200, TRUE, 'https://goo.su/0zVTzol', '2026-06-15 09:00:00'),
('Chocolate Artesanal', 'Barra de chocolate bitter 70% cacao', 12.00, 30, TRUE, 'https://goo.su/sqwNBKk', '2026-06-15 09:30:00'),
('Vino Tinto (Mini)', 'Botella de vino tinto 187ml', 25.00, 20, TRUE, 'https://goo.su/U044BG', '2026-06-15 10:00:00'),
('Gaseosa Cola 355ml', 'Bebida carbonatada clásica', 6.00, 80, TRUE, 'https://goo.su/endpZf', '2026-06-15 10:30:00'),
('Kit de Costura', 'Agujas, hilos varios y botones', 4.50, 40, FALSE, 'https://goo.su/wPRA7lJ', '2026-06-15 11:00:00'),
('Jabón Artesanal', 'Jabón aromático de lavanda', 7.50, 150, TRUE, 'https://goo.su/GQngW', '2026-06-15 11:30:00'),
('Cerveza Artesanal', 'Cerveza rubia local 330ml', 15.00, 60, TRUE, 'https://acortar.link/00pCwy', '2026-06-15 12:00:00'),
('Té Premium Variado', 'Caja con 5 sobres de té de hierbas', 9.00, 70, TRUE, 'https://acortar.link/KUR6bM', '2026-06-15 12:30:00');




INSERT INTO IMAGEN_HABITACION (url_imagen, idhabitacion)
VALUES
('https://acortar.link/vM4zPm', 1),
('https://acortar.link/7Efz6S', 2),
('https://acortar.link/dyMlXN', 3),
('https://acortar.link/QEkGYe', 4),
('https://acortar.link/u6tBRW', 5),
('https://acortar.link/I6jGNY', 6),
('https://acortar.link/9h1RaI', 7),
('https://acortar.link/AFXmu5', 8),
('https://acortar.link/1rtO9Y', 9),
('https://acortar.link/auhVCk', 10);
