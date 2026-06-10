package com.hotel.cibertec.service;

import com.hotel.cibertec.dto.PersonaDto;
import java.util.List;

public interface PersonaService {
    List<PersonaDto> listarTodos();
    PersonaDto buscarPorId(Integer id);
    PersonaDto guardar(PersonaDto dto);
    PersonaDto actualizar(Integer id, PersonaDto dto);
    void eliminar(Integer id);
}
