package com.hotel.cibertec.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecepcionDto {

    private Integer idRecepcion;
    private Integer idCliente;
    private Integer idHabitacion;

    // Campos descriptivos
    private String numero;
    private String categoriaNombre;
    private String pisoNombre;
    private String detalleHabitacion;
    private BigDecimal precioHabitacion;
    private String estadoHabitacion;

    // Datos del cliente
    private String tipoDocumento;
    private String documento;
    private String nombre;
    private String apellido;
    private String correo;
    @Builder.Default
    private BigDecimal precioInicial = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal adelanto = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal precioRestante = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalPagado = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal costoPenalidad = BigDecimal.ZERO;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private LocalDateTime fechaSalidaConfirmacion;

    // Finanzas
    private String observacion;
    private Boolean estado;
}