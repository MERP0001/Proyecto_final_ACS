package org.example.proyectofinal.repository;

import org.example.proyectofinal.entity.MovimientoHistorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoHistorialRepository extends JpaRepository<MovimientoHistorial, Long> {
    
    @Query("SELECT m FROM MovimientoHistorial m " +
           "LEFT JOIN FETCH m.producto " +
           "LEFT JOIN FETCH m.usuario " +
           "ORDER BY m.fecha DESC")
    Page<MovimientoHistorial> findAllWithProductoAndUsuario(Pageable pageable);
    
    /**
     * Buscar movimientos por producto ordenados por fecha descendente
     */
    List<MovimientoHistorial> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);
    
    /**
     * Buscar movimientos entre fechas
     */
    List<MovimientoHistorial> findByFechaMovimientoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Buscar movimientos por producto entre fechas
     */
    List<MovimientoHistorial> findByProductoIdAndFechaMovimientoBetween(
        Long productoId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
} 