package com.hotel.cibertec.entity;

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
@Table(name = "HABITACION")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idhabitacion")
    private Integer idHabitacion;

    @Column(name = "numero")
    private String numero;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "precio")
    private BigDecimal precio;

    @ManyToOne
    @JoinColumn(name = "idestadohabitacion") // Relación correcta
    private EstadoHabitacion estadoHabitacion;

    @ManyToOne
    @JoinColumn(name = "idpiso") // Relación correcta
    private Piso piso;

    @ManyToOne
    @JoinColumn(name = "idcategoria") // Relación correcta
    private Categoria categoria;
    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImagenHabitacion> imagenes;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "fechacreacion")
    private LocalDateTime fechaCreacion;

    @JsonIgnore
    @OneToMany(mappedBy = "habitacion")
    private List<Recepcion> recepciones;
}