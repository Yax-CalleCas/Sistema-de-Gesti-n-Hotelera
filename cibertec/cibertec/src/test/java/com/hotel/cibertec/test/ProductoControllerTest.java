package com.hotel.cibertec.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.cibertec.dto.ProductoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    // Generador de nombres únicos para productos
    private String getUniqueProductName() {
        return "Producto-" + UUID.randomUUID().toString().substring(0, 8);
    }
    @Test
    void listarProductos() throws Exception {

        System.out.println("=================================");
        System.out.println("PROBANDO LISTAR PRODUCTOS");
        System.out.println("=================================");

        mockMvc.perform(get("/api/producto/listar"))
                .andExpect(status().isOk());

        System.out.println("LISTAR PRODUCTOS OK");
    }

    @Test
    void buscarProducto() throws Exception {

        Integer id = 1;

        System.out.println("=================================");
        System.out.println("PROBANDO BUSCAR PRODUCTO");
        System.out.println("ID: " + id);
        System.out.println("=================================");

        mockMvc.perform(get("/api/producto/buscar/" + id))
                .andExpect(status().isOk());

        System.out.println("BUSCAR PRODUCTO OK");
    }

    @Test
    void registrarProducto() throws Exception {
        // 1. Usamos UUID para asegurar que el nombre sea único en cada ejecución
        String nombreUnico = "Producto-" + java.util.UUID.randomUUID().toString().substring(0, 8);

        ProductoDto dto = ProductoDto.builder()
                .nombre(nombreUnico)
                .detalle("Producto creado desde prueba automática")
                .precio(new java.math.BigDecimal("25.50"))
                .cantidad(100)
                .estado(true)
                .build();

        System.out.println("=================================");
        System.out.println("PROBANDO REGISTRO PRODUCTO");
        System.out.println("Nombre: " + dto.getNombre());
        System.out.println("=================================");

        // 2. Realizamos la petición y validamos que el estado HTTP sea 200 OK
        mockMvc.perform(
                        post("/api/producto/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        System.out.println("REGISTRO PRODUCTO OK");
    }

    @Test
    void actualizarProducto() throws Exception {

        Integer id = 1;

        ProductoDto dto = ProductoDto.builder()
                .nombre("Producto Actualizado JUnit")
                .detalle("Actualización automática")
                .precio(new BigDecimal("50.00"))
                .cantidad(200)
                .estado(true)
                .build();

        System.out.println("=================================");
        System.out.println("PROBANDO ACTUALIZAR PRODUCTO");
        System.out.println("ID: " + id);
        System.out.println("=================================");

        mockMvc.perform(
                        put("/api/producto/actualizar/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        System.out.println("ACTUALIZACIÓN PRODUCTO OK");
    }

    @Test
    void eliminarProducto() throws Exception {

        Integer id = 2;

        System.out.println("=================================");
        System.out.println("PROBANDO ELIMINAR PRODUCTO");
        System.out.println("ID: " + id);
        System.out.println("=================================");

        mockMvc.perform(delete("/api/producto/eliminar/" + id))
                .andExpect(status().isOk());

        System.out.println("ELIMINACIÓN PRODUCTO OK");
    }
}