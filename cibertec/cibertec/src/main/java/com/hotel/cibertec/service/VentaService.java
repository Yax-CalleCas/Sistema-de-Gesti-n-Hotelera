package com.hotel.cibertec.service;
import com.hotel.cibertec.dto.VentaDto;
import java.util.List;

public interface VentaService {
    List<VentaDto> listarTodos();
    VentaDto buscarPorId(Integer id);
    VentaDto guardar(VentaDto dto);
    VentaDto actualizar(Integer id, VentaDto dto);
    void eliminar(Integer id);
    List<VentaDto> listarPorRecepcion(Integer idRecepcion);
}