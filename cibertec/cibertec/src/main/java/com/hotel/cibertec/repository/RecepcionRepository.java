package com.hotel.cibertec.repository;

import com.hotel.cibertec.dto.ReporteCobroDto;
import com.hotel.cibertec.entity.Recepcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecepcionRepository extends JpaRepository<Recepcion, Integer> {
    java.util.Optional<Recepcion> findFirstByHabitacionIdHabitacionAndEstado(Integer idHabitacion, Boolean estado);


}