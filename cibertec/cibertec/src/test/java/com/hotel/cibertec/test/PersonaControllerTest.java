package com.hotel.cibertec.test;

import com.hotel.cibertec.entity.Persona;
import com.hotel.cibertec.repository.PersonaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonaRepository personaRepository;

    @Test
    void registrarPersona() throws Exception {

        String correo = "persona.test." + System.currentTimeMillis() + "@mail.com";
        String documento = String.valueOf(System.currentTimeMillis()).substring(5);

        String json = """
                {
                  "tipoDocumento":"DNI",
                  "documento":"%s",
                  "nombre":"Juan",
                  "apellido":"Perez",
                  "correo":"%s",
                  "clave":"123456",
                  "idTipoPersona":1
                }
                """.formatted(documento, correo);

        mockMvc.perform(
                        post("/api/persona/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk());

        Optional<Persona> persona = personaRepository.findByCorreo(correo);

        Assertions.assertTrue(persona.isPresent());
        Assertions.assertEquals(documento, persona.get().getDocumento());
        Assertions.assertEquals("Juan", persona.get().getNombre());
        Assertions.assertEquals("Perez", persona.get().getApellido());
        Assertions.assertEquals(correo, persona.get().getCorreo());
    }
}