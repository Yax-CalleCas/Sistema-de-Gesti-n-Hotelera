package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.TipoPersonaDto;
import com.hotel.cibertec.entity.TipoPersona;
import com.hotel.cibertec.repository.TipoPersonaRepository;
import com.hotel.cibertec.service.TipoPersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoPersonaServiceImpl implements TipoPersonaService {

    private final TipoPersonaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoPersonaDto> listarTodos() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TipoPersonaDto buscarPorId(Integer id) {
        return repository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }

    @Override
    @Transactional
    public TipoPersonaDto guardar(TipoPersonaDto dto) {
        return toDto(repository.save(toEntity(dto)));
    }

    @Override
    @Transactional
    public TipoPersonaDto actualizar(Integer id, TipoPersonaDto dto) {
        return repository.findById(id).map(e -> {
            e.setDescripcion(dto.getDescripcion());
            e.setEstado(dto.getEstado());
            return toDto(repository.save(e));
        }).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        TipoPersona t = repository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        t.setEstado(false);
        repository.save(t);
    }

    private TipoPersonaDto toDto(TipoPersona e) {
        return TipoPersonaDto.builder()
                .idTipoPersona(e.getIdTipoPersona())
                .descripcion(e.getDescripcion())
                .estado(e.getEstado())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    private TipoPersona toEntity(TipoPersonaDto d) {
        return TipoPersona.builder()
                .idTipoPersona(d.getIdTipoPersona())
                .descripcion(d.getDescripcion())
                .estado(d.getEstado())
                .fechaCreacion(d.getFechaCreacion())
                .build();
    }
}