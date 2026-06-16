package com.hotel.cibertec.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties({"detalleVentas"})
@Table(name = "PRODUCTO")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproducto")
    private Integer idProducto;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "estado")
    private Boolean estado;

    // Campo nuevo para la URL de la imagen
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "fechacreacion")
    private LocalDateTime fechaCreacion;

    @JsonIgnore
    @OneToMany(mappedBy = "producto")
    private List<DetalleVenta> detalleVentas;
}