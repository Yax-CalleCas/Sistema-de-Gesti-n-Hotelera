package com.hotel.cibertec.service;

import com.hotel.cibertec.dto.PisoDto;
import java.util.List;

public interface PisoService {
    List<PisoDto> listarTodos();
    PisoDto buscarPorId(Integer id);
    PisoDto guardar(PisoDto dto);
    PisoDto actualizar(Integer id, PisoDto dto);
    void eliminar(Integer id);
}