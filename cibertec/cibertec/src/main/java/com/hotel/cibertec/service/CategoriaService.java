package com.hotel.cibertec.service;


import com.hotel.cibertec.dto.CategoriaDto;
import java.util.List;

public interface CategoriaService {
    List<CategoriaDto> listarTodos();
    CategoriaDto buscarPorId(Integer id);
    CategoriaDto guardar(CategoriaDto dto);
    CategoriaDto actualizar(Integer id, CategoriaDto dto);
    void eliminar(Integer id);
}