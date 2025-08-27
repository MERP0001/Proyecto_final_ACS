package org.example.proyectofinal.service;

import lombok.RequiredArgsConstructor;
import org.example.proyectofinal.entity.MovimientoHistorial;
import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.entity.TipoMovimiento;
import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.repository.MovimientoHistorialRepository;
import org.example.proyectofinal.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimientoHistorialService {

    private final MovimientoHistorialRepository movimientoHistorialRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public void registrarMovimiento(Producto producto, User usuario, TipoMovimiento tipo, int cantidad) {
        MovimientoHistorial historial = MovimientoHistorial.builder()
                .producto(producto)
                .usuario(usuario)
                .tipoMovimiento(tipo)
                .cantidad(cantidad)
                .build();
        movimientoHistorialRepository.save(historial);
    }
    
    @Transactional
    public void registrarMovimiento(Long productoId, TipoMovimiento tipo, Integer cantidad, String motivo) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));
        
        // Validar stock para salidas
        if (tipo == TipoMovimiento.SALIDA && producto.getCantidadActual() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Stock actual: " + producto.getCantidadActual() + ", requerido: " + cantidad);
        }
        
        // Actualizar stock del producto
        if (tipo == TipoMovimiento.ENTRADA) {
            producto.setCantidadActual(producto.getCantidadActual() + cantidad);
        } else {
            producto.setCantidadActual(producto.getCantidadActual() - cantidad);
        }
        productoRepository.save(producto);
        
        // Crear registro de movimiento
        MovimientoHistorial historial = MovimientoHistorial.builder()
                .producto(producto)
                .tipoMovimiento(tipo)
                .cantidad(cantidad)
                .motivo(motivo)
                .fechaMovimiento(LocalDateTime.now())
                .fecha(LocalDateTime.now())
                .build();
        movimientoHistorialRepository.save(historial);
    }

    @Transactional(readOnly = true)
    public Page<MovimientoHistorial> obtenerHistorial(Pageable pageable) {
        return movimientoHistorialRepository.findAllWithProductoAndUsuario(pageable);
    }
} 