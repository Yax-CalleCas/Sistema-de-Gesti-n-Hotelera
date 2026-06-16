package com.hotel.cibertec.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // Import necesario para validar la longitud
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

    // Validamos que la URL tenga una longitud razonable
    @Size(max = 500, message = "La URL de la imagen es demasiado larga")
    private String imagenUrl;

    private BigDecimal precio;

    private Integer cantidad;

    private Boolean estado;

    private LocalDateTime fechaCreacion;
}