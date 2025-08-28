package org.example.proyectofinal.cucumber.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.example.proyectofinal.entity.Producto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps definition simplificados para las pruebas de regresión de productos.
 * Se enfoca en validaciones básicas y rápidas para la suite de regresión.
 */
public class ProductoSimpleSteps {

    private static final Logger log = LoggerFactory.getLogger(ProductoSimpleSteps.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    // Variables para almacenar resultados simples
    private List<Producto> listaProductosSimple;
    private boolean respuestaRecibida;

    // ==================== PASOS CUANDO (WHEN) - SIMPLES ====================

    @Cuando("solicito la lista de productos")
    public void solicito_la_lista_de_productos() {
        log.info("Solicitando lista de productos para regresión");
        
        try {
            // Usar búsqueda paginada básica
            Page<Producto> productosPage = productoRepository.findByActivoTrue(PageRequest.of(0, 100));
            listaProductosSimple = productosPage.getContent();
            respuestaRecibida = true;
            
            log.info("Lista de productos obtenida: {} elementos", listaProductosSimple.size());
            
        } catch (Exception e) {
            log.error("Error al obtener lista de productos: {}", e.getMessage());
            respuestaRecibida = false;
        }
    }

    // ==================== PASOS ENTONCES (THEN) - SIMPLES ====================

    @Entonces("debo obtener una respuesta del sistema")
    public void debo_obtener_una_respuesta_del_sistema() {
        log.info("Verificando que se obtuvo respuesta del sistema");
        
        assertTrue(respuestaRecibida, "Debe haberse recibido una respuesta del sistema");
        
        log.info("Respuesta del sistema confirmada");
    }

    /**
     * Método para limpiar el estado entre escenarios
     */
    public void limpiarEstado() {
        listaProductosSimple = null;
        respuestaRecibida = false;
    }
}
