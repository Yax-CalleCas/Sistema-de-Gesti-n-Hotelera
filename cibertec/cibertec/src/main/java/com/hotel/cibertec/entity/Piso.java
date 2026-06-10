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
@Table(name = "PISO")
public class Piso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpiso") // Corregido a minúsculas
    private Integer idPiso;

    @Column(name = "descripcion") // Corregido a minúsculas
    private String descripcion;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "fechacreacion") // Corregido a minúsculas, sin guion bajo
    private LocalDateTime fechaCreacion;

    @JsonIgnore
    @OneToMany(mappedBy = "piso")
    private List<Habitacion> habitaciones;
}