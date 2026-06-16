package com.hotel.cibertec.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReporteHabitacionDto {
    private String numeroHabitacion;
    private String categoria;
    private Long vecesAlquilada;
}