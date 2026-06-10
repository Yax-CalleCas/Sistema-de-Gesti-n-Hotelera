package com.hotel.cibertec.service;

import com.hotel.cibertec.dto.HabitacionDto;
import java.util.List;

public interface HabitacionService {
    List<HabitacionDto> listarTodos();
    HabitacionDto buscarPorId(Integer id);
    HabitacionDto guardar(HabitacionDto dto);
    HabitacionDto actualizar(Integer id, HabitacionDto dto);
    void eliminar(Integer id);
}