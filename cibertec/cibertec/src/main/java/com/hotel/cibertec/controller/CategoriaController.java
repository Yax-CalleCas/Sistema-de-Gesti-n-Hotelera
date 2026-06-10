package com.hotel.cibertec.controller;

import com.hotel.cibertec.dto.ApiResponse;
import com.hotel.cibertec.dto.CategoriaDto;
import com.hotel.cibertec.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categoria")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService service;

    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<?>> listar() {
        try { return ResponseEntity.ok(ApiResponse.success(service.listarTodos())); }
        catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage())); }
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ApiResponse<?>> buscar(@PathVariable Integer id) {
        try { return ResponseEntity.ok(ApiResponse.success(service.buscarPorId(id))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage())); }
    }

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<?>> registrar(@Valid @RequestBody CategoriaDto dto) {
        try { return ResponseEntity.ok(ApiResponse.success(service.guardar(dto))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage())); }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ApiResponse<?>> actualizar(@PathVariable Integer id, @Valid @RequestBody CategoriaDto dto) {
        try { return ResponseEntity.ok(ApiResponse.success(service.actualizar(id, dto))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage())); }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Integer id) {
        try { service.eliminar(id); return ResponseEntity.ok(ApiResponse.success(null)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage())); }
    }
}