package com.hotel.cibertec.controller;

import com.hotel.cibertec.dto.ApiResponse;
import com.hotel.cibertec.dto.TipoPersonaDto;
import com.hotel.cibertec.service.TipoPersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tipopersona")
@RequiredArgsConstructor
public class TipoPersonaController {

    private final TipoPersonaService service;

    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<?>> listar() {
        try {
            return ResponseEntity.ok(ApiResponse.success(service.listarTodos()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ApiResponse<?>> buscar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(service.buscarPorId(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<?>> registrar(@Valid @RequestBody TipoPersonaDto dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success(service.guardar(dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ApiResponse<?>> actualizar(@PathVariable Integer id, @Valid @RequestBody TipoPersonaDto dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success(service.actualizar(id, dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Integer id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}