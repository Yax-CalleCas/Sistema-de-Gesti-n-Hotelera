// com/hotel/cibertec/controller/LoginController.java
package com.hotel.cibertec.controller;

import com.hotel.cibertec.dto.ApiResponse;
import com.hotel.cibertec.dto.LoginRequestDto;
import com.hotel.cibertec.dto.LoginResponseDto;
import com.hotel.cibertec.entity.Persona;
import com.hotel.cibertec.exception.BusinessException;
import com.hotel.cibertec.repository.PersonaRepository;
import com.hotel.cibertec.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final PersonaRepository personaRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginController(
            PersonaRepository personaRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @RequestBody LoginRequestDto request) {

        // 1. Validar que el usuario exista
        Persona persona = personaRepository
                .findByCorreo(request.getCorreo())
                .orElseThrow(() -> new BusinessException("Correo no registrado"));

        // 2. Validar contraseña
        if (persona.getClave() == null || !passwordEncoder.matches(request.getClave(), persona.getClave())) {
            throw new BusinessException("Contraseña incorrecta");
        }

        // 3. Generar Token JWT
        String token = jwtService.generateToken(persona.getCorreo());

        // 4. Validación Defensiva del Rol (Evita el NullPointerException que causa el Error 500)
        String rolDescripcion = "Cliente"; // Rol por defecto por seguridad
        if (persona.getTipoPersona() != null && persona.getTipoPersona().getDescripcion() != null) {
            rolDescripcion = persona.getTipoPersona().getDescripcion();
        }

        // 5. Construir Respuesta de forma segura
        LoginResponseDto response = LoginResponseDto.builder()
                .idPersona(persona.getIdPersona())
                .nombre(persona.getNombre())
                .apellido(persona.getApellido())
                .correo(persona.getCorreo())
                .tipoPersona(rolDescripcion) // Asignamos la variable validada
                .token(token)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }
}