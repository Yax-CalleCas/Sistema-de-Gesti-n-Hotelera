package com.hotel.cibertec.dto;


import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoHabitacionDto {

    private Integer idEstadoHabitacion;

    @NotBlank
    private String descripcion;

    private Boolean estado;

    private LocalDateTime fechaCreacion;
}