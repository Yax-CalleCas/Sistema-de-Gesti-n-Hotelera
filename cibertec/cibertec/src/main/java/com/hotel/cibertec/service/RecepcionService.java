package com.hotel.cibertec.service;

import com.hotel.cibertec.dto.RecepcionDto;
import java.math.BigDecimal;
import java.util.List;

public interface RecepcionService {
    List<RecepcionDto> listarTodos();
    RecepcionDto buscarPorId(Integer id);
    RecepcionDto buscarActivaPorHabitacion(Integer idHabitacion);

    RecepcionDto guardar(RecepcionDto dto);
    RecepcionDto actualizar(Integer id, RecepcionDto dto);
    void eliminar(Integer id);

    // 🚀 AGREGADO: Método para procesar la salida (Check-out)
    Boolean procesarSalida(Integer idRecepcion, Integer idHabitacion, BigDecimal penalidad, BigDecimal total);
}