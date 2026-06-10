package com.hotel.cibertec.repository;

import com.hotel.cibertec.entity.Piso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PisoRepository extends JpaRepository<Piso, Integer> {
    // Solo métodos estándar de JpaRepository
}