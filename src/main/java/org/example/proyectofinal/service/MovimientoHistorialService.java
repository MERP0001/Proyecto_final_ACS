package org.example.proyectofinal.service;

import lombok.RequiredArgsConstructor;
import org.example.proyectofinal.entity.MovimientoHistorial;
import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.entity.TipoMovimiento;
import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.repository.MovimientoHistorialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class MovimientoHistorialService {

    private final MovimientoHistorialRepository movimientoHistorialRepository;

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

    @Transactional(readOnly = true)
    public Page<MovimientoHistorial> obtenerHistorial(Pageable pageable) {
        return movimientoHistorialRepository.findAll(pageable);
    }
} 