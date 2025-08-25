package org.example.proyectofinal.mapper;

import org.example.proyectofinal.dto.CategoriaCreateDTO;
import org.example.proyectofinal.dto.CategoriaDTO;
import org.example.proyectofinal.dto.CategoriaUpdateDTO;
import org.example.proyectofinal.entity.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper para conversión entre entidades Categoria y DTOs.
 * Utiliza MapStruct para generación automática de código.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoriaMapper {

    /**
     * Convierte una entidad Categoria a DTO.
     *
     * @param categoria entidad a convertir
     * @return DTO correspondiente
     */
    @Mapping(target = "cantidadProductos", ignore = true)
    CategoriaDTO toDTO(Categoria categoria);

    /**
     * Convierte un DTO a entidad Categoria.
     *
     * @param categoriaDTO DTO a convertir
     * @return entidad correspondiente
     */
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "version", ignore = true)
    Categoria toEntity(CategoriaDTO categoriaDTO);

    /**
     * Convierte una lista de entidades a lista de DTOs.
     *
     * @param categorias lista de entidades
     * @return lista de DTOs
     */
    List<CategoriaDTO> toDTOList(List<Categoria> categorias);

    /**
     * Convierte una lista de DTOs a lista de entidades.
     *
     * @param categoriaDTOs lista de DTOs
     * @return lista de entidades
     */
    List<Categoria> toEntityList(List<CategoriaDTO> categoriaDTOs);

    /**
     * Actualiza una entidad existente con datos del DTO.
     * Útil para operaciones de actualización parcial.
     *
     * @param categoriaDTO DTO con datos a actualizar
     * @param categoria entidad existente a actualizar
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDTO(CategoriaDTO categoriaDTO, @MappingTarget Categoria categoria);

    /**
     * Convierte CategoriaCreateDTO a entidad Categoria.
     * 
     * @param createDTO DTO con datos para crear
     * @return nueva entidad Categoria
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "version", ignore = true)
    Categoria toEntity(CategoriaCreateDTO createDTO);

    /**
     * Convierte CategoriaUpdateDTO a entidad Categoria.
     * 
     * @param updateDTO DTO con datos para actualizar
     * @return entidad Categoria
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "version", ignore = true)
    Categoria toEntity(CategoriaUpdateDTO updateDTO);
}
