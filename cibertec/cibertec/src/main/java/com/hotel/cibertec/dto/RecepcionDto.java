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

    // Fechas (LocalDate elimina el error de parseo de formato de hora)
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;

    // Campo para auditoría (se mantiene LocalDateTime si fuera necesario)
    private LocalDateTime fechaSalidaConfirmacion;

    // Finanzas
    private BigDecimal precioInicial;
    private BigDecimal adelanto;
    private BigDecimal precioRestante;
    private BigDecimal totalPagado;
    private BigDecimal costoPenalidad;

    private String observacion;
    private Boolean estado;
}