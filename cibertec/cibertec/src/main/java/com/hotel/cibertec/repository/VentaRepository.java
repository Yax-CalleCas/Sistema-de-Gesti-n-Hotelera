package com.hotel.cibertec.repository;

import com.hotel.cibertec.dto.ReporteVentaDto;
import com.hotel.cibertec.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    @Procedure(name = "sp_RegistrarVenta")
    Boolean registrarVenta(@Param("p_IdRecepcion") Integer idRecepcion,
                           @Param("p_Estado") String estado,
                           @Param("p_Detalles") String detalles);

    // Cambia 'id_recepcion' por 'idrecepcion' tal como te lo sugiere la base de datos
    @Query(value = "SELECT * FROM venta WHERE idrecepcion = :idRecepcion", nativeQuery = true)
    List<Venta> findByRecepcionId(@Param("idRecepcion") Integer idRecepcion);


}
