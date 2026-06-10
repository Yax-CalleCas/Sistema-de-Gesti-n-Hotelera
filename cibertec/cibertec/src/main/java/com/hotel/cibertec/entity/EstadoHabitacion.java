package com.hotel.cibertec.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ESTADO_HABITACION")
public class EstadoHabitacion {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idestadohabitacion")
    private Integer idEstadoHabitacion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "fechacreacion")
    private LocalDateTime fechaCreacion;
    @JsonIgnore
    @OneToMany(mappedBy = "estadoHabitacion")
    private List<Habitacion> habitaciones;
}