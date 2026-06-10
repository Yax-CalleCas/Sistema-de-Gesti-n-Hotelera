package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.HabitacionDto;
import com.hotel.cibertec.entity.Habitacion;
import com.hotel.cibertec.repository.HabitacionRepository;
import com.hotel.cibertec.service.HabitacionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository repository;
    private final EntityManager entityManager; // Inyectamos el gestor de entidades

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDto> listarTodos() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionDto buscarPorId(Integer id) {
        return repository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
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
            Boolean resultado = (Boolean) query.getOutputParameterValue("Resultado");

            if (Boolean.FALSE.equals(resultado)) {
                throw new IllegalArgumentException("El número de habitación ya se encuentra registrado.");
            }

            return dto;
        } catch (Exception e) {
            // Forzamos el lanzamiento controlado para que llegue al controlador como error HTTP estricto
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public HabitacionDto actualizar(Integer id, HabitacionDto dto) {
        try {
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
            Boolean resultado = (Boolean) query.getOutputParameterValue("Resultado");

            if (Boolean.FALSE.equals(resultado)) {
                throw new IllegalArgumentException("No se pudo actualizar. El número de habitación ya existe.");
            }

            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Habitacion h = repository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        h.setEstado(false); // Eliminación lógica
        repository.save(h);
    }

    private HabitacionDto toDto(Habitacion e) {
        return HabitacionDto.builder()
                .idHabitacion(e.getIdHabitacion())
                .numero(e.getNumero())
                .detalle(e.getDetalle())
                .precio(e.getPrecio())
                .idPiso(e.getPiso() != null ? e.getPiso().getIdPiso() : null)
                .idCategoria(e.getCategoria() != null ? e.getCategoria().getIdCategoria() : null)
                .idEstadoHabitacion(e.getEstadoHabitacion() != null ? e.getEstadoHabitacion().getIdEstadoHabitacion() : null) // AGREGAR ESTA LÍNEA
                .estado(e.getEstado())
                .build();
    }
}