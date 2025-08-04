package org.example.proyectofinal.repository;

import org.example.proyectofinal.entity.MovimientoHistorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoHistorialRepository extends JpaRepository<MovimientoHistorial, Long> {
    
    @Query("SELECT m FROM MovimientoHistorial m " +
           "LEFT JOIN FETCH m.producto " +
           "LEFT JOIN FETCH m.usuario " +
           "ORDER BY m.fecha DESC")
    Page<MovimientoHistorial> findAllWithProductoAndUsuario(Pageable pageable);
} 