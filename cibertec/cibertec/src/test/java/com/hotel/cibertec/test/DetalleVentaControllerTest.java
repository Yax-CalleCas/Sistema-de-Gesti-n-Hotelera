package com.hotel.cibertec.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.cibertec.dto.DetalleVentaDto;
import com.hotel.cibertec.service.DetalleVentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
public class DetalleVentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Con @MockBean, "fingimos" que el servicio funciona correctamente
    // evitando que intente buscar en una base de datos real
    @MockitoBean
    private DetalleVentaService detalleVentaService;
    @Test
    void registrarDetalleVentaTest() throws Exception {
        DetalleVentaDto dto = DetalleVentaDto.builder()
                .idVenta(1)
                .idProducto(1)
                .cantidad(5)
                .subTotal(new BigDecimal("100.00"))
                .build();

        // Configuramos el mock para que devuelva un objeto exitoso
        when(detalleVentaService.guardar(any(DetalleVentaDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/detalleventa/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarDetalleVentaTest() throws Exception {
        DetalleVentaDto dto = DetalleVentaDto.builder()
                .idVenta(1)
                .idProducto(1)
                .cantidad(10)
                .subTotal(new BigDecimal("200.00"))
                .build();

        // Configuramos el mock para la actualización
        when(detalleVentaService.actualizar(any(Integer.class), any(DetalleVentaDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/detalleventa/actualizar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}