package com.hotel.cibertec.repository;

import com.hotel.cibertec.entity.Recepcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecepcionRepository extends JpaRepository<Recepcion, Integer> {
    java.util.Optional<Recepcion> findFirstByHabitacionIdHabitacionAndEstado(Integer idHabitacion, Boolean estado);
}