package org.example.proyectofinal.mapper;

import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.entity.Producto;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    
    /**
     * Mapear de Producto (entity) a ProductoDTO.
     * La categoría se mapea usando el nombre de la categoría.
     */
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoriaToString")
    ProductoDTO toDTO(Producto producto);
    
    /**
     * Mapear de ProductoDTO a Producto (entity).
     * La categoría se asigna como categoriaLegacy temporalmente.
     */
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "categoriaLegacy", source = "categoria")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "version", ignore = true)
    Producto toEntity(ProductoDTO dto);
    
    List<ProductoDTO> toDTOList(List<Producto> productos);
    
    List<Producto> toEntityList(List<ProductoDTO> dtos);
    
    /**
     * Actualizar entidad desde DTO.
     * Solo actualiza categoriaLegacy, no la relación categoria.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "categoriaLegacy", source = "categoria")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(ProductoDTO dto, @MappingTarget Producto producto);
    
    /**
     * Convierte Categoria entity a String (nombre de la categoría).
     */
    @Named("categoriaToString")
    default String categoriaToString(Categoria categoria) {
        if (categoria != null) {
            return categoria.getNombre();
        }
        return null;
    }
} 