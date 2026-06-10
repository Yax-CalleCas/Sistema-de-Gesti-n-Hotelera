package com.hotel.cibertec.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private Integer idPersona;
    private String nombre;
    private String apellido;
    private String correo;
    private String tipoPersona;
    private String token;
}