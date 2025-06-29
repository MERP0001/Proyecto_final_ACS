package org.example.proyectofinal.mapper;

import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.entity.Producto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversiones entre entidad Producto y ProductoDTO.
 * Centraliza la lógica de mapeo para mantener consistencia.
 */
@Component
public class ProductoMapper {

    /**
     * Convierte una entidad Producto a ProductoDTO.
     * @param producto entidad Producto
     * @return ProductoDTO correspondiente
     */
    public ProductoDTO toDTO(Producto producto) {
        if (producto == null) {
            return null;
        }

        return ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .categoria(producto.getCategoria())
                .precio(producto.getPrecio())
                .cantidadInicial(producto.getCantidadInicial())
                .cantidadActual(producto.getCantidadActual())
                .activo(producto.getActivo())
                .unidadMedida(producto.getUnidadMedida())
                .sku(producto.getSku())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaModificacion(producto.getFechaModificacion())
                .version(producto.getVersion())
                .build();
    }

    /**
     * Convierte un ProductoDTO a entidad Producto (para crear nuevo).
     * @param dto ProductoDTO
     * @return entidad Producto
     */
    public Producto toEntity(ProductoDTO dto) {
        if (dto == null) {
            return null;
        }

        return Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .categoria(dto.getCategoria())
                .precio(dto.getPrecio())
                .cantidadInicial(dto.getCantidadInicial())
                .cantidadActual(dto.getCantidadActual() != null ? dto.getCantidadActual() : dto.getCantidadInicial())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .unidadMedida(dto.getUnidadMedida() != null ? dto.getUnidadMedida() : "UNIDAD")
                .sku(dto.getSku())
                .build();
    }

    /**
     * Actualiza una entidad Producto existente con datos del ProductoDTO.
     * Preserva campos que no deben ser modificados directamente.
     * @param producto entidad existente
     * @param dto DTO con nuevos datos
     * @return entidad actualizada
     */
    public Producto updateEntity(Producto producto, ProductoDTO dto) {
        if (producto == null || dto == null) {
            return producto;
        }

        // Actualizar solo campos modificables
        if (dto.getNombre() != null) {
            producto.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) {
            producto.setDescripcion(dto.getDescripcion());
        }
        if (dto.getCategoria() != null) {
            producto.setCategoria(dto.getCategoria());
        }
        if (dto.getPrecio() != null) {
            producto.setPrecio(dto.getPrecio());
        }
        if (dto.getCantidadActual() != null) {
            producto.setCantidadActual(dto.getCantidadActual());
        }
        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }
        if (dto.getUnidadMedida() != null) {
            producto.setUnidadMedida(dto.getUnidadMedida());
        }
        if (dto.getSku() != null) {
            producto.setSku(dto.getSku());
        }

        // Nota: No actualizamos cantidadInicial, fechaCreacion, fechaModificacion, version
        // ya que son controlados automáticamente por JPA/Hibernate

        return producto;
    }

    /**
     * Convierte una lista de entidades Producto a lista de ProductoDTO.
     * @param productos lista de entidades
     * @return lista de DTOs
     */
    public List<ProductoDTO> toDTOList(List<Producto> productos) {
        if (productos == null) {
            return null;
        }

        return productos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de ProductoDTO a lista de entidades Producto.
     * @param dtos lista de DTOs
     * @return lista de entidades
     */
    public List<Producto> toEntityList(List<ProductoDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 