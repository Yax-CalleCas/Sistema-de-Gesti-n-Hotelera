package com.hotel.cibertec.repository;

import com.hotel.cibertec.dto.ReporteProductoDto;
import com.hotel.cibertec.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {



}