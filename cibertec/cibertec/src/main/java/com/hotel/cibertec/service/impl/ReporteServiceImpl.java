package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.*;

import com.hotel.cibertec.repository.ReporteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<ReporteProductoDto> getProductosBajoStock(Integer limite) {
        List<Object[]> results = entityManager.createNativeQuery("SELECT * FROM fn_Reporte_ProductosBajoStock(:limite)")
                .setParameter("limite", limite)
                .getResultList();

        return results.stream().map(row -> new ReporteProductoDto(
                (Integer) row[0],
                (String) row[1],
                ((Number) row[2]).intValue(),
                (BigDecimal) row[3],
                (Boolean) row[4]
        )).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReporteVentaDto> getVentas(LocalDateTime inicio, LocalDateTime fin) {
        List<Object[]> results = entityManager.createNativeQuery("SELECT * FROM fn_Reporte_Ventas(:inicio, :fin)")
                .setParameter("inicio", Timestamp.valueOf(inicio))
                .setParameter("fin", Timestamp.valueOf(fin))
                .getResultList();

        return results.stream().map(row -> new ReporteVentaDto(
                (String) row[0],
                ((Number) row[1]).longValue(),
                (BigDecimal) row[2]
        )).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReporteHabitacionDto> getOcupacion(LocalDateTime inicio, LocalDateTime fin) {
        List<Object[]> results = entityManager.createNativeQuery("SELECT * FROM fn_Reporte_Ocupacion(:inicio, :fin)")
                .setParameter("inicio", Timestamp.valueOf(inicio))
                .setParameter("fin", Timestamp.valueOf(fin))
                .getResultList();

        return results.stream().map(row -> new ReporteHabitacionDto(
                (String) row[0],
                (String) row[1],
                ((Number) row[2]).longValue()
        )).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReporteCobroDto> getCobros(LocalDateTime inicio, LocalDateTime fin) {
        List<Object[]> results = entityManager.createNativeQuery("SELECT * FROM fn_Reporte_Cobros(:inicio, :fin)")
                .setParameter("inicio", Timestamp.valueOf(inicio))
                .setParameter("fin", Timestamp.valueOf(fin))
                .getResultList();

        return results.stream().map(row -> new ReporteCobroDto(
                (String) row[0],
                (String) row[1],
                (BigDecimal) row[2],
                ((Timestamp) row[3]).toLocalDateTime()
        )).collect(Collectors.toList());
    }

    @Override
    public DashboardStatsDto getDashboardStats() {
        Object[] row = (Object[]) entityManager.createNativeQuery("SELECT * FROM fn_Dashboard_Estadisticas()")
                .getSingleResult();

        return DashboardStatsDto.builder()
                .habitacionesOcupadas(((Number) row[0]).longValue())
                .habitacionesDisponibles(((Number) row[1]).longValue())
                .productosBajoStock(((Number) row[2]).longValue())
                .ingresosHoy((BigDecimal) row[3])
                .build();
    }
}