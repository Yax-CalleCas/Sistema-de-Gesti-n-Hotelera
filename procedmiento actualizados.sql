




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
        INSERT INTO HABITACION(numero, detalle, precio, idpiso, idcategoria, idestadohabitacion)
        VALUES (p_Numero, p_Detalle, p_Precio, p_IdPiso, p_IdCategoria, 1);
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


CREATE OR REPLACE PROCEDURE sp_RegistrarPersona(
IN p_TipoDocumento VARCHAR,
IN p_Documento VARCHAR,
IN p_Nombre VARCHAR,
IN p_Apellido VARCHAR,
IN p_Correo VARCHAR,
IN p_Clave VARCHAR,
IN p_IdTipoPersona INT,
OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
	Resultado := TRUE;

	IF NOT EXISTS (SELECT 1 FROM PERSONA WHERE Documento = p_Documento) THEN
		--  Añadimos la columna 'Estado' y le pasamos 'TRUE' por defecto en el VALUES
		INSERT INTO PERSONA(TipoDocumento, Documento, Nombre, Apellido, Correo, Clave, IdTipoPersona, Estado)
		VALUES (p_TipoDocumento, p_Documento, p_Nombre, p_Apellido, p_Correo, p_Clave, p_IdTipoPersona, TRUE);
	ELSE
		Resultado := FALSE;
	END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_ModificarPersona(
IN p_IdPersona INT,
IN p_TipoDocumento VARCHAR,
IN p_Documento VARCHAR,
IN p_Nombre VARCHAR,
IN p_Apellido VARCHAR,
IN p_Correo VARCHAR,
IN p_Clave VARCHAR,
IN p_IdTipoPersona INT,
IN p_Estado BOOLEAN,
OUT Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
	Resultado := TRUE;

	IF NOT EXISTS (SELECT 1 FROM PERSONA WHERE Documento = p_Documento AND IdPersona <> p_IdPersona) THEN
		UPDATE PERSONA
		SET TipoDocumento = p_TipoDocumento,
			Documento = p_Documento,
			Nombre = p_Nombre,
			Apellido = p_Apellido,
			Correo = p_Correo,
			Clave = p_Clave,
			IdTipoPersona = p_IdTipoPersona,
			Estado = p_Estado
		WHERE IdPersona = p_IdPersona;
	ELSE
		Resultado := FALSE;
	END IF;
END;
$$;

CREATE PROCEDURE sp_RegistrarRecepcion(
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
	
    IF v_IdCliente IS NULL OR v_IdCliente = 0 OR NOT EXISTS (SELECT 1 FROM PERSONA WHERE IdPersona = v_IdCliente) THEN
        INSERT INTO PERSONA(TipoDocumento, Documento, Nombre, Apellido, Correo, IdTipoPersona, Estado)
        VALUES (p_TipoDocumento, p_Documento, p_Nombre, p_Apellido, p_Correo, 3, TRUE)
        RETURNING IdPersona INTO v_IdCliente;
    END IF;

    UPDATE HABITACION SET IdEstadoHabitacion = 2 WHERE IdHabitacion = p_IdHabitacion;

    INSERT INTO RECEPCION(IdCliente, IdHabitacion, FechaEntrada, FechaSalida, PrecioInicial, Adelanto, PrecioRestante, Observacion, Estado)
    VALUES (v_IdCliente, p_IdHabitacion, CURRENT_DATE, p_FechaSalida, p_PrecioInicial, p_Adelanto, p_PrecioRestante, p_Observacion, TRUE);
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
    -- 1. Actualizar la recepción
    UPDATE RECEPCION
    SET Estado = FALSE,
        FechaSalidaConfirmacion = CURRENT_TIMESTAMP,
        TotalPagado = p_TotalPagado,
        CostoPenalidad = p_CostoPenalidad
    WHERE IdRecepcion = p_IdRecepcion;

    -- 2. Marcar consumos (ventas) como 'PAGADO' al cerrar la recepción
    UPDATE VENTA 
    SET Estado = 'PAGADO' 
    WHERE IdRecepcion = p_IdRecepcion AND Estado != 'PAGADO';

    -- 3. Cambiar habitación a estado 3 (Limpieza/Mantenimiento)
    UPDATE HABITACION
    SET IdEstadoHabitacion = 3
    WHERE IdHabitacion = p_IdHabitacion;

    p_Resultado := TRUE;
EXCEPTION WHEN OTHERS THEN
    p_Resultado := FALSE;
    RAISE NOTICE 'Error en sp_RegistrarSalida: %', SQLERRM;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_RegistrarVenta(
    IN p_IdRecepcion INT,
    IN p_Estado VARCHAR,
    IN p_Detalles TEXT, 
    OUT p_Resultado BOOLEAN
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_IdVenta INT;
    v_TotalVenta NUMERIC(10,2) := 0.00;
    v_Detalle RECORD;
    v_JsonData JSON;
BEGIN
    -- Conversión explícita del parámetro de texto a JSON
    v_JsonData := p_Detalles::JSON;

    -- 1. Validar existencia de la recepción
    IF NOT EXISTS (SELECT 1 FROM RECEPCION WHERE IdRecepcion = p_IdRecepcion) THEN
        RAISE EXCEPTION 'La recepción con ID % no existe.', p_IdRecepcion;
    END IF;

    -- 2. Insertar cabecera de la venta
    INSERT INTO VENTA (IdRecepcion, Total, Estado, FechaCreacion)
    VALUES (p_IdRecepcion, 0.00, COALESCE(p_Estado, 'PAGADO'), CURRENT_TIMESTAMP)
    RETURNING IdVenta INTO v_IdVenta;

    -- 3. Procesamiento de detalles
    FOR v_Detalle IN 
        SELECT 
            (elem->>'idProducto')::INT AS id_prod,
            (elem->>'cantidad')::INT AS cant,
            (elem->>'precioUnitario')::NUMERIC(10,2) AS precio
        FROM json_array_elements(v_JsonData) AS elem
    LOOP
        -- Validar stock disponible
        IF (SELECT Cantidad FROM PRODUCTO WHERE IdProducto = v_Detalle.id_prod) < v_Detalle.cant THEN
            RAISE EXCEPTION 'Stock insuficiente para el producto ID %', v_Detalle.id_prod;
        END IF;

        -- 4. Insertar detalle
        INSERT INTO DETALLE_VENTA (IdVenta, IdProducto, Cantidad, preciounitario, SubTotal)
        VALUES (v_IdVenta, v_Detalle.id_prod, v_Detalle.cant, v_Detalle.precio, (v_Detalle.cant * v_Detalle.precio));

        -- 5. Actualizar stock del producto
        UPDATE PRODUCTO 
        SET Cantidad = Cantidad - v_Detalle.cant 
        WHERE IdProducto = v_Detalle.id_prod;
        
        v_TotalVenta := v_TotalVenta + (v_Detalle.cant * v_Detalle.precio);
    END LOOP;

    -- 6. Actualizar total final
    UPDATE VENTA SET Total = v_TotalVenta WHERE IdVenta = v_IdVenta;

    p_Resultado := TRUE;
EXCEPTION WHEN OTHERS THEN
    -- El ROLLBACK es automático en procedimientos de Postgres ante excepciones
    RAISE;
END;
$$;


-- reportes del sistema ----------------------------------

-- Ejecuta esto antes de recrear cada función
DROP FUNCTION IF EXISTS fn_Reporte_ProductosBajoStock(INT);
DROP FUNCTION IF EXISTS fn_Reporte_Ventas(TIMESTAMP, TIMESTAMP);
DROP FUNCTION IF EXISTS fn_Reporte_Ocupacion(TIMESTAMP, TIMESTAMP);
DROP FUNCTION IF EXISTS fn_Reporte_Cobros(TIMESTAMP, TIMESTAMP);

-------------------------------------------
-- 1. Reporte de Productos Bajo Stock
CREATE OR REPLACE FUNCTION fn_Reporte_ProductosBajoStock(p_limite INT)
RETURNS TABLE(id_producto INT, nombre_prod TEXT, cant INT, prec NUMERIC, est BOOLEAN) AS $$
BEGIN
    RETURN QUERY 
    SELECT p.idproducto, p.nombre::TEXT, p.cantidad, p.precio, p.estado 
    FROM PRODUCTO p 
    WHERE p.cantidad <= p_limite;
END;
$$ LANGUAGE plpgsql;

-- 2. Reporte de Ventas
CREATE OR REPLACE FUNCTION fn_Reporte_Ventas(p_inicio TIMESTAMP, p_fin TIMESTAMP)
RETURNS TABLE(nombre_producto TEXT, cantidad_total BIGINT, total_ingresado NUMERIC) AS $$
BEGIN
    RETURN QUERY 
    SELECT p.nombre::TEXT, SUM(dv.cantidad)::BIGINT, SUM(dv.subtotal)::NUMERIC
    FROM VENTA v
    JOIN DETALLE_VENTA dv ON v.idventa = dv.idventa
    JOIN PRODUCTO p ON dv.idproducto = p.idproducto
    WHERE v.fechacreacion BETWEEN p_inicio AND p_fin
    GROUP BY p.nombre;
END;
$$ LANGUAGE plpgsql;

-- 3. Reporte de Ocupación
CREATE OR REPLACE FUNCTION fn_Reporte_Ocupacion(p_inicio TIMESTAMP, p_fin TIMESTAMP)
RETURNS TABLE(num_habitacion TEXT, desc_categoria TEXT, veces_alquilada BIGINT) AS $$
BEGIN
    RETURN QUERY 
    SELECT h.numero::TEXT, c.descripcion::TEXT, COUNT(r.idrecepcion)::BIGINT
    FROM RECEPCION r
    JOIN HABITACION h ON r.idhabitacion = h.idhabitacion
    JOIN CATEGORIA c ON h.idcategoria = c.idcategoria
    WHERE r.fechaentrada BETWEEN p_inicio AND p_fin
    GROUP BY h.numero, c.descripcion;
END;
$$ LANGUAGE plpgsql;

-- 4. Reporte de Cobros
-- Asegúrate de que la tabla sea "CLIENTE" (o "TB_CLIENTE" si así se llama en tu DB)

CREATE OR REPLACE FUNCTION fn_Reporte_Cobros(p_inicio TIMESTAMP, p_fin TIMESTAMP)
RETURNS TABLE(numero_habitacion TEXT, nombre_cliente TEXT, total_pagado NUMERIC, fecha_pago TIMESTAMP) AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        h.numero::TEXT, 
        (p.nombre || ' ' || p.apellido)::TEXT, 
        r.totalpagado, 
        r.fechaSalidaConfirmacion
    FROM RECEPCION r
    JOIN HABITACION h ON r.idhabitacion = h.idhabitacion
    -- Cambiamos el JOIN: ahora unimos con PERSONA y filtramos por idtipopersona = 3
    JOIN PERSONA p ON r.idcliente = p.idpersona 
    WHERE p.idtipopersona = 3 
    AND r.fechaSalidaConfirmacion BETWEEN p_inicio AND p_fin;
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






