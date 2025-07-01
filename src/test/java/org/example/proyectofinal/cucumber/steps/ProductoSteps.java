package org.example.proyectofinal.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.mapper.ProductoMapper;
import org.example.proyectofinal.service.ProductoService;
import org.example.proyectofinal.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductoSteps {

    @LocalServerPort
    private Integer port;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoMapper productoMapper;

    private ProductoDTO productoCreado;
    private ProductoDTO productoActual;
    private Page<ProductoDTO> productosEncontrados;
    private Exception ultimaExcepcion;

    @Dado("que soy un administrador autenticado")
    public void queSOyUnAdministradorAutenticado() {
        // Por ahora, asumimos que estamos autenticados
    }

    @Dado("el sistema está conectado a la base de datos")
    public void elSistemaEstaConectadoALaBaseDeDatos() {
        assertNotNull(productoService, "El servicio de productos debe estar disponible");
    }

    @Cuando("creo un producto con los siguientes datos:")
    public void creoUnProductoConLosSiguientesDatos(DataTable dataTable) {
        try {
            Map<String, String> datos = dataTable.asMap(String.class, String.class);
            
            ProductoDTO producto = ProductoDTO.builder()
                    .nombre(datos.get("nombre"))
                    .descripcion(datos.get("descripcion"))
                    .categoria(datos.get("categoria"))
                    .precio(new BigDecimal(datos.get("precio")))
                    .cantidadInicial(Integer.parseInt(datos.get("cantidad")))
                    .sku(datos.get("sku"))
                    .build();

            productoCreado = productoService.crearProducto(producto);
        } catch (Exception e) {
            ultimaExcepcion = e;
        }
    }

    @Entonces("el producto debe ser creado exitosamente")
    public void elProductoDebeSerCreadoExitosamente() {
        assertNull(ultimaExcepcion, "No debería haber excepciones");
        assertNotNull(productoCreado, "El producto debe existir");
    }

    @Entonces("debe tener un ID asignado")
    public void debeTenerUnIdAsignado() {
        assertNotNull(productoCreado.getId(), "El ID no debe ser null");
        assertTrue(productoCreado.getId() > 0, "El ID debe ser mayor que 0");
    }

    @Entonces("los datos del producto deben coincidir con los ingresados")
    public void losDatosDelProductoDebenCoincidirConLosIngresados() {
        assertEquals("Monitor Samsung 32\"", productoCreado.getNombre());
        assertEquals("Monitores", productoCreado.getCategoria());
        assertEquals(new BigDecimal("499.99"), productoCreado.getPrecio());
        assertEquals(15, productoCreado.getCantidadInicial());
        assertEquals("MON-SAM32-4K", productoCreado.getSku());
    }

    @Dado("que existen productos en el sistema")
    public void queExistenProductosEnElSistema() {
        Page<ProductoDTO> productos = productoService.listarProductosActivos(PageRequest.of(0, 10));
        assertFalse(productos.isEmpty(), "Debe haber al menos un producto en el sistema");
    }

    @Cuando("busco productos con nombre {string}")
    public void buscoProductosConNombre(String nombre) {
        productosEncontrados = productoService.buscarProductos(
            nombre, null, null, null, PageRequest.of(0, 10));
    }

    @Entonces("debo obtener una lista de productos")
    public void deboObtenerUnaListaDeProductos() {
        assertNotNull(productosEncontrados, "La lista de productos no debe ser null");
        assertFalse(productosEncontrados.isEmpty(), "La lista de productos no debe estar vacía");
    }

    @Entonces("todos los productos deben contener {string} en su nombre")
    public void todosLosProductosDebenContenerEnSuNombre(String texto) {
        assertTrue(
            productosEncontrados.getContent().stream()
                .allMatch(p -> p.getNombre().toLowerCase().contains(texto.toLowerCase())),
            "Todos los productos deben contener el texto buscado"
        );
    }

    @Dado("que existe un producto con SKU {string}")
    public void queExisteUnProductoConSKU(String sku) {
        Optional<Producto> producto = productoRepository.findBySkuAndActivoTrue(sku);
        assertTrue(producto.isPresent(), "Debe existir un producto con el SKU especificado");
        productoActual = productoMapper.toDTO(producto.get());
    }

    @Cuando("actualizo el precio a {double}")
    public void actualizoElPrecioA(Double nuevoPrecio) {
        try {
            productoActual.setPrecio(BigDecimal.valueOf(nuevoPrecio));
            productoActual = productoService.actualizarProducto(productoActual.getId(), productoActual);
        } catch (Exception e) {
            ultimaExcepcion = e;
        }
    }

    @Entonces("el producto debe ser actualizado exitosamente")
    public void elProductoDebeSerActualizadoExitosamente() {
        assertNull(ultimaExcepcion, "No debería haber excepciones");
        assertNotNull(productoActual, "El producto debe existir después de la actualización");
    }

    @Entonces("el nuevo precio debe ser {double}")
    public void elNuevoPrecioDebeSer(Double precioEsperado) {
        assertEquals(BigDecimal.valueOf(precioEsperado), productoActual.getPrecio(),
            "El precio debe ser actualizado al valor esperado");
    }

    @Cuando("elimino el producto")
    public void eliminoElProducto() {
        try {
            productoService.eliminarProducto(productoActual.getId());
            // Recargar el producto después de la eliminación
            Optional<Producto> producto = productoRepository.findBySku(productoActual.getSku());
            if (producto.isPresent()) {
                productoActual = productoMapper.toDTO(producto.get());
            }
        } catch (Exception e) {
            ultimaExcepcion = e;
        }
    }

    @Entonces("el producto debe ser marcado como inactivo")
    public void elProductoDebeSerMarcadoComoInactivo() {
        assertNull(ultimaExcepcion, "No debería haber excepciones");
        assertNotNull(productoActual, "El producto debe existir después de la eliminación");
        assertFalse(productoActual.getActivo(), "El producto debe estar inactivo");
    }

    @Entonces("no debe aparecer en las búsquedas regulares")
    public void noDebeAparecerEnLasBusquedasRegulares() {
        Page<ProductoDTO> productos = productoService.listarProductosActivos(PageRequest.of(0, 10));
        assertTrue(
            productos.getContent().stream()
                .noneMatch(p -> p.getId().equals(productoActual.getId())),
            "El producto no debe aparecer en la lista de productos activos"
        );
    }
} 