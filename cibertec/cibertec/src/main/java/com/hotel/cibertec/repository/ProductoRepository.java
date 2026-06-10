package com.hotel.cibertec.repository;

import com.hotel.cibertec.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}