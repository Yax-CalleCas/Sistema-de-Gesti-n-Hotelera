package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.EstadoHabitacionDto;
import com.hotel.cibertec.dto.HabitacionDto;
import com.hotel.cibertec.entity.Habitacion;
import com.hotel.cibertec.entity.ImagenHabitacion;
import com.hotel.cibertec.exception.ResourceNotFoundException;
import com.hotel.cibertec.repository.HabitacionRepository;
import com.hotel.cibertec.repository.ImagenHabitacionRepository;
import com.hotel.cibertec.service.HabitacionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository repository;
    private final ImagenHabitacionRepository imagenRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDto> listarTodos() {
        return repository.findAllWithDetails().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionDto buscarPorId(Integer id) {
        return repository.findByIdWithDetails(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public HabitacionDto guardar(HabitacionDto dto) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_RegistrarHabitacion");
            query.registerStoredProcedureParameter("p_Numero", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_Detalle", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_Precio", java.math.BigDecimal.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_IdPiso", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_IdCategoria", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("Resultado", Boolean.class, ParameterMode.OUT);

            query.setParameter("p_Numero", dto.getNumero());
            query.setParameter("p_Detalle", dto.getDetalle());
            query.setParameter("p_Precio", dto.getPrecio());
            query.setParameter("p_IdPiso", dto.getIdPiso());
            query.setParameter("p_IdCategoria", dto.getIdCategoria());

            query.execute();
            if (Boolean.FALSE.equals(query.getOutputParameterValue("Resultado"))) {
                throw new IllegalArgumentException("El número de habitación ya existe.");
            }

            Habitacion nuevaHab = repository.findByNumero(dto.getNumero())
                    .orElseThrow(() -> new RuntimeException("Error al recuperar la habitación registrada"));

            guardarImagenes(nuevaHab, dto.getUrlsImagenes());
            return toDto(nuevaHab);
        } catch (Exception e) {
            log.error("Error en guardar habitación: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public HabitacionDto actualizar(Integer id, HabitacionDto dto) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_ModificarHabitacion");
        query.registerStoredProcedureParameter("p_IdHabitacion", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Numero", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Detalle", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Precio", java.math.BigDecimal.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_IdPiso", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_IdCategoria", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_IdEstadoHabitacion", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Estado", Boolean.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("Resultado", Boolean.class, ParameterMode.OUT);

        query.setParameter("p_IdHabitacion", id);
        query.setParameter("p_Numero", dto.getNumero());
        query.setParameter("p_Detalle", dto.getDetalle());
        query.setParameter("p_Precio", dto.getPrecio());
        query.setParameter("p_IdPiso", dto.getIdPiso());
        query.setParameter("p_IdCategoria", dto.getIdCategoria());
        query.setParameter("p_IdEstadoHabitacion", dto.getIdEstadoHabitacion());
        query.setParameter("p_Estado", dto.getEstado());

        query.execute();

        imagenRepository.deleteByHabitacion_IdHabitacion(id);
        Habitacion hab = repository.findById(id).orElseThrow();
        guardarImagenes(hab, dto.getUrlsImagenes());

        return toDto(hab);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Habitacion h = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No encontrado"));
        h.setEstado(false);
        repository.save(h);
    }

    private void guardarImagenes(Habitacion habitacion, List<String> urls) {
        if (urls != null) {
            imagenRepository.saveAll(urls.stream()
                    .map(url -> ImagenHabitacion.builder().urlImagen(url).habitacion(habitacion).build())
                    .collect(Collectors.toList()));
        }
    }

    private HabitacionDto toDto(Habitacion e) {
        return HabitacionDto.builder()
                .idHabitacion(e.getIdHabitacion())
                .numero(e.getNumero())
                .detalle(e.getDetalle())
                .precio(e.getPrecio())
                .idPiso(e.getPiso() != null ? e.getPiso().getIdPiso() : null)
                .idCategoria(e.getCategoria() != null ? e.getCategoria().getIdCategoria() : null)
                .idEstadoHabitacion(e.getEstadoHabitacion() != null ? e.getEstadoHabitacion().getIdEstadoHabitacion() : null)
                .estado(e.getEstado())
                // Descripción plana para visualización rápida
                .descripcionEstado(e.getEstadoHabitacion() != null ? e.getEstadoHabitacion().getDescripcion() : null)

                // Objeto estructurado para lógica avanzada en el frontend
                .estadoHabitacion(e.getEstadoHabitacion() != null ?
                        EstadoHabitacionDto.builder()
                                .idEstadoHabitacion(e.getEstadoHabitacion().getIdEstadoHabitacion())
                                .descripcion(e.getEstadoHabitacion().getDescripcion())
                                .estado(e.getEstadoHabitacion().getEstado())
                                .fechaCreacion(e.getEstadoHabitacion().getFechaCreacion())
                                .build()
                        : null)

                .urlsImagenes(e.getImagenes() != null ?
                        e.getImagenes().stream().map(ImagenHabitacion::getUrlImagen).collect(Collectors.toList()) : List.of())
                .build();
    }
}