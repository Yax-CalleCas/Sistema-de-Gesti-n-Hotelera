package com.hotel.cibertec.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.cibertec.dto.EstadoHabitacionDto;
import com.hotel.cibertec.entity.EstadoHabitacion; // Asegúrate de importar tu entidad
import com.hotel.cibertec.repository.EstadoHabitacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@Transactional // IMPORTANTE: Esto asegura que los datos creados en el test se borren al terminar
public class EstadoHabitacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EstadoHabitacionRepository repository;

    @Test
    void listarTest() throws Exception {
        mockMvc.perform(get("/api/estadohabitacion/listar"))
                .andExpect(status().isOk());
    }

    @Test
    void registrarTest() throws Exception {
        EstadoHabitacionDto dto = EstadoHabitacionDto.builder()
                .descripcion("NUEVO ESTADO")
                .estado(true)
                .build();

        mockMvc.perform(post("/api/estadohabitacion/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarTest() throws Exception {
        // 1. Creamos un registro real antes de intentar actualizarlo
        EstadoHabitacion entidad = repository.save(EstadoHabitacion.builder()
                .descripcion("PREVIO").estado(true).build());

        EstadoHabitacionDto dto = EstadoHabitacionDto.builder()
                .descripcion("ACTUALIZADO")
                .estado(true)
                .build();

        // 2. Usamos el ID de la entidad que acabamos de crear
        mockMvc.perform(put("/api/estadohabitacion/actualizar/" + entidad.getIdEstadoHabitacion())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarTest() throws Exception {
        // 1. Creamos un registro para asegurar que el ID 1 no sea necesario
        EstadoHabitacion entidad = repository.save(EstadoHabitacion.builder()
                .descripcion("PARA ELIMINAR").estado(true).build());

        // 2. Eliminamos el registro que acabamos de crear
        mockMvc.perform(delete("/api/estadohabitacion/eliminar/" + entidad.getIdEstadoHabitacion()))
                .andExpect(status().isOk());
    }
}