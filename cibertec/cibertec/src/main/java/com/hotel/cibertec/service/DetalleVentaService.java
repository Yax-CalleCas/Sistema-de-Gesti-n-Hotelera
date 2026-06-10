package com.hotel.cibertec.service;

import com.hotel.cibertec.dto.DetalleVentaDto;	
import java.util.List;

public interface DetalleVentaService {
    List<DetalleVentaDto> listarTodos();
    DetalleVentaDto buscarPorId(Integer id);
    DetalleVentaDto guardar(DetalleVentaDto dto);
    DetalleVentaDto actualizar(Integer id, DetalleVentaDto dto);
    void eliminar(Integer id);
}