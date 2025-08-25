package org.example.proyectofinal.repository;

import org.example.proyectofinal.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Categoria.
 * Proporciona métodos de consulta personalizados para la gestión de categorías.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {

    /**
     * Buscar categorías activas con paginación.
     * @param pageable información de paginación
     * @return página de categorías activas
     */
    Page<Categoria> findByActivoTrue(Pageable pageable);

    /**
     * Buscar todas las categorías activas ordenadas por nombre.
     * @return lista de categorías activas ordenadas
     */
    List<Categoria> findByActivoTrueOrderByNombre();

    /**
     * Buscar categoría por nombre (case-insensitive) y que esté activa.
     * @param nombre nombre de la categoría
     * @return categoría opcional
     */
    Optional<Categoria> findByNombreIgnoreCaseAndActivoTrue(String nombre);

    /**
     * Buscar categoría por nombre (case-insensitive).
     * @param nombre nombre de la categoría
     * @return categoría opcional
     */
    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    /**
     * Verificar si existe una categoría con el nombre especificado (excluyendo un ID específico).
     * @param nombre nombre de la categoría
     * @param id ID a excluir
     * @return true si existe otra categoría con el mismo nombre
     */
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    /**
     * Verificar si existe una categoría con el nombre especificado.
     * @param nombre nombre de la categoría
     * @return true si existe una categoría con el nombre
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Buscar categorías por nombre que contenga el texto especificado (case-insensitive).
     * @param nombre texto a buscar en el nombre
     * @param pageable información de paginación
     * @return página de categorías que coinciden
     */
    Page<Categoria> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);

    /**
     * Buscar categorías por descripción que contenga el texto especificado (case-insensitive).
     * @param descripcion texto a buscar en la descripción
     * @param pageable información de paginación
     * @return página de categorías que coinciden
     */
    Page<Categoria> findByDescripcionContainingIgnoreCaseAndActivoTrue(String descripcion, Pageable pageable);

    /**
     * Contar productos activos asociados a una categoría.
     * @param categoriaId ID de la categoría
     * @return cantidad de productos activos
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoria.id = :categoriaId AND p.activo = true")
    Long countProductosActivosByCategoria(@Param("categoriaId") Long categoriaId);

    /**
     * Verificar si una categoría tiene productos activos asociados.
     * @param categoriaId ID de la categoría
     * @return true si tiene productos activos
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Producto p WHERE p.categoria.id = :categoriaId AND p.activo = true")
    boolean tieneProductosActivos(@Param("categoriaId") Long categoriaId);

    /**
     * Obtener categorías con la cantidad de productos activos.
     * @return lista de arrays con [categoria, cantidadProductos]
     */
    @Query("SELECT c, COUNT(p) FROM Categoria c LEFT JOIN Producto p ON c.id = p.categoria.id AND p.activo = true WHERE c.activo = true GROUP BY c ORDER BY c.nombre")
    List<Object[]> findCategoriasConCantidadProductos();

    /**
     * Buscar categorías que no tienen productos asociados.
     * @return lista de categorías sin productos
     */
    @Query("SELECT c FROM Categoria c WHERE c.activo = true AND c.id NOT IN (SELECT DISTINCT p.categoria.id FROM Producto p WHERE p.activo = true)")
    List<Categoria> findCategoriasSinProductos();

    /**
     * Obtener las categorías más utilizadas (con más productos).
     * @param limite cantidad máxima de categorías a retornar
     * @return lista de categorías ordenadas por cantidad de productos (descendente)
     */
    @Query("SELECT c FROM Categoria c LEFT JOIN Producto p ON c.id = p.categoria.id AND p.activo = true WHERE c.activo = true GROUP BY c ORDER BY COUNT(p) DESC")
    Page<Categoria> findCategoriasMasUtilizadas(Pageable pageable);

    /**
     * Buscar categorías por múltiples criterios con consulta nativa para casos complejos.
     * @param nombre nombre a buscar (puede ser null)
     * @param descripcion descripción a buscar (puede ser null)
     * @param activo estado activo (puede ser null)
     * @param pageable información de paginación
     * @return página de categorías que coinciden
     */
    @Query("SELECT c FROM Categoria c WHERE " +
           "(:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:descripcion IS NULL OR LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))) AND " +
           "(:activo IS NULL OR c.activo = :activo)")
    Page<Categoria> findByMultiplesCriterios(
            @Param("nombre") String nombre,
            @Param("descripcion") String descripcion,
            @Param("activo") Boolean activo,
            Pageable pageable
    );
}
