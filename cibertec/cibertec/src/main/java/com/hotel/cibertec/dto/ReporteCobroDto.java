package com.hotel.cibertec.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReporteCobroDto {
    private String numeroHabitacion;
    private String nombreCliente;
    private BigDecimal totalPagado;
    private LocalDateTime fechaPago;
}
