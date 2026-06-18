package com.hotel.cibertec.repository;

import com.hotel.cibertec.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    // Esto evita que JPA intente inyectar nombres de parámetros (=>),
    @Query(value = "SELECT * FROM sp_RegistrarVenta(:p_IdRecepcion, :p_Estado, :p_Detalles)", nativeQuery = true)
    Boolean registrarVenta(
            @Param("p_IdRecepcion") Integer p_IdRecepcion,
            @Param("p_Estado") String p_Estado,
            @Param("p_Detalles") String p_Detalles
    );

    @Query("SELECT DISTINCT v FROM Venta v LEFT JOIN FETCH v.detalleVentas WHERE v.recepcion.idRecepcion = :idRecepcion")
    List<Venta> findByRecepcionId(@Param("idRecepcion") Integer idRecepcion);
}