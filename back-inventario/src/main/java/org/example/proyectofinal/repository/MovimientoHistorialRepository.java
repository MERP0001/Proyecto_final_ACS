package org.example.proyectofinal.repository;

import org.example.proyectofinal.entity.MovimientoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoHistorialRepository extends JpaRepository<MovimientoHistorial, Long> {
} 