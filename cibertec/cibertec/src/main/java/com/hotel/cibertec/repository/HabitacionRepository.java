package com.hotel.cibertec.repository;

import com.hotel.cibertec.dto.ReporteHabitacionDto;
import com.hotel.cibertec.entity.Habitacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    // Usar Optional es mejor práctica que retornar null si no existe
    Optional<Habitacion> findByNumero(String numero);

    Optional<Habitacion> findByNumeroAndEstadoTrue(String numero);

}