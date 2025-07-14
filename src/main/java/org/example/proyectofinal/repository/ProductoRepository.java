package org.example.proyectofinal.repository;

import org.example.proyectofinal.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Producto.
 * Proporciona métodos de consulta personalizados para el sistema de inventarios.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    /**
     * Buscar productos activos.
     * @param pageable información de paginación
     * @return página de productos activos
     */
    Page<Producto> findByActivoTrue(Pageable pageable);

    /**
     * Buscar productos por nombre (búsqueda parcial e insensible a mayúsculas).
     * @param nombre nombre a buscar
     * @param pageable información de paginación
     * @return página de productos que coinciden con el nombre
     */
    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);

    /**
     * Buscar productos por categoría.
     * @param categoria categoría a buscar
     * @param pageable información de paginación
     * @return página de productos de la categoría especificada
     */
    Page<Producto> findByCategoriaIgnoreCaseAndActivoTrue(String categoria, Pageable pageable);

    /**
     * Buscar productos por rango de precios.
     * @param precioMin precio mínimo
     * @param precioMax precio máximo
     * @param pageable información de paginación
     * @return página de productos en el rango de precios
     */
    Page<Producto> findByPrecioBetweenAndActivoTrue(BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);

    /**
     * Buscar producto por SKU.
     * @param sku código SKU
     * @return producto opcional
     */
    Optional<Producto> findBySkuAndActivoTrue(String sku);

    /**
     * Verificar si existe un producto con el SKU especificado (excluyendo un ID específico).
     * @param sku código SKU
     * @param id ID a excluir
     * @return true si existe otro producto con el mismo SKU
     */
    boolean existsBySkuAndIdNot(String sku, Long id);

    /**
     * Verificar si existe un producto con el SKU especificado.
     * @param sku código SKU
     * @return true si existe un producto con el SKU
     */
    boolean existsBySku(String sku);

    /**
     * Buscar productos con stock bajo (cantidad actual menor al mínimo especificado).
     * @param cantidadMinima cantidad mínima de stock
     * @return lista de productos con stock bajo
     */
    @Query("SELECT p FROM Producto p WHERE p.cantidadActual < :cantidadMinima AND p.activo = true")
    List<Producto> findProductosConStockBajo(@Param("cantidadMinima") Integer cantidadMinima);

    /**
     * Buscar productos sin stock.
     * @return lista de productos sin stock
     */
    @Query("SELECT p FROM Producto p WHERE p.cantidadActual = 0 AND p.activo = true")
    List<Producto> findProductosSinStock();

    /**
     * Buscar productos por múltiples criterios.
     * @param nombre nombre a buscar (puede ser null)
     * @param categoria categoría a buscar (puede ser null)
     * @param precioMin precio mínimo (puede ser null)
     * @param precioMax precio máximo (puede ser null)
     * @param pageable información de paginación
     * @return página de productos que coinciden con los criterios
     */
    @Query("SELECT p FROM Producto p WHERE " +
           "p.activo = true AND " +
           "(:nombre IS NULL OR p.nombre LIKE CONCAT('%', :nombre, '%')) AND " +
           "(:categoria IS NULL OR p.categoria = :categoria) AND " +
           "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
           "(:precioMax IS NULL OR p.precio <= :precioMax)")
    Page<Producto> findByMultiplesCriterios(
            @Param("nombre") String nombre,
            @Param("categoria") String categoria,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            Pageable pageable
    );

    /**
     * Obtener todas las categorías únicas de productos activos.
     * @return lista de categorías únicas
     */
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.activo = true ORDER BY p.categoria")
    List<String> findDistinctCategorias();

    /**
     * Contar productos por categoría.
     * @return lista de arrays con [categoria, cantidad]
     */
    @Query("SELECT p.categoria, COUNT(p) FROM Producto p WHERE p.activo = true GROUP BY p.categoria")
    List<Object[]> countProductosPorCategoria();

    /**
     * Obtener valor total del inventario.
     * @return valor total del inventario
     */
    @Query("SELECT COALESCE(SUM(p.precio * p.cantidadActual), 0) FROM Producto p WHERE p.activo = true")
    BigDecimal calcularValorTotalInventario();

    /**
     * Obtener productos más vendidos (simulado por cantidad inicial - cantidad actual).
     * @param limit límite de resultados
     * @return lista de productos más vendidos
     */
    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "ORDER BY (p.cantidadInicial - p.cantidadActual) DESC")
    List<Producto> findProductosMasVendidos(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.sku = :sku")
    Optional<Producto> findBySku(@Param("sku") String sku);
} 