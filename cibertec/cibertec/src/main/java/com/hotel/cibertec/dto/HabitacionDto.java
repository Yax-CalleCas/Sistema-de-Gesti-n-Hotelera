package com.hotel.cibertec.dto;

import lombok.*;
import java.util.List;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HabitacionDto {
    private Integer idHabitacion;
    private String numero;
    private String detalle;
    private BigDecimal precio;
    private Integer idEstadoHabitacion;

    private Integer idPiso;
    private Integer idCategoria;
    private Boolean estado;
    private EstadoHabitacionDto estadoHabitacion;

    // O si prefieres un campo plano:
    private String descripcionEstado;

    // Lista de URLs o rutas de imágenes
    private List<String> urlsImagenes;
}