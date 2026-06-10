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
@Table(name = "PERSONA")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpersona")
    private Integer idPersona;

    @Column(name = "tipodocumento")
    private String tipoDocumento;

    @Column(name = "documento")
    private String documento;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "correo")
    private String correo;

    @Column(name = "clave")
    private String clave;

    @ManyToOne
    @JoinColumn(name = "idtipopersona") // Debe ser igual al nombre de la FK en tu tabla
    private TipoPersona tipoPersona;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "fechacreacion")
    private LocalDateTime fechaCreacion;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Recepcion> recepciones;
}