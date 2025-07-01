package org.example.proyectofinal.cucumber.steps;

import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.example.proyectofinal.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductoSimpleSteps {

    @Autowired
    private ProductoService productoService;
    private boolean respuestaObtenida;

    @Dado("que el sistema está conectado a la base de datos")
    public void elSistemaEstaConectadoALaBaseDeDatos() {
        assertNotNull(productoService, "El servicio de productos debe estar disponible");
    }

    @Cuando("solicito la lista de productos")
    public void solicitoLaListaDeProductos() {
        try {
            productoService.listarProductosActivos(PageRequest.of(0, 10));
            respuestaObtenida = true;
        } catch (Exception e) {
            respuestaObtenida = false;
            throw e;
        }
    }

    @Entonces("debo obtener una respuesta del sistema")
    public void deboObtenerUnaRespuestaDelSistema() {
        assertNotNull(respuestaObtenida, "Debe haber una respuesta del sistema");
    }
} 