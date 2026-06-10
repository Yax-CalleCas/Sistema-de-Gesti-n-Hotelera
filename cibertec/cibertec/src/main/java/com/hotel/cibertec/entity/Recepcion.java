package com.hotel.cibertec.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "RECEPCION")
public class Recepcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idrecepcion")
    private Integer idRecepcion;

    @ManyToOne
    @JoinColumn(name = "idcliente")
    private Persona cliente;

    @ManyToOne
    @JoinColumn(name = "idhabitacion")
    private Habitacion habitacion;

    @Column(name = "fechaentrada")
    private LocalDate fechaEntrada; // Cambiado a LocalDate

    @Column(name = "fechasalida")
    private LocalDate fechaSalida;  // Cambiado a LocalDate

    @Column(name = "fechasalidaconfirmacion")
    private LocalDateTime fechaSalidaConfirmacion; // Se mantiene por ser auditoría de evento

    @Column(name = "precioinicial")
    private BigDecimal precioInicial;

    @Column(name = "adelanto")
    private BigDecimal adelanto;

    @Column(name = "preciorestante")
    private BigDecimal precioRestante;

    @Column(name = "totalpagado")
    private BigDecimal totalPagado;

    @Column(name = "costopenalidad")
    private BigDecimal costoPenalidad;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "estado")
    private Boolean estado;

    @JsonIgnore
    @OneToMany(mappedBy = "recepcion")
    private List<Venta> ventas;
}