package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.PersonaDto;
import com.hotel.cibertec.entity.Persona;
import com.hotel.cibertec.repository.PersonaRepository;
import com.hotel.cibertec.service.PersonaService;
import com.hotel.cibertec.service.SocialAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialAuthServiceImpl implements SocialAuthService {
    private final PersonaRepository personaRepository;
    private final PersonaService personaService;

    @Override
    @Transactional
    public Persona processSocialLogin(String nombre, String apellido, String correo) {
        return personaRepository.findByCorreo(correo).orElseGet(() -> {
            PersonaDto nuevo = PersonaDto.builder()
                    .nombre(nombre).apellido(apellido).correo(correo)
                    .clave("SOCIAL_LOGIN_USER").idTipoPersona(3)
                    .tipoDocumento("DNI").documento("00000000").estado(true)
                    .build();
            personaService.guardar(nuevo);
            return personaRepository.findByCorreo(correo).orElseThrow();
        });
    }
}