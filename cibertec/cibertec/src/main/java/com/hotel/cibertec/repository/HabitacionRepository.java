package com.hotel.cibertec.repository;

import com.hotel.cibertec.entity.Habitacion;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

}