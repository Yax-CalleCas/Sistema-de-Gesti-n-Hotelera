package com.hotel.cibertec.controller;

import com.hotel.cibertec.dto.*;
import com.hotel.cibertec.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteRepository reporteService;

    // 1. Reporte de Inventario (Bajo stock)
    @GetMapping("/productos-bajo-stock")
    public ResponseEntity<ApiResponse<List<ReporteProductoDto>>> getProductosBajoStock(@RequestParam Integer limite) {
        return ResponseEntity.ok(ApiResponse.success(reporteService.getProductosBajoStock(limite)));
    }

    // 2. Reporte de Ventas
    @GetMapping("/ventas")
    public ResponseEntity<ApiResponse<List<ReporteVentaDto>>> getVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(ApiResponse.success(reporteService.getVentas(inicio, fin)));
    }

    // 3. Reporte de Ocupación
    @GetMapping("/ocupacion")
    public ResponseEntity<ApiResponse<List<ReporteHabitacionDto>>> getOcupacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(ApiResponse.success(reporteService.getOcupacion(inicio, fin)));
    }

    // 4. Reporte de Cobros
    @GetMapping("/cobros")
    public ResponseEntity<ApiResponse<List<ReporteCobroDto>>> getCobros(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        // Validación básica de coherencia temporal
        if (inicio.isAfter(fin)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("La fecha de inicio no puede ser posterior a la fecha de fin."));
        }

        return ResponseEntity.ok(ApiResponse.success(reporteService.getCobros(inicio, fin)));
    }

    // 5. Estadísticas para Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(reporteService.getDashboardStats()));
    }
}