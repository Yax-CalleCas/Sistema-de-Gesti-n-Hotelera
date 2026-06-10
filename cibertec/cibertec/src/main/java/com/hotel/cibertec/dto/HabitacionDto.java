package com.hotel.cibertec.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionDto {

    private Integer idHabitacion;

    @NotBlank
    private String numero;

    private String detalle;

    private BigDecimal precio;

    @NotNull
    private Integer idEstadoHabitacion;

    @NotNull
    private Integer idPiso;

    @NotNull
    private Integer idCategoria;

    private Boolean estado;

    private LocalDateTime fechaCreacion;
}