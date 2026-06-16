package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.ProductoDto;
import com.hotel.cibertec.entity.Producto;
import com.hotel.cibertec.repository.ProductoRepository;
import com.hotel.cibertec.service.ProductoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDto buscarPorId(Integer id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }

    @Override
    @Transactional
    public ProductoDto guardar(ProductoDto dto) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_RegistrarProducto");

        query.registerStoredProcedureParameter("p_Nombre", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Detalle", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Precio", java.math.BigDecimal.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Cantidad", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_ImagenUrl", String.class, ParameterMode.IN); // Parámetro añadido
        query.registerStoredProcedureParameter("Resultado", Boolean.class, ParameterMode.OUT);

        query.setParameter("p_Nombre", dto.getNombre());
        query.setParameter("p_Detalle", dto.getDetalle());
        query.setParameter("p_Precio", dto.getPrecio());
        query.setParameter("p_Cantidad", dto.getCantidad());
        query.setParameter("p_ImagenUrl", dto.getImagenUrl()); // Valor añadido

        query.execute();

        Boolean resultado = (Boolean) query.getOutputParameterValue("Resultado");

        if (!Boolean.TRUE.equals(resultado)) {
            throw new RuntimeException("El producto ya existe");
        }

        return dto;
    }

    @Override
    @Transactional
    public ProductoDto actualizar(Integer id, ProductoDto dto) {
        // 1. Validar existencia
        repository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        // 2. Crear query explícita
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_ModificarProducto");

        // REGISTRO DE PARÁMETROS (El orden y el nombre DEBEN coincidir con el SQL)
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN); // p_IdProducto
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);  // p_Nombre
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);  // p_Detalle
        query.registerStoredProcedureParameter(4, java.math.BigDecimal.class, ParameterMode.IN); // p_Precio
        query.registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN); // p_Cantidad
        query.registerStoredProcedureParameter(6, Boolean.class, ParameterMode.IN); // p_Estado
        query.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);  // p_ImagenUrl
        query.registerStoredProcedureParameter(8, Boolean.class, ParameterMode.OUT); // Resultado

        // ASIGNACIÓN POR POSICIÓN (Evita errores de nombre de parámetro)
        query.setParameter(1, id);
        query.setParameter(2, dto.getNombre());
        query.setParameter(3, dto.getDetalle());
        query.setParameter(4, dto.getPrecio());
        query.setParameter(5, dto.getCantidad());
        query.setParameter(6, dto.getEstado());
        query.setParameter(7, dto.getImagenUrl());

        query.execute();

        Boolean resultado = (Boolean) query.getOutputParameterValue(8);
        if (!Boolean.TRUE.equals(resultado)) {
            throw new RuntimeException("No se pudo actualizar el producto");
        }

        return buscarPorId(id);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Producto p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        p.setEstado(false);
        repository.save(p);
    }

    private ProductoDto toDto(Producto e) {
        return ProductoDto.builder()
                .idProducto(e.getIdProducto())
                .nombre(e.getNombre())
                .detalle(e.getDetalle())
                .precio(e.getPrecio())
                .cantidad(e.getCantidad())
                .estado(e.getEstado())
                .imagenUrl(e.getImagenUrl()) // Mapeo añadido
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    private Producto toEntity(ProductoDto d) {
        return Producto.builder()
                .idProducto(d.getIdProducto())
                .nombre(d.getNombre())
                .detalle(d.getDetalle())
                .precio(d.getPrecio())
                .cantidad(d.getCantidad())
                .estado(d.getEstado())
                .imagenUrl(d.getImagenUrl()) // Mapeo añadido
                .fechaCreacion(d.getFechaCreacion())
                .build();
    }
}