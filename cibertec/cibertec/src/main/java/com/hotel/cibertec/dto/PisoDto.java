package com.hotel.cibertec.dto;


import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PisoDto {

    private Integer idPiso;

    @NotBlank
    private String descripcion;

    private Boolean estado;

    private LocalDateTime fechaCreacion;
}