package com.hotel.cibertec.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReporteVentaDto {
    private String nombreProducto;
    private Long cantidadTotal;
    private BigDecimal totalIngresado;
}