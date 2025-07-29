package org.example.proyectofinal.mapper;

import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.entity.Producto;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    ProductoDTO toDTO(Producto producto);
    Producto toEntity(ProductoDTO dto);
    List<ProductoDTO> toDTOList(List<Producto> productos);
    List<Producto> toEntityList(List<ProductoDTO> dtos);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductoDTO dto, @MappingTarget Producto producto);
} 