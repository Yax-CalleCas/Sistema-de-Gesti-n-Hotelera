package com.hotel.cibertec.repository;

import com.hotel.cibertec.dto.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz para la capa de persistencia de reportes.
 * Refleja la estructura de los procedimientos almacenados optimizados.
 */
public interface ReporteRepository {

    // Lista productos con stock crítico
    List<ReporteProductoDto> getProductosBajoStock(Integer limite);

    // Lista ventas validadas con estado 'PAGADO' en el rango de fechas
    List<ReporteVentaDto> getVentas(LocalDateTime inicio, LocalDateTime fin);

    // Reporte de ocupación basada en salidas efectivas
    List<ReporteHabitacionDto> getOcupacion(LocalDateTime inicio, LocalDateTime fin);

    // Reporte consolidado: Alojamiento + Consumos (Integrador financiero)
    List<ReporteCobroDto> getCobros(LocalDateTime inicio, LocalDateTime fin);

    // Estadísticas para el Dashboard central
    DashboardStatsDto getDashboardStats();
}