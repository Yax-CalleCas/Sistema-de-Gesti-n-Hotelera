package com.hotel.cibertec.service;

import com.hotel.cibertec.dto.TipoPersonaDto;
import java.util.List;

public interface TipoPersonaService {
    List<TipoPersonaDto> listarTodos();
    TipoPersonaDto buscarPorId(Integer id);
    TipoPersonaDto guardar(TipoPersonaDto dto);
    TipoPersonaDto actualizar(Integer id, TipoPersonaDto dto);
    void eliminar(Integer id);
}