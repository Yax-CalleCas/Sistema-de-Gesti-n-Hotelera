package com.hotel.cibertec.controller;

import com.hotel.cibertec.dto.ApiResponse;
import com.hotel.cibertec.dto.RecepcionDto;

import com.hotel.cibertec.service.RecepcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/recepcion")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RecepcionController {

    private final RecepcionService service;

    // --- ENDPOINT UNIFICADO Y DEPURADO ---
    @GetMapping("/habitacion-activa/{idHabitacion}")
    public ResponseEntity<ApiResponse<?>> buscarActivaPorHabitacion(@PathVariable Integer idHabitacion) {
        System.out.println("DEBUG: Iniciando búsqueda para habitación ID: " + idHabitacion);

        try {
            var resultado = service.buscarActivaPorHabitacion(idHabitacion);

            if (resultado == null) {
                System.out.println("DEBUG: El servicio retornó NULL para ID: " + idHabitacion);
                return ResponseEntity.ok(ApiResponse.error("No se encontró recepción activa para esta habitación."));
            }

            System.out.println("DEBUG: Datos encontrados: " + resultado.toString());
            return ResponseEntity.ok(ApiResponse.success(resultado));

        } catch (Exception e) {
            System.err.println("DEBUG: Error en el servicio: " + e.getMessage());
            e.printStackTrace(); // Esto te dirá la línea exacta del error en Java
            return ResponseEntity.badRequest().body(ApiResponse.error("Error técnico: " + e.getMessage()));
        }
    }

    // --- OTROS ENDPOINTS ---
    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<?>> listar() {
        return ResponseEntity.ok(ApiResponse.success(service.listarTodos()));
    }

    @PostMapping("/registrar-salida")
    public ResponseEntity<ApiResponse<?>> registrarSalida(@RequestBody Map<String, Object> payload) {
        try {
            Integer idRecepcion = Integer.valueOf(payload.get("idRecepcion").toString());
            Integer idHabitacion = Integer.valueOf(payload.get("idHabitacion").toString());
            BigDecimal penalidad = new BigDecimal(payload.getOrDefault("costoPenalidad", "0").toString());
            BigDecimal total = new BigDecimal(payload.getOrDefault("totalPagado", "0").toString());

            Boolean exito = service.procesarSalida(idRecepcion, idHabitacion, penalidad, total);
            return exito ? ResponseEntity.ok(ApiResponse.success(true))
                    : ResponseEntity.badRequest().body(ApiResponse.error("Error al procesar salida."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }


    
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<?>> registrar(@Valid @RequestBody RecepcionDto dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success(service.guardar(dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ApiResponse<?>> actualizar(@PathVariable Integer id, @Valid @RequestBody RecepcionDto dto) {
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