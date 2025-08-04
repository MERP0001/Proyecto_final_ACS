package org.example.proyectofinal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.proyectofinal.entity.TipoMovimiento;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoHistorialDTO {
    
    private Long id;
    
    private ProductoDTO producto;
    
    private UserDTO usuario;
    
    private TipoMovimiento tipoMovimiento;
    
    private int cantidad;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fecha;
}
