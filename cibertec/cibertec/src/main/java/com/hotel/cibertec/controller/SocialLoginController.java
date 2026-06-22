package com.hotel.cibertec.controller;

import com.hotel.cibertec.dto.*;
import com.hotel.cibertec.entity.Persona;
import com.hotel.cibertec.security.JwtService;
import com.hotel.cibertec.security.SocialAuthValidator;
import com.hotel.cibertec.service.SocialAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SocialLoginController {
    private final SocialAuthService socialAuthService;
    private final JwtService jwtService;
    private final SocialAuthValidator socialAuthValidator; // Inyectamos el validador

    @PostMapping("/social")
    public ResponseEntity<ApiResponse<LoginResponseDto>> socialLogin(@RequestBody SocialLoginRequestDto request) {

        // 1. VALIDACIÓN OBLIGATORIA: Verifica que el token sea genuino de Google/Facebook
        if (!socialAuthValidator.isValidToken(request.getToken(), request.getProvider())) {
            throw new RuntimeException("Error: Token de autenticación externo no válido");
        }

        // 2. Procesa el login (buscar en BD o registrar)
        Persona persona = socialAuthService.processSocialLogin(
                request.getNombre(),
                request.getApellido(),
                request.getCorreo()
        );

        // 3. Genera tu JWT para que el resto de tu app (Spring Security) lo reconozca
        String token = jwtService.generateToken(persona.getCorreo());

        LoginResponseDto response = LoginResponseDto.builder()
                .idPersona(persona.getIdPersona())
                .nombre(persona.getNombre())
                .apellido(persona.getApellido())
                .correo(persona.getCorreo())
                .token(token)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login social exitoso", response));
    }
}