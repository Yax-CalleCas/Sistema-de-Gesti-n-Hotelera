package com.hotel.cibertec.repository;

import com.hotel.cibertec.dto.*;
import java.time.LocalDateTime;
import java.util.List;

public interface ReporteRepository {
    List<ReporteProductoDto> getProductosBajoStock(Integer limite);
    List<ReporteVentaDto> getVentas(LocalDateTime inicio, LocalDateTime fin);
    List<ReporteHabitacionDto> getOcupacion(LocalDateTime inicio, LocalDateTime fin);
    List<ReporteCobroDto> getCobros(LocalDateTime inicio, LocalDateTime fin); // <-- Agregado
    DashboardStatsDto getDashboardStats();
}