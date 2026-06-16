package com.hotel.cibertec.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReporteProductoDto {
    private Integer idProducto;
    private String nombre;
    private Integer cantidad;
    private BigDecimal precio;
    private Boolean estado;
}
