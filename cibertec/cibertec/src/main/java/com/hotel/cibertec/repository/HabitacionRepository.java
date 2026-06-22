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

    Optional<Habitacion> findByNumero(String numero);



    @Query("SELECT h FROM Habitacion h " +
            "LEFT JOIN FETCH h.estadoHabitacion " +
            "LEFT JOIN FETCH h.piso " +
            "LEFT JOIN FETCH h.categoria " +
            "LEFT JOIN FETCH h.imagenes " +
            "WHERE h.idHabitacion = :id " +
            "AND h.estado = true")
    Optional<Habitacion> findByIdWithDetails(@Param("id") Integer id);


    @Query("SELECT h FROM Habitacion h " +
            "LEFT JOIN FETCH h.estadoHabitacion " +
            "LEFT JOIN FETCH h.piso " +
            "LEFT JOIN FETCH h.categoria " +
            "LEFT JOIN FETCH h.imagenes " +
            "WHERE h.estado = true")
    List<Habitacion> findAllWithDetails();
}