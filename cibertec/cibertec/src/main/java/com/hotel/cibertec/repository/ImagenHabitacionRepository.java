package com.hotel.cibertec.repository;

import com.hotel.cibertec.entity.ImagenHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImagenHabitacionRepository extends JpaRepository<ImagenHabitacion, Integer> {

    // Listar es un SELECT, no requiere @Modifying
    List<ImagenHabitacion> findByHabitacion_IdHabitacion(Integer idHabitacion);

    // IMPORTANTE: Al borrar por ID de FK, debemos indicar que es una operación de modificación
    @Modifying
    @Query("DELETE FROM ImagenHabitacion i WHERE i.habitacion.idHabitacion = :idHabitacion")
    void deleteByHabitacion_IdHabitacion(@Param("idHabitacion") Integer idHabitacion);
}