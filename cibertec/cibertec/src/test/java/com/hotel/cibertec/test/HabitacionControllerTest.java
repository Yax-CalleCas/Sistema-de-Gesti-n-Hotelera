package com.hotel.cibertec.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.cibertec.dto.HabitacionDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HabitacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Generamos un número de habitación único para evitar duplicidad
    private String getUniqueNumero() {
        return "HAB-" + UUID.randomUUID().toString().substring(0, 5);
    }

    @Test
    @Order(1)
    void registrarHabitacionTest() throws Exception {
        HabitacionDto dto = HabitacionDto.builder()
                .numero(getUniqueNumero())
                .detalle("Habitación estándar con vista al mar")
                .precio(new BigDecimal("150.00"))
                .idPiso(1)
                .idCategoria(1)
                .idEstadoHabitacion(1) // <--- ¡AQUÍ ESTÁ EL ERROR! Agrégalo.
                .estado(true)
                .build();

        mockMvc.perform(post("/api/habitacion/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void listarHabitacionesTest() throws Exception {
        mockMvc.perform(get("/api/habitacion/listar"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void buscarHabitacionTest() throws Exception {
        // Asegúrate que el ID 1 exista en tu base de datos de pruebas
        mockMvc.perform(get("/api/habitacion/buscar/1"))
                .andExpect(status().isOk());
    }
    @Test
    @Order(4)
    void actualizarHabitacionTest() throws Exception {
        HabitacionDto dto = HabitacionDto.builder()
                .numero(getUniqueNumero())
                .detalle("Habitación actualizada")
                .precio(new BigDecimal("180.00"))
                .idPiso(1)
                .idCategoria(1)
                .idEstadoHabitacion(1) // <--- ¡AQUÍ ESTÁ EL MISMO ERROR! Agrégalo.
                .estado(true)
                .build();

        mockMvc.perform(put("/api/habitacion/actualizar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    @Test
    @Order(5)
    void eliminarHabitacionTest() throws Exception {
        mockMvc.perform(delete("/api/habitacion/eliminar/1"))
                .andExpect(status().isOk());
    }
}