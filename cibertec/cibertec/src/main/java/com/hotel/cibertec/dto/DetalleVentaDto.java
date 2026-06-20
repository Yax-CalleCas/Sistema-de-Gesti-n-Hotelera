package com.hotel.cibertec.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVentaDto {
    private Integer idDetalleVenta;
    private Integer idVenta;
    private Integer idProducto;

    // Campo necesario para mostrar el nombre en la interfaz
    private String nombreProducto;

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subTotal;
}