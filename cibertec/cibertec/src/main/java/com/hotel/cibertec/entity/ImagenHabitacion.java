package com.hotel.cibertec.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "IMAGEN_HABITACION")
public class ImagenHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idimagen")
    private Integer idImagen;

    @Column(name = "url_imagen", nullable = false)
    private String urlImagen;

    @ManyToOne
    @JoinColumn(name = "idhabitacion")
    @JsonIgnore // Evita bucles infinitos en el JSON
    private Habitacion habitacion;
}