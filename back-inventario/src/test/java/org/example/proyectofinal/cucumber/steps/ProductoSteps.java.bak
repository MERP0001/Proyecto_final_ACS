package org.example.proyectofinal.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.exception.ProductoNotFoundException;
import org.example.proyectofinal.mapper.ProductoMapper;
import org.example.proyectofinal.repository.CategoriaRepository;
import org.example.proyectofinal.repository.ProductoRepository;
import org.example.proyectofinal.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps definition para las pruebas de Cucumber relacionadas con la gestión de productos.
 * Incluye todas las operaciones CRUD y validaciones de productos.
 */
public class ProductoSteps {

    private static final Logger log = LoggerFactory.getLogger(ProductoSteps.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoMapper productoMapper;

    // Variables para almacenar datos entre steps
    private Producto productoActual;
    private ProductoDTO productoDTOActual;
    private List<Producto> listaProductos;
    private Page<Producto> paginaProductos;
    private Exception excepcionLanzada;
    private String resultadoOperacion;
    private Long idProductoCreado;

    // ==================== PASOS DADOS (GIVEN) ====================

    @Dado("que existen productos en el sistema")
    public void que_existen_productos_en_el_sistema() {
        log.info("Verificando que existen productos en el sistema");
        
        // Crear categoría de prueba si no existe
        Categoria categoria = categoriaRepository.findByNombreIgnoreCase("Monitores")
                .orElse(Categoria.builder()
                        .nombre("Monitores")
                        .descripcion("Monitores y pantallas")
                        .activo(true)
                        .build());
        if (categoria.getId() == null) {
            categoria = categoriaRepository.save(categoria);
        }

        // Crear productos de prueba si no existen
        crearProductosDePrueba(categoria);
        
        long cantidadProductos = productoRepository.count();
        assertTrue(cantidadProductos > 0, "Debe haber al menos un producto en el sistema");
        log.info("Cantidad de productos en el sistema: {}", cantidadProductos);
    }

    @Dado("que existe un producto con SKU {string}")
    public void que_existe_un_producto_con_sku(String sku) {
        log.info("Verificando que existe un producto con SKU: {}", sku);
        
        Optional<Producto> productoOpt = productoRepository.findBySku(sku);
        if (productoOpt.isEmpty()) {
            // Crear el producto si no existe
            Categoria categoria = categoriaRepository.findByNombreIgnoreCase("Monitores")
                    .orElse(categoriaRepository.save(Categoria.builder()
                            .nombre("Monitores")
                            .descripcion("Monitores y pantallas")
                            .activo(true)
                            .build()));

            Producto producto = Producto.builder()
                    .nombre("Monitor Samsung 32\"")
                    .descripcion("Monitor 4K para gaming")
                    .categoria(categoria)
                    .precio(new BigDecimal("499.99"))
                    .cantidadInicial(15)
                    .cantidadActual(15)
                    .sku(sku)
                    .activo(true)
                    .build();
            
            productoActual = productoRepository.save(producto);
        } else {
            productoActual = productoOpt.get();
        }
        
        assertNotNull(productoActual, "El producto debe existir");
        assertEquals(sku, productoActual.getSku(), "El SKU debe coincidir");
        log.info("Producto encontrado: {} con ID: {}", productoActual.getNombre(), productoActual.getId());
    }

    // ==================== PASOS CUANDO (WHEN) ====================

    @Cuando("creo un producto con los siguientes datos:")
    public void creo_un_producto_con_los_siguientes_datos(DataTable dataTable) {
        log.info("Creando producto con datos de la tabla");
        
        try {
            Map<String, String> datos = dataTable.asMap();
            
            // Buscar o crear la categoría
            String nombreCategoria = datos.get("categoria");
            Categoria categoria = categoriaRepository.findByNombreIgnoreCase(nombreCategoria)
                    .orElse(categoriaRepository.save(Categoria.builder()
                            .nombre(nombreCategoria)
                            .descripcion("Categoría para " + nombreCategoria)
                            .activo(true)
                            .build()));

            // Crear el producto
            Producto nuevoProducto = Producto.builder()
                    .nombre(datos.get("nombre"))
                    .descripcion(datos.get("descripcion"))
                    .categoria(categoria)
                    .precio(new BigDecimal(datos.get("precio")))
                    .cantidadInicial(Integer.parseInt(datos.get("cantidad")))
                    .cantidadActual(Integer.parseInt(datos.get("cantidad")))
                    .sku(datos.get("sku"))
                    .activo(true)
                    .build();

            productoActual = productoService.crearProducto(nuevoProducto);
            idProductoCreado = productoActual.getId();
            resultadoOperacion = "exito";
            
            log.info("Producto creado exitosamente con ID: {}", productoActual.getId());
            
        } catch (Exception e) {
            log.error("Error al crear producto: {}", e.getMessage());
            excepcionLanzada = e;
            resultadoOperacion = "error";
        }
    }

    @Cuando("busco productos con nombre {string}")
    public void busco_productos_con_nombre(String nombre) {
        log.info("Buscando productos con nombre: {}", nombre);
        
        try {
            // Buscar productos que contengan el nombre en su título
            Page<Producto> productosPage = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, PageRequest.of(0, 100));
            listaProductos = productosPage.getContent();
            log.info("Encontrados {} productos", listaProductos.size());
            
        } catch (Exception e) {
            log.error("Error al buscar productos: {}", e.getMessage());
            excepcionLanzada = e;
        }
    }

    @Cuando("actualizo el precio a {double}")
    public void actualizo_el_precio_a(Double nuevoPrecio) {
        log.info("Actualizando precio del producto a: {}", nuevoPrecio);
        
        try {
            assertNotNull(productoActual, "Debe existir un producto para actualizar");
            
            productoActual.setPrecio(new BigDecimal(nuevoPrecio.toString()));
            productoActual = productoService.actualizarProducto(productoActual.getId(), productoActual);
            
            log.info("Precio actualizado exitosamente a: {}", nuevoPrecio);
            
        } catch (Exception e) {
            log.error("Error al actualizar precio: {}", e.getMessage());
            excepcionLanzada = e;
        }
    }

    @Cuando("elimino el producto")
    public void elimino_el_producto() {
        log.info("Eliminando producto con ID: {}", productoActual.getId());
        
        try {
            productoService.eliminarProducto(productoActual.getId());
            log.info("Producto eliminado exitosamente");
            
        } catch (Exception e) {
            log.error("Error al eliminar producto: {}", e.getMessage());
            excepcionLanzada = e;
        }
    }

    @Cuando("intento crear un producto con datos:")
    public void intento_crear_un_producto_con_datos(DataTable dataTable) {
        log.info("Intentando crear producto con datos de validación");
        
        try {
            Map<String, String> datos = dataTable.asMap();
            
            // Crear el producto con datos que pueden ser inválidos
            Producto nuevoProducto = Producto.builder()
                    .nombre(datos.get("nombre"))
                    .descripcion(datos.get("descripcion"))
                    .precio(datos.get("precio").isEmpty() ? null : new BigDecimal(datos.get("precio")))
                    .cantidadInicial(datos.get("cantidad").isEmpty() ? null : Integer.parseInt(datos.get("cantidad")))
                    .activo(true)
                    .build();

            // Si la cantidad es negativa, establecerla
            String cantidadStr = datos.get("cantidad");
            if (!cantidadStr.isEmpty() && Integer.parseInt(cantidadStr) < 0) {
                nuevoProducto.setCantidadInicial(Integer.parseInt(cantidadStr));
            }

            productoActual = productoService.crearProducto(nuevoProducto);
            resultadoOperacion = "exito";
            
        } catch (Exception e) {
            log.warn("Error esperado al crear producto: {}", e.getMessage());
            excepcionLanzada = e;
            resultadoOperacion = "error";
        }
    }

    // ==================== PASOS ENTONCES (THEN) ====================

    @Entonces("el producto debe ser creado exitosamente")
    public void el_producto_debe_ser_creado_exitosamente() {
        log.info("Verificando que el producto fue creado exitosamente");
        
        assertNotNull(productoActual, "El producto debe haber sido creado");
        assertNotNull(productoActual.getId(), "El producto debe tener un ID asignado");
        assertTrue(productoActual.getActivo(), "El producto debe estar activo");
        
        log.info("Producto creado correctamente con ID: {}", productoActual.getId());
    }

    @Entonces("debe tener un ID asignado")
    public void debe_tener_un_id_asignado() {
        log.info("Verificando que el producto tiene un ID asignado");
        
        assertNotNull(productoActual, "El producto debe existir");
        assertNotNull(productoActual.getId(), "El producto debe tener un ID");
        assertTrue(productoActual.getId() > 0, "El ID debe ser mayor que 0");
        
        log.info("ID asignado correctamente: {}", productoActual.getId());
    }

    @Entonces("los datos del producto deben coincidir con los ingresados")
    public void los_datos_del_producto_deben_coincidir_con_los_ingresados() {
        log.info("Verificando que los datos del producto coinciden");
        
        assertNotNull(productoActual, "El producto debe existir");
        
        // Verificar que los datos básicos están presentes
        assertNotNull(productoActual.getNombre(), "El nombre no debe ser nulo");
        assertNotNull(productoActual.getPrecio(), "El precio no debe ser nulo");
        assertNotNull(productoActual.getCantidadInicial(), "La cantidad inicial no debe ser nula");
        
        assertTrue(productoActual.getPrecio().compareTo(BigDecimal.ZERO) > 0, "El precio debe ser mayor que 0");
        assertTrue(productoActual.getCantidadInicial() >= 0, "La cantidad debe ser mayor o igual que 0");
        
        log.info("Datos del producto verificados correctamente");
    }

    @Entonces("debo obtener una lista de productos")
    public void debo_obtener_una_lista_de_productos() {
        log.info("Verificando que se obtuvo una lista de productos");
        
        assertNotNull(listaProductos, "La lista de productos no debe ser nula");
        assertFalse(listaProductos.isEmpty(), "La lista de productos no debe estar vacía");
        
        log.info("Lista de productos obtenida con {} elementos", listaProductos.size());
    }

    @Entonces("todos los productos deben contener {string} en su nombre")
    public void todos_los_productos_deben_contener_en_su_nombre(String texto) {
        log.info("Verificando que todos los productos contienen '{}' en su nombre", texto);
        
        assertNotNull(listaProductos, "La lista de productos no debe ser nula");
        
        for (Producto producto : listaProductos) {
            assertTrue(producto.getNombre().toLowerCase().contains(texto.toLowerCase()),
                    "El producto '" + producto.getNombre() + "' debe contener '" + texto + "'");
        }
        
        log.info("Verificación completada. Todos los productos contienen el texto buscado");
    }

    @Entonces("el producto debe ser actualizado exitosamente")
    public void el_producto_debe_ser_actualizado_exitosamente() {
        log.info("Verificando que el producto fue actualizado exitosamente");
        
        assertNotNull(productoActual, "El producto debe existir");
        assertNull(excepcionLanzada, "No debe haberse lanzado ninguna excepción");
        
        log.info("Producto actualizado correctamente");
    }

    @Entonces("el nuevo precio debe ser {double}")
    public void el_nuevo_precio_debe_ser(Double precioEsperado) {
        log.info("Verificando que el nuevo precio es: {}", precioEsperado);
        
        assertNotNull(productoActual, "El producto debe existir");
        assertNotNull(productoActual.getPrecio(), "El precio no debe ser nulo");
        
        BigDecimal precioEsperadoBD = new BigDecimal(precioEsperado.toString());
        assertEquals(0, productoActual.getPrecio().compareTo(precioEsperadoBD),
                "El precio actual debe ser " + precioEsperado);
        
        log.info("Precio verificado correctamente: {}", productoActual.getPrecio());
    }

    @Entonces("el producto debe ser marcado como inactivo")
    public void el_producto_debe_ser_marcado_como_inactivo() {
        log.info("Verificando que el producto fue marcado como inactivo");
        
        assertNotNull(productoActual, "El producto debe existir");
        
        // Refrescar el producto desde la base de datos
        Optional<Producto> productoActualizadoOpt = productoRepository.findById(productoActual.getId());
        assertTrue(productoActualizadoOpt.isPresent(), "El producto debe existir en la base de datos");
        
        Producto productoActualizado = productoActualizadoOpt.get();
        assertFalse(productoActualizado.getActivo(), "El producto debe estar marcado como inactivo");
        
        log.info("Producto marcado como inactivo correctamente");
    }

    @Entonces("no debe aparecer en las búsquedas regulares")
    public void no_debe_aparecer_en_las_busquedas_regulares() {
        log.info("Verificando que el producto no aparece en búsquedas regulares");
        
        assertNotNull(productoActual, "El producto debe existir");
        
        // Buscar productos activos
        Page<Producto> productosActivosPage = productoRepository.findByActivoTrue(PageRequest.of(0, 100));
        List<Producto> productosActivos = productosActivosPage.getContent();
        
        // Verificar que el producto eliminado no está en la lista
        boolean productoEncontrado = productosActivos.stream()
                .anyMatch(p -> p.getId().equals(productoActual.getId()));
        
        assertFalse(productoEncontrado, "El producto eliminado no debe aparecer en búsquedas de productos activos");
        
        log.info("Verificación completada. El producto no aparece en búsquedas regulares");
    }

    @Entonces("debería obtener el resultado: {string}")
    public void deberia_obtener_el_resultado(String resultadoEsperado) {
        log.info("Verificando resultado esperado: {}", resultadoEsperado);
        
        assertEquals(resultadoEsperado, resultadoOperacion,
                "El resultado debe ser " + resultadoEsperado);
        
        if ("error".equals(resultadoEsperado)) {
            assertNotNull(excepcionLanzada, "Debe haberse lanzado una excepción para el resultado 'error'");
            log.info("Error esperado confirmado: {}", excepcionLanzada.getMessage());
        } else {
            assertNull(excepcionLanzada, "No debe haberse lanzado ninguna excepción para el resultado 'exito'");
            log.info("Éxito confirmado");
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void crearProductosDePrueba(Categoria categoria) {
        log.info("Creando productos de prueba");
        
        // Verificar si ya existen productos de prueba
        Page<Producto> productosExistentes = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue("Monitor", PageRequest.of(0, 10));
        if (productosExistentes.isEmpty()) {
            
            Producto monitor1 = Producto.builder()
                    .nombre("Monitor Samsung 24\"")
                    .descripcion("Monitor Full HD para oficina")
                    .categoria(categoria)
                    .precio(new BigDecimal("299.99"))
                    .cantidadInicial(20)
                    .cantidadActual(20)
                    .sku("MON-SAM24-FHD")
                    .activo(true)
                    .build();
            
            Producto monitor2 = Producto.builder()
                    .nombre("Monitor LG 27\"")
                    .descripcion("Monitor 4K para diseño")
                    .categoria(categoria)
                    .precio(new BigDecimal("599.99"))
                    .cantidadInicial(10)
                    .cantidadActual(10)
                    .sku("MON-LG27-4K")
                    .activo(true)
                    .build();
            
            productoRepository.save(monitor1);
            productoRepository.save(monitor2);
            
            log.info("Productos de prueba creados");
        }
    }

    /**
     * Método para limpiar el estado entre escenarios
     */
    public void limpiarEstado() {
        productoActual = null;
        productoDTOActual = null;
        listaProductos = null;
        paginaProductos = null;
        excepcionLanzada = null;
        resultadoOperacion = null;
        idProductoCreado = null;
    }
}
