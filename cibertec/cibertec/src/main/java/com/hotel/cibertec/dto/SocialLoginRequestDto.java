package com.hotel.cibertec.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SocialLoginRequestDto {
    private String token;    // Token de Google/FB
    private String provider; // "GOOGLE" o "FACEBOOK"
    private String nombre;   // Enviado desde Android tras obtener el perfil
    private String apellido;
    private String correo;
}