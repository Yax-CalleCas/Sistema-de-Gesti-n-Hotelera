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
@Table(name = "CATEGORIA") // PostgreSQL suele buscar tablas en minúsculas, pero 'CATEGORIA' está bien
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcategoria") // Debe ser todo en minúsculas
    private Integer idCategoria;

    @Column(name = "descripcion") // Sin mayúsculas
    private String descripcion;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "fechacreacion") // Sin guiones, todo minúsculas
    private LocalDateTime fechaCreacion;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Habitacion> habitaciones;
}