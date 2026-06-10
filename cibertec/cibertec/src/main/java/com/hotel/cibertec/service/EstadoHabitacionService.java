package com.hotel.cibertec.service;
import com.hotel.cibertec.dto.EstadoHabitacionDto;
import java.util.List;

public interface EstadoHabitacionService {
    List<EstadoHabitacionDto> listarTodos();
    EstadoHabitacionDto buscarPorId(Integer id);
    EstadoHabitacionDto guardar(EstadoHabitacionDto dto);
    EstadoHabitacionDto actualizar(Integer id, EstadoHabitacionDto dto);
    void eliminar(Integer id);
}