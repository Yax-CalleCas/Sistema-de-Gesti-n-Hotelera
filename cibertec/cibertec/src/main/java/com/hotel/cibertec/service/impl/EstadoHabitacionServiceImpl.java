package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.EstadoHabitacionDto;
import com.hotel.cibertec.entity.EstadoHabitacion;
import com.hotel.cibertec.repository.EstadoHabitacionRepository;
import com.hotel.cibertec.service.EstadoHabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoHabitacionServiceImpl implements EstadoHabitacionService {

    private final EstadoHabitacionRepository estadoHabitacionRepository;

    @Override
    public List<EstadoHabitacionDto> listarTodos() {
        return estadoHabitacionRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public EstadoHabitacionDto buscarPorId(Integer id) {
        EstadoHabitacion estadoHabitacion = estadoHabitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        return toDto(estadoHabitacion);
    }

    @Override
    @Transactional
    public EstadoHabitacionDto guardar(EstadoHabitacionDto dto) {
        EstadoHabitacion entity = toEntity(dto);
        return toDto(estadoHabitacionRepository.save(entity));
    }

    @Override
    @Transactional
    public EstadoHabitacionDto actualizar(Integer id, EstadoHabitacionDto dto) {
        estadoHabitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        EstadoHabitacion entity = toEntity(dto);
        entity.setIdEstadoHabitacion(id);

        return toDto(estadoHabitacionRepository.save(entity));
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        EstadoHabitacion estadoHabitacion = estadoHabitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        estadoHabitacionRepository.delete(estadoHabitacion);
    }

    private EstadoHabitacionDto toDto(EstadoHabitacion entity) {
        return EstadoHabitacionDto.builder()
                .idEstadoHabitacion(entity.getIdEstadoHabitacion())
                .descripcion(entity.getDescripcion())
                .estado(entity.getEstado())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

    private EstadoHabitacion toEntity(EstadoHabitacionDto dto) {
        return EstadoHabitacion.builder()
                .idEstadoHabitacion(dto.getIdEstadoHabitacion())
                .descripcion(dto.getDescripcion())
                .estado(dto.getEstado())
                .fechaCreacion(dto.getFechaCreacion())
                .build();
    }
}