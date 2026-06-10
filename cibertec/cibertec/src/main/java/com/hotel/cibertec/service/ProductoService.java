package com.hotel.cibertec.service;

import com.hotel.cibertec.dto.ProductoDto;
import java.util.List;

public interface ProductoService {
    List<ProductoDto> listarTodos();
    ProductoDto buscarPorId(Integer id);
    ProductoDto guardar(ProductoDto dto);
    ProductoDto actualizar(Integer id, ProductoDto dto);
    void eliminar(Integer id);
}