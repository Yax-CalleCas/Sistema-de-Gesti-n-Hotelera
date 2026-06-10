package com.hotel.cibertec.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "VENTA")
@NamedStoredProcedureQuery(
        name = "sp_RegistrarVenta",
        procedureName = "sp_RegistrarVenta",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_IdRecepcion", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_Estado", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_Detalles", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_Resultado", type = Boolean.class)
        }
)
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idventa")
    private Integer idVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrecepcion")
    private Recepcion recepcion;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "fechacreacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalleVentas;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}