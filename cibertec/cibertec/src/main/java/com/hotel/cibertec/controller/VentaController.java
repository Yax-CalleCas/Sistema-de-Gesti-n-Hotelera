package com.hotel.cibertec.controller;

import com.hotel.cibertec.dto.ApiResponse;
import com.hotel.cibertec.dto.VentaDto;
import com.hotel.cibertec.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venta")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {

    private final VentaService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VentaDto>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(service.listarTodos()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ApiResponse<?>> buscar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(service.buscarPorId(id)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> registrar(@Valid @RequestBody VentaDto dto) {
        try {
            VentaDto ventaRegistrada = service.guardar(dto);
            return ResponseEntity.ok(ApiResponse.success(ventaRegistrada));
        } catch (RuntimeException e) {
            // Esto capturará el error "Stock insuficiente" o cualquier error del SP
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            // Esto captura errores inesperados del servidor
            return ResponseEntity.internalServerError().body(ApiResponse.error("Error inesperado en el servidor"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaDto>> actualizar(@PathVariable Integer id, @Valid @RequestBody VentaDto dto) {
        return ResponseEntity.ok(ApiResponse.success(service.actualizar(id, dto)));
    }
// En com.hotel.cibertec.controller.VentaController.java

    @GetMapping("/recepcion/{idRecepcion}")
    public ResponseEntity<ApiResponse<List<VentaDto>>> listarPorRecepcion(@PathVariable Integer idRecepcion) {
        // Si tu capa de servicio ya tiene la búsqueda armada por recepción:
        return ResponseEntity.ok(ApiResponse.success(service.listarPorRecepcion(idRecepcion)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}