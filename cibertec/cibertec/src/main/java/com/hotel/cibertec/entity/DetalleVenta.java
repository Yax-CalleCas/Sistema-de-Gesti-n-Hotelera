package com.hotel.cibertec.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DETALLE_VENTA")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddetalleventa")
    private Integer idDetalleVenta;

    @ManyToOne
    @JoinColumn(name = "idventa")
    private Venta venta;



    @ManyToOne
    @JoinColumn(name = "idproducto")
    private Producto producto;

    @Column(name = "cantidad")
    private Integer cantidad;

    // AÑADIDO: Guardar el precio al momento de la venta
    @Column(name = "preciounitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;


    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subTotal;
}