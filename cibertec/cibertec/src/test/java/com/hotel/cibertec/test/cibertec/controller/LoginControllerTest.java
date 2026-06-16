package com.hotel.cibertec.controller;

import com.hotel.cibertec.dto.LoginRequestDto;
import com.hotel.cibertec.entity.Persona;
import com.hotel.cibertec.repository.PersonaRepository;
import com.hotel.cibertec.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser
class LoginControllerTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginController loginController;

    @Test
    void loginExitoso() {

        LoginRequestDto request = new LoginRequestDto();
        request.setCorreo("admin@test.com");
        request.setClave("123456");

        Persona persona = new Persona();
        persona.setCorreo("admin@test.com");
        persona.setClave("HASH");

        when(personaRepository.findByCorreo("admin@test.com"))
                .thenReturn(Optional.of(persona));

        when(passwordEncoder.matches("123456", "HASH"))
                .thenReturn(true);

        when(jwtService.generateToken("admin@test.com"))
                .thenReturn("TOKEN123");

        var response = loginController.login(request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void correoNoRegistrado() {

        LoginRequestDto request = new LoginRequestDto();
        request.setCorreo("fake@test.com");

        when(personaRepository.findByCorreo(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> loginController.login(request)
        );
    }
    @Test
    void passwordIncorrecto() {

        LoginRequestDto request = new LoginRequestDto();
        request.setCorreo("admin@test.com");
        request.setClave("123");

        Persona persona = new Persona();
        persona.setCorreo("admin@test.com");
        persona.setClave("HASH");

        when(personaRepository.findByCorreo(anyString()))
                .thenReturn(Optional.of(persona));

        when(passwordEncoder.matches("123", "HASH"))
                .thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> loginController.login(request)
        );
    }
}