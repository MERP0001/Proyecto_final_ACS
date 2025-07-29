package org.example.proyectofinal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.proyectofinal.entity.MovimientoHistorial;
import org.example.proyectofinal.service.MovimientoHistorialService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
@Tag(name = "Gestión de Historial", description = "Operaciones para consultar el historial de movimientos de inventario.")
public class MovimientoHistorialController {

    private final MovimientoHistorialService movimientoHistorialService;

    @GetMapping
    @Operation(
        summary = "Obtener historial de movimientos",
        description = "Recupera una lista paginada de todos los movimientos de entrada y salida de productos."
    )
    @ApiResponse(responseCode = "200", description = "Historial recuperado exitosamente.")
    public ResponseEntity<Page<MovimientoHistorial>> obtenerHistorial(@ParameterObject Pageable pageable) {
        Page<MovimientoHistorial> historial = movimientoHistorialService.obtenerHistorial(pageable);
        return ResponseEntity.ok(historial);
    }
} 