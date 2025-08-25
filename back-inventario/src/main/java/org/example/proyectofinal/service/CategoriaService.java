package org.example.proyectofinal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.exception.CategoriaAlreadyExistsException;
import org.example.proyectofinal.exception.CategoriaNotFoundException;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.example.proyectofinal.filter.CategoriaFilter;
import org.example.proyectofinal.repository.CategoriaRepository;
import org.example.proyectofinal.specification.CategoriaSpecification;
import org.example.proyectofinal.validator.CategoriaValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Servicio para la gestión de categorías.
 * Contiene toda la lógica de negocio para operaciones CRUD y validaciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaValidator categoriaValidator;

    /**
     * Crear una nueva categoría.
     * @param categoria datos de la categoría a crear
     * @return Categoria creada
     */
    @Transactional
    public Categoria crearCategoria(Categoria categoria) {
        log.info("Creando nueva categoría: {}", categoria.getNombre());
        
        // Validar datos de entrada
        categoriaValidator.validar(categoria);
        
        // Verificar que no exista una categoría con el mismo nombre
        if (StringUtils.hasText(categoria.getNombre()) &&
            categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw CategoriaAlreadyExistsException.porNombre(categoria.getNombre());
        }
        
        // Guardar categoría
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        log.info("Categoría creada exitosamente con ID: {}", categoriaGuardada.getId());
        
        return categoriaGuardada;
    }

    /**
     * Obtener categoría por ID.
     * @param id ID de la categoría
     * @return Categoria encontrada
     */
    public Categoria obtenerCategoriaPorId(Long id) {
        log.debug("Buscando categoría con ID: {}", id);
        return categoriaRepository.findById(id)
                .orElseThrow(() -> CategoriaNotFoundException.porId(id));
    }

    /**
     * Obtener categoría por nombre.
     * @param nombre nombre de la categoría
     * @return Categoria encontrada
     */
    public Categoria obtenerCategoriaPorNombre(String nombre) {
        log.debug("Buscando categoría con nombre: {}", nombre);
        return categoriaRepository.findByNombreIgnoreCaseAndActivoTrue(nombre)
                .orElseThrow(() -> CategoriaNotFoundException.porNombre(nombre));
    }

    /**
     * Listar todas las categorías activas con paginación.
     * @param pageable información de paginación
     * @return página de categorías
     */
    public Page<Categoria> listarCategoriasActivas(Pageable pageable) {
        log.debug("Listando categorías activas");
        return categoriaRepository.findByActivoTrue(pageable);
    }

    /**
     * Obtener todas las categorías activas ordenadas por nombre.
     * @return lista de categorías activas
     */
    public List<Categoria> obtenerCategoriasActivas() {
        log.debug("Obteniendo todas las categorías activas");
        return categoriaRepository.findByActivoTrueOrderByNombre();
    }

    /**
     * Buscar categorías por múltiples criterios.
     * @param filter filtros para la búsqueda
     * @param pageable información de paginación
     * @return página de categorías que coinciden con los criterios
     */
    public Page<Categoria> buscarCategorias(CategoriaFilter filter, Pageable pageable) {
        log.debug("Buscando categorías con filtros dinámicos");
        
        if (filter == null) {
            return categoriaRepository.findByActivoTrue(pageable);
        }
        
        var spec = CategoriaSpecification.conFiltros(filter);
        return categoriaRepository.findAll(spec, pageable);
    }

    /**
     * Actualizar una categoría existente.
     * @param id ID de la categoría a actualizar
     * @param categoriaActualizada datos actualizados de la categoría
     * @return Categoria actualizada
     */
    @Transactional
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        log.info("Actualizando categoría con ID: {}", id);
        
        // Verificar que la categoría existe
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> CategoriaNotFoundException.porId(id));
        
        // Validar datos actualizados
        categoriaValidator.validar(categoriaActualizada);
        
        // Verificar que no exista otra categoría con el mismo nombre
        if (StringUtils.hasText(categoriaActualizada.getNombre()) &&
            !categoriaActualizada.getNombre().equalsIgnoreCase(categoriaExistente.getNombre()) &&
            categoriaRepository.existsByNombreIgnoreCaseAndIdNot(categoriaActualizada.getNombre(), id)) {
            throw CategoriaAlreadyExistsException.porNombre(categoriaActualizada.getNombre());
        }
        
        // Actualizar campos
        categoriaExistente.setNombre(categoriaActualizada.getNombre());
        categoriaExistente.setDescripcion(categoriaActualizada.getDescripcion());
        categoriaExistente.setActivo(categoriaActualizada.getActivo());
        
        // Guardar cambios
        Categoria categoriaGuardada = categoriaRepository.save(categoriaExistente);
        log.info("Categoría actualizada exitosamente");
        
        return categoriaGuardada;
    }

    /**
     * Eliminar una categoría (soft delete).
     * Una categoría no puede ser eliminada si tiene productos activos asociados.
     * @param id ID de la categoría a eliminar
     */
    @Transactional
    public void eliminarCategoria(Long id) {
        log.info("Eliminando categoría con ID: {}", id);
        
        // Verificar que la categoría existe
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> CategoriaNotFoundException.porId(id));
        
        // Verificar si tiene productos activos asociados
        boolean tieneProductosActivos = categoriaRepository.tieneProductosActivos(id);
        
        // Validar que se puede eliminar
        categoriaValidator.validarEliminacion(categoria, tieneProductosActivos);
        
        // Realizar soft delete
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
        
        log.info("Categoría eliminada exitosamente (soft delete)");
    }

    /**
     * Activar o desactivar una categoría.
     * @param id ID de la categoría
     * @param activo nuevo estado
     * @return Categoria actualizada
     */
    @Transactional
    public Categoria cambiarEstadoCategoria(Long id, boolean activo) {
        log.info("Cambiando estado de categoría ID: {} a {}", id, activo ? "activa" : "inactiva");
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> CategoriaNotFoundException.porId(id));
        
        if (!activo) {
            // Si se está desactivando, verificar que no tenga productos activos
            boolean tieneProductosActivos = categoriaRepository.tieneProductosActivos(id);
            if (tieneProductosActivos) {
                throw new BusinessValidationException(
                    "No se puede desactivar la categoría '" + categoria.getNombre() + 
                    "' porque tiene productos activos asociados"
                );
            }
        }
        
        categoria.setActivo(activo);
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        
        log.info("Estado de categoría cambiado exitosamente");
        return categoriaGuardada;
    }

    /**
     * Obtener la cantidad de productos activos asociados a una categoría.
     * @param categoriaId ID de la categoría
     * @return cantidad de productos activos
     */
    public Long contarProductosPorCategoria(Long categoriaId) {
        log.debug("Contando productos para categoría ID: {}", categoriaId);
        return categoriaRepository.countProductosActivosByCategoria(categoriaId);
    }

    /**
     * Obtener categorías con la cantidad de productos asociados.
     * @return lista de categorías con conteo de productos
     */
    public List<Object[]> obtenerCategoriasConCantidadProductos() {
        log.debug("Obteniendo categorías con cantidad de productos");
        return categoriaRepository.findCategoriasConCantidadProductos();
    }

    /**
     * Obtener categorías sin productos asociados.
     * @return lista de categorías sin productos
     */
    public List<Categoria> obtenerCategoriasSinProductos() {
        log.debug("Obteniendo categorías sin productos");
        return categoriaRepository.findCategoriasSinProductos();
    }

    /**
     * Obtener las categorías más utilizadas.
     * @param pageable información de paginación
     * @return página de categorías ordenadas por uso
     */
    public Page<Categoria> obtenerCategoriasMasUtilizadas(Pageable pageable) {
        log.debug("Obteniendo categorías más utilizadas");
        return categoriaRepository.findCategoriasMasUtilizadas(pageable);
    }

    /**
     * Verificar si existe una categoría por nombre.
     * @param nombre nombre a verificar
     * @return true si existe
     */
    public boolean existeCategoriaPorNombre(String nombre) {
        return categoriaRepository.existsByNombreIgnoreCase(nombre);
    }

    /**
     * Buscar categorías por nombre que contenga el texto especificado.
     * @param nombre texto a buscar
     * @param pageable información de paginación
     * @return página de categorías que coinciden
     */
    public Page<Categoria> buscarPorNombre(String nombre, Pageable pageable) {
        log.debug("Buscando categorías por nombre: {}", nombre);
        return categoriaRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable);
    }
}
