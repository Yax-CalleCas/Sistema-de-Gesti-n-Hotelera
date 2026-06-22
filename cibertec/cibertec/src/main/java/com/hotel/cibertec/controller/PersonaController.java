package com.hotel.cibertec.controller;

import com.hotel.cibertec.dto.ApiResponse;
import com.hotel.cibertec.dto.PersonaDto;
import com.hotel.cibertec.service.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persona")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService service;

    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<?>> listar() {
        return ResponseEntity.ok(ApiResponse.success(service.listarTodos()));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ApiResponse<?>> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.buscarPorId(id)));
    }

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<?>> registrar(@Valid @RequestBody PersonaDto dto) {
        return ResponseEntity.ok(ApiResponse.success(service.guardar(dto)));
    }


    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ApiResponse<?>> actualizar(@PathVariable Integer id, @Valid @RequestBody PersonaDto dto) {

        System.out.println("Actualizando ID: " + id + " con datos: " + dto.toString());
        return ResponseEntity.ok(ApiResponse.success(service.actualizar(id, dto)));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Persona eliminada correctamente"));
    }
}