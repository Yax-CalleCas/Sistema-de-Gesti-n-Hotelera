package com.hotel.cibertec.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.cibertec.dto.DetalleVentaDto;
import com.hotel.cibertec.dto.VentaDto;
import com.hotel.cibertec.entity.Venta;
import com.hotel.cibertec.repository.VentaRepository;
import com.hotel.cibertec.service.VentaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository repository;
    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<VentaDto> listarTodos() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDto buscarPorId(Integer id) {
        return repository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    @Override
    @Transactional
    public VentaDto guardar(VentaDto dto) {
        if (dto.getIdRecepcion() == null || dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new RuntimeException("Datos de venta incompletos");
        }

        String estadoFinal = (dto.getEstado() != null && !dto.getEstado().isEmpty()) ? dto.getEstado() : "PENDIENTE";

        try {
            String detallesJson = objectMapper.writeValueAsString(dto.getDetalles());
            String sql = "SELECT sp_RegistrarVenta(:p_IdRecepcion, :p_Estado, :p_Detalles)";

            Boolean exito = (Boolean) entityManager.createNativeQuery(sql)
                    .setParameter("p_IdRecepcion", dto.getIdRecepcion())
                    .setParameter("p_Estado", estadoFinal)
                    .setParameter("p_Detalles", detallesJson)
                    .getSingleResult();

            if (Boolean.TRUE.equals(exito)) {
                return dto;
            } else {
                throw new RuntimeException("Error al procesar la venta en BD: El procedimiento retornó falso");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al procesar JSON", e);
        } catch (Exception e) {
            throw new RuntimeException("Error en BD: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDto> listarPorRecepcion(Integer idRecepcion) {
        if (idRecepcion == null) return Collections.emptyList();
        return repository.findByRecepcionId(idRecepcion).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VentaDto actualizar(Integer id, VentaDto dto) {
        Venta venta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        venta.setEstado(dto.getEstado());
        return toDto(repository.save(venta));
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) throw new RuntimeException("Venta no encontrada");
        repository.deleteById(id);
    }

    private VentaDto toDto(Venta venta) {
        List<DetalleVentaDto> detallesDto = venta.getDetalleVentas().stream().map(d ->
                DetalleVentaDto.builder()
                        .idDetalleVenta(d.getIdDetalleVenta())
                        .idVenta(venta.getIdVenta())
                        .idProducto(d.getProducto() != null ? d.getProducto().getIdProducto() : null)
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        // Cálculo manual seguro:
                        .subTotal(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                        .build()
        ).collect(Collectors.toList());

        return VentaDto.builder()
                .idVenta(venta.getIdVenta())
                .idRecepcion(venta.getRecepcion() != null ? venta.getRecepcion().getIdRecepcion() : null)
                .total(venta.getTotal())
                .estado(venta.getEstado())
                .detalles(detallesDto)
                .build();
    }
}