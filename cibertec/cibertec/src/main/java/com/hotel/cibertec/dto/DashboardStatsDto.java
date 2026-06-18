package com.hotel.cibertec.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatsDto {
    private Long habitacionesOcupadas;
    private Long habitacionesDisponibles;
    private Long productosBajoStock;
    private BigDecimal ingresosHoy;
}