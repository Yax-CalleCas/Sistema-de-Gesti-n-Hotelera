package com.hotel.cibertec.service;

import com.hotel.cibertec.entity.Persona;

public interface SocialAuthService {
    Persona processSocialLogin(String nombre, String apellido, String correo);
}