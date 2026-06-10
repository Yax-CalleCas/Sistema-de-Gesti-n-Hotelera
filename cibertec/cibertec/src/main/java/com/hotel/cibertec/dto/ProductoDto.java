package com.hotel.cibertec.dto;


import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDto {

    private Integer idProducto;

    @NotBlank
    private String nombre;

    private String detalle;

    private BigDecimal precio;

    private Integer cantidad;

    private Boolean estado;

    private LocalDateTime fechaCreacion;
}