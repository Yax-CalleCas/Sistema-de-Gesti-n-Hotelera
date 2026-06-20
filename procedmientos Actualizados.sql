





CREATE OR REPLACE PROCEDURE sp_RegistrarCategoria(
IN p_Descripcion VARCHAR,
OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
	Resultado := TRUE;

	IF NOT EXISTS (SELECT 1 FROM CATEGORIA WHERE Descripcion = p_Descripcion) THEN
		INSERT INTO CATEGORIA(Descripcion) VALUES (p_Descripcion);
	ELSE
		Resultado := FALSE;
	END IF;
END;
$$;


CREATE OR REPLACE PROCEDURE sp_ModificarCategoria(
IN p_IdCategoria INT,
IN p_Descripcion VARCHAR,
IN p_Estado BOOLEAN,
OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
	Resultado := TRUE;

	IF NOT EXISTS (SELECT 1 FROM CATEGORIA WHERE Descripcion = p_Descripcion AND IdCategoria <> p_IdCategoria) THEN
		UPDATE CATEGORIA
		SET Descripcion = p_Descripcion,
			Estado = p_Estado
		WHERE IdCategoria = p_IdCategoria;
	ELSE
		Resultado := FALSE;
	END IF;
END;
$$;


CREATE OR REPLACE PROCEDURE sp_RegistrarPiso(
IN p_Descripcion VARCHAR,
OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
	Resultado := TRUE;

	IF NOT EXISTS (SELECT 1 FROM PISO WHERE Descripcion = p_Descripcion) THEN
		INSERT INTO PISO(Descripcion) VALUES (p_Descripcion);
	ELSE
		Resultado := FALSE;
	END IF;
END;
$$;


CREATE OR REPLACE PROCEDURE sp_ModificarPiso(
IN p_IdPiso INT,
IN p_Descripcion VARCHAR,
IN p_Estado BOOLEAN,
OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
	Resultado := TRUE;

	IF NOT EXISTS (SELECT 1 FROM PISO WHERE Descripcion = p_Descripcion AND IdPiso <> p_IdPiso) THEN
		UPDATE PISO
		SET Descripcion = p_Descripcion,
			Estado = p_Estado
		WHERE IdPiso = p_IdPiso;
	ELSE
		Resultado := FALSE;
	END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_RegistrarHabitacion(
    IN p_Numero VARCHAR,
    IN p_Detalle VARCHAR,
    IN p_Precio NUMERIC,
    IN p_IdPiso INT,
    IN p_IdCategoria INT,
    OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    Resultado := TRUE;
    IF NOT EXISTS (SELECT 1 FROM HABITACION WHERE numero = p_Numero) THEN
        -- Se agregó la columna 'estado' y su valor TRUE
        INSERT INTO HABITACION(numero, detalle, precio, idpiso, idcategoria, idestadohabitacion, estado)
        VALUES (p_Numero, p_Detalle, p_Precio, p_IdPiso, p_IdCategoria, 1, TRUE);
    ELSE
        Resultado := FALSE;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_ModificarHabitacion(
    IN p_IdHabitacion INT,
    IN p_Numero VARCHAR(50),
    IN p_Detalle VARCHAR(255),
    IN p_Precio NUMERIC(10,2),
    IN p_IdPiso INT,
    IN p_IdCategoria INT,
    IN p_IdEstadoHabitacion INT,
    IN p_Estado BOOLEAN,
    OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    Resultado := TRUE;

    IF NOT EXISTS (SELECT 1 FROM HABITACION WHERE numero = p_Numero AND idhabitacion <> p_IdHabitacion) THEN
        UPDATE HABITACION
        SET numero = p_Numero,
            detalle = p_Detalle,
            precio = p_Precio,
            idpiso = p_IdPiso,
            idcategoria = p_IdCategoria,
            idestadohabitacion = p_IdEstadoHabitacion,
            estado = p_Estado
        WHERE idhabitacion = p_IdHabitacion;
    ELSE
        Resultado := FALSE;
    END IF;
END;
$$;

DROP PROCEDURE IF EXISTS sp_ModificarProducto;

DROP PROCEDURE IF EXISTS sp_RegistrarProducto;


CREATE OR REPLACE PROCEDURE sp_RegistrarProducto(
    IN p_Nombre VARCHAR,
    IN p_Detalle VARCHAR,
    IN p_Precio NUMERIC,
    IN p_Cantidad INT,
    IN p_ImagenUrl VARCHAR, -- Nuevo campo
    OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    Resultado := TRUE;

    IF EXISTS (
        SELECT 1
        FROM PRODUCTO
        WHERE Nombre = p_Nombre
    ) THEN
        Resultado := FALSE;
    ELSE
        INSERT INTO PRODUCTO(
            Nombre,
            Detalle,
            Precio,
            Cantidad,
            Imagen_Url, -- Nombre de la columna en la tabla
            Estado,
            FechaCreacion
        )
        VALUES(
            p_Nombre,
            p_Detalle,
            p_Precio,
            p_Cantidad,
            p_ImagenUrl,
            TRUE,
            CURRENT_TIMESTAMP
        );
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_ModificarProducto(
    IN p_IdProducto INT,
    IN p_Nombre VARCHAR,
    IN p_Detalle VARCHAR,
    IN p_Precio NUMERIC,
    IN p_Cantidad INT,
    IN p_Estado BOOLEAN,
    IN p_ImagenUrl VARCHAR, -- Nuevo campo
    OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    Resultado := TRUE;

    IF EXISTS (
        SELECT 1
        FROM PRODUCTO
        WHERE Nombre = p_Nombre
          AND IdProducto <> p_IdProducto
    ) THEN
        Resultado := FALSE;
    ELSE
        UPDATE PRODUCTO
        SET Nombre = p_Nombre,
            Detalle = p_Detalle,
            Precio = p_Precio,
            Cantidad = p_Cantidad,
            Estado = p_Estado,
            Imagen_Url = p_ImagenUrl -- Actualización del campo
        WHERE IdProducto = p_IdProducto;
    END IF;
END;
$$;


CREATE OR REPLACE FUNCTION sp_RegistrarPersona(
    p_TipoDocumento VARCHAR, p_Documento VARCHAR, p_Nombre VARCHAR,
    p_Apellido VARCHAR, p_Correo VARCHAR, p_Clave VARCHAR, p_IdTipoPersona INT
) RETURNS BOOLEAN AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM PERSONA WHERE Documento = p_Documento) THEN
        -- Incluimos fechaCreacion usando NOW()
        INSERT INTO PERSONA(TipoDocumento, Documento, Nombre, Apellido, Correo, Clave, IdTipoPersona, Estado, fechaCreacion)
        VALUES (p_TipoDocumento, p_Documento, p_Nombre, p_Apellido, p_Correo, p_Clave, p_IdTipoPersona, TRUE, NOW());
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION sp_ModificarPersona(
    p_IdPersona INT, 
    p_TipoDocumento VARCHAR, 
    p_Documento VARCHAR, 
    p_Nombre VARCHAR, 
    p_Apellido VARCHAR, 
    p_Correo VARCHAR, 
    p_Clave VARCHAR, 
    p_IdTipoPersona INT, 
    p_Estado BOOLEAN,
    p_FotoUrl VARCHAR
) RETURNS BOOLEAN AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM PERSONA WHERE Documento = p_Documento AND IdPersona <> p_IdPersona) THEN
        UPDATE PERSONA
        SET TipoDocumento = p_TipoDocumento, 
            Documento = p_Documento, 
            Nombre = p_Nombre, 
            Apellido = p_Apellido, 
            Correo = p_Correo, 
            Clave = p_Clave, 
            IdTipoPersona = p_IdTipoPersona, 
            Estado = p_Estado,
            foto_url = p_FotoUrl -- Actualización unificada
        WHERE IdPersona = p_IdPersona;
        
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE sp_RegistrarRecepcion(
    IN p_IdCliente INT,
    IN p_TipoDocumento VARCHAR,
    IN p_Documento VARCHAR,
    IN p_Nombre VARCHAR,
    IN p_Apellido VARCHAR,
    IN p_Correo VARCHAR,
    IN p_IdHabitacion INT,
    IN p_FechaSalida DATE,
    IN p_PrecioInicial NUMERIC,
    IN p_Adelanto NUMERIC,
    IN p_PrecioRestante NUMERIC,
    IN p_Observacion VARCHAR,
    OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
DECLARE v_IdCliente INT;
BEGIN
    Resultado := TRUE;
    v_IdCliente := p_IdCliente;
    
    -- 1. ASEGURAR QUE NO HAYA OTRA RECEPCIÓN ACTIVA EN LA MISMA HABITACIÓN
    UPDATE RECEPCION 
    SET Estado = FALSE 
    WHERE IdHabitacion = p_IdHabitacion AND Estado = TRUE;

    -- 2. Lógica de cliente
    IF v_IdCliente IS NULL OR v_IdCliente = 0 OR NOT EXISTS (SELECT 1 FROM PERSONA WHERE IdPersona = v_IdCliente) THEN
        INSERT INTO PERSONA(TipoDocumento, Documento, Nombre, Apellido, Correo, IdTipoPersona, Estado)
        VALUES (p_TipoDocumento, p_Documento, p_Nombre, p_Apellido, p_Correo, 3, TRUE)
        RETURNING IdPersona INTO v_IdCliente;
    END IF;

    -- 3. Actualizar estado de la habitación
    UPDATE HABITACION SET IdEstadoHabitacion = 2 WHERE IdHabitacion = p_IdHabitacion;

    -- 4. Insertar la nueva recepción
    INSERT INTO RECEPCION(IdCliente, IdHabitacion, FechaEntrada, FechaSalida, PrecioInicial, Adelanto, PrecioRestante, Observacion, Estado)
    VALUES (v_IdCliente, p_IdHabitacion, CURRENT_DATE, p_FechaSalida, p_PrecioInicial, p_Adelanto, p_PrecioRestante, p_Observacion, TRUE);
    
EXCEPTION WHEN OTHERS THEN
    Resultado := FALSE;
END;
$$;


CREATE OR REPLACE PROCEDURE sp_RegistrarSalida(
    IN p_IdRecepcion INT,
    IN p_IdHabitacion INT,
    IN p_CostoPenalidad NUMERIC,
    IN p_TotalPagado NUMERIC,
    OUT p_Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- 1. Verificar existencia
    IF NOT EXISTS (SELECT 1 FROM RECEPCION WHERE IdRecepcion = p_IdRecepcion) THEN
        p_Resultado := FALSE;
        RETURN;
    END IF;

    -- 2. Actualizar recepción
    UPDATE RECEPCION
    SET Estado = FALSE,
        FechaSalidaConfirmacion = CURRENT_TIMESTAMP,
        TotalPagado = p_TotalPagado,
        CostoPenalidad = p_CostoPenalidad
    WHERE IdRecepcion = p_IdRecepcion;

    -- 3. Marcar consumos
    UPDATE VENTA 
    SET Estado = 'PAGADO' 
    WHERE IdRecepcion = p_IdRecepcion AND Estado != 'PAGADO';

    -- 4. Cambiar habitación a estado 3 (Limpieza)
    UPDATE HABITACION
    SET IdEstadoHabitacion = 3
    WHERE IdHabitacion = p_IdHabitacion;

    p_Resultado := TRUE;
    -- COMMIT y ROLLBACK se eliminan porque Spring Boot los controla mediante @Transactional
EXCEPTION WHEN OTHERS THEN
    p_Resultado := FALSE;
    RAISE; -- Esto relanza el error para que Spring Boot vea que falló y ejecute su propio ROLLBACK automáticamente
END;
$$;

DROP FUNCTION sp_RegistrarVenta(INT, VARCHAR, TEXT);
CREATE OR REPLACE FUNCTION sp_RegistrarVenta(
    p_IdRecepcion INT,
    p_Estado VARCHAR,
    p_Detalles TEXT
)
RETURNS INT -- CAMBIO: De BOOLEAN a INT
LANGUAGE plpgsql
AS $$
DECLARE
    v_IdVenta INT;
    v_TotalVenta NUMERIC(10,2) := 0.00;
    v_Detalle RECORD;
    v_JsonData JSON;
BEGIN
    v_JsonData := p_Detalles::JSON;

    -- 1. Validar existencia
    IF NOT EXISTS (SELECT 1 FROM RECEPCION WHERE IdRecepcion = p_IdRecepcion) THEN
        RAISE EXCEPTION 'La recepción con ID % no existe.', p_IdRecepcion;
    END IF;

    -- 2. Insertar cabecera
    INSERT INTO VENTA (IdRecepcion, Total, Estado, FechaCreacion)
    VALUES (p_IdRecepcion, 0.00, p_Estado, CURRENT_TIMESTAMP)
    RETURNING IdVenta INTO v_IdVenta;

    -- 3. Procesar detalles
    FOR v_Detalle IN 
        SELECT 
            (elem->>'idProducto')::INT AS id_prod,
            (elem->>'cantidad')::INT AS cant,
            (elem->>'precioUnitario')::NUMERIC(10,2) AS precio
        FROM json_array_elements(v_JsonData) AS elem
    LOOP
        IF (SELECT Cantidad FROM PRODUCTO WHERE IdProducto = v_Detalle.id_prod) < v_Detalle.cant THEN
            RAISE EXCEPTION 'Stock insuficiente para el producto ID %', v_Detalle.id_prod;
        END IF;

        INSERT INTO DETALLE_VENTA (IdVenta, IdProducto, Cantidad, preciounitario, SubTotal)
        VALUES (v_IdVenta, v_Detalle.id_prod, v_Detalle.cant, v_Detalle.precio, (v_Detalle.cant * v_Detalle.precio));

        UPDATE PRODUCTO 
        SET Cantidad = Cantidad - v_Detalle.cant 
        WHERE IdProducto = v_Detalle.id_prod;
        
        v_TotalVenta := v_TotalVenta + (v_Detalle.cant * v_Detalle.precio);
    END LOOP;

    -- 4. Finalizar
    UPDATE VENTA SET Total = v_TotalVenta WHERE IdVenta = v_IdVenta;

    RETURN v_IdVenta; -- CAMBIO: Retornamos el ID, no un booleano
EXCEPTION WHEN OTHERS THEN
    RAISE;
END;
$$;



-- reportes del sistema ----------------------------------

-- borrar datos
DROP FUNCTION IF EXISTS fn_Reporte_ProductosBajoStock(INT);
DROP FUNCTION IF EXISTS fn_Reporte_Ventas(TIMESTAMP, TIMESTAMP);
DROP FUNCTION IF EXISTS fn_Reporte_Ocupacion(TIMESTAMP, TIMESTAMP);
DROP FUNCTION IF EXISTS fn_Reporte_Cobros(TIMESTAMP, TIMESTAMP);

CREATE OR REPLACE FUNCTION fn_Reporte_Cobros_Consolidado(p_inicio TIMESTAMP, p_fin TIMESTAMP)
RETURNS TABLE(
    numero_habitacion TEXT, 
    nombre_cliente TEXT, 
    total_alojamiento NUMERIC, 
    total_consumos NUMERIC, 
    total_general NUMERIC,
    fecha_cierre TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        h.numero::TEXT, 
        (p.nombre || ' ' || p.apellido)::TEXT, 
        r.totalpagado AS total_alojamiento,
        COALESCE((SELECT SUM(v.total) FROM VENTA v WHERE v.idrecepcion = r.idrecepcion AND v.estado = 'PAGADO'), 0) AS total_consumos,
        (r.totalpagado + COALESCE((SELECT SUM(v.total) FROM VENTA v WHERE v.idrecepcion = r.idrecepcion AND v.estado = 'PAGADO'), 0)) AS total_general,
        r.fechaSalidaConfirmacion
    FROM RECEPCION r
    JOIN HABITACION h ON r.idhabitacion = h.idhabitacion
    JOIN PERSONA p ON r.idcliente = p.idpersona 
    WHERE p.idtipopersona = 3 
    AND r.fechaSalidaConfirmacion BETWEEN p_inicio AND p_fin;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fn_Reporte_Ventas_Validadas(p_inicio TIMESTAMP, p_fin TIMESTAMP)
RETURNS TABLE(nombre_producto TEXT, cantidad_total BIGINT, total_ingresado NUMERIC) AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        p.nombre::TEXT, 
        SUM(dv.cantidad)::BIGINT, 
        SUM(dv.subtotal)::NUMERIC
    FROM VENTA v
    JOIN DETALLE_VENTA dv ON v.idventa = dv.idventa
    JOIN PRODUCTO p ON dv.idproducto = p.idproducto
    WHERE v.fechacreacion BETWEEN p_inicio AND p_fin
    AND v.estado = 'PAGADO' -- Regla crítica para no inflar recaudación
    GROUP BY p.nombre;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION fn_Reporte_Ocupacion_Efectiva(p_inicio TIMESTAMP, p_fin TIMESTAMP)
RETURNS TABLE(num_habitacion TEXT, desc_categoria TEXT, veces_alquilada BIGINT) AS $$
BEGIN
    RETURN QUERY 
    SELECT h.numero::TEXT, c.descripcion::TEXT, COUNT(r.idrecepcion)::BIGINT
    FROM RECEPCION r
    JOIN HABITACION h ON r.idhabitacion = h.idhabitacion
    JOIN CATEGORIA c ON h.idcategoria = c.idcategoria
    WHERE r.fechaSalidaConfirmacion BETWEEN p_inicio AND p_fin -- Filtro por salida efectiva
    GROUP BY h.numero, c.descripcion;
END;
$$ LANGUAGE plpgsql;



-- 4. Dashboard Estadísticas (Retorna una fila única)
CREATE OR REPLACE FUNCTION fn_Dashboard_Estadisticas()
RETURNS TABLE(ocupadas BIGINT, disponibles BIGINT, bajo_stock BIGINT, ingresos_hoy NUMERIC) AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        (SELECT COUNT(*) FROM HABITACION WHERE idestadohabitacion = 2),
        (SELECT COUNT(*) FROM HABITACION WHERE idestadohabitacion = 1),
        (SELECT COUNT(*) FROM PRODUCTO WHERE cantidad <= 10),
        -- Suma de ventas + Suma de pagos de habitación realizados hoy
        (SELECT COALESCE(SUM(total), 0) + (SELECT COALESCE(SUM(totalpagado), 0) 
         FROM RECEPCION WHERE fechasalidaconfirmacion::date = CURRENT_DATE) 
         FROM VENTA WHERE fechacreacion::date = CURRENT_DATE);
END;
$$ LANGUAGE plpgsql;



select * from venta;





-- Para el reporte de Ventas (filtros por fecha y joins)
CREATE INDEX idx_venta_fecha ON VENTA(fechacreacion);
CREATE INDEX idx_detalle_venta_idventa ON DETALLE_VENTA(idventa);
CREATE INDEX idx_detalle_venta_idproducto ON DETALLE_VENTA(idproducto);

-- Para el reporte de Ocupación
CREATE INDEX idx_recepcion_fecha_entrada ON RECEPCION(fechaentrada);
CREATE INDEX idx_recepcion_idhabitacion ON RECEPCION(idhabitacion);

-- Para el reporte de Cobros
CREATE INDEX idx_recepcion_fecha_salida ON RECEPCION(fechaSalidaConfirmacion);
CREATE INDEX idx_persona_idtipopersona ON PERSONA(idtipopersona);

-- Para productos bajo stock
CREATE INDEX idx_producto_cantidad ON PRODUCTO(cantidad);



SELECT idrecepcion, precioinicial, adelanto, preciorestante 
FROM RECEPCION 
WHERE idhabitacion = (SELECT idhabitacion FROM HABITACION WHERE numero = '43');



select * from persona;



