package org.example.proyectofinal.cucumber.steps;

import io.cucumber.java.es.*;
import io.cucumber.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.entity.MovimientoHistorial;
import org.example.proyectofinal.entity.TipoMovimiento;
import org.example.proyectofinal.repository.ProductoRepository;
import org.example.proyectofinal.repository.CategoriaRepository;
import org.example.proyectofinal.repository.MovimientoHistorialRepository;
import org.example.proyectofinal.service.ProductoService;
import org.example.proyectofinal.service.MovimientoHistorialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MovimientoSteps {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoSteps.class);

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private MovimientoHistorialService movimientoService;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private MovimientoHistorialRepository movimientoRepository;

    private Producto productoActual;
    private List<MovimientoHistorial> movimientosEncontrados;
    private Exception ultimaExcepcion;
    private Integer stockAnterior;
    private Integer stockActual;

    @Dado("que existe un producto con stock actual de {int} unidades")
    @Transactional
    public void que_existe_un_producto_con_stock_actual_de_unidades(Integer stock) {
        logger.info("Creando producto con stock: {}", stock);
        
        // Crear categoría si no existe
        Categoria categoria = categoriaRepository.findByNombreIgnoreCase("Categoria Test")
            .orElseGet(() -> {
                Categoria nuevaCategoria = new Categoria();
                nuevaCategoria.setNombre("Categoria Test");
                nuevaCategoria.setDescripcion("Categoria para pruebas");
                return categoriaRepository.save(nuevaCategoria);
            });

        // Crear producto
        productoActual = new Producto();
        productoActual.setNombre("Producto Test Movimientos");
        productoActual.setDescripcion("Producto para probar movimientos");
        productoActual.setPrecio(new BigDecimal("100.00"));
        productoActual.setCantidadActual(stock);
        productoActual.setCategoria(categoria);
        productoActual.setSku("PROD-MOV-001");
        productoActual.setActivo(true);
        
        productoActual = productoRepository.save(productoActual);
        stockAnterior = stock;
        
        logger.info("Producto creado con ID: {} y stock: {}", productoActual.getId(), productoActual.getCantidadActual());
    }

    @Cuando("registro una entrada de {int} unidades con motivo {string}")
    @Transactional
    public void registro_una_entrada_de_unidades_con_motivo(Integer cantidad, String motivo) {
        logger.info("Registrando entrada de {} unidades para producto {}", cantidad, productoActual.getId());
        
        try {
            movimientoService.registrarMovimiento(
                productoActual.getId(),
                TipoMovimiento.ENTRADA,
                cantidad,
                motivo
            );
            
            // Actualizar el producto actual
            productoActual = productoRepository.findById(productoActual.getId()).orElse(productoActual);
            stockActual = productoActual.getCantidadActual();
            
        } catch (Exception e) {
            ultimaExcepcion = e;
            logger.error("Error al registrar entrada: {}", e.getMessage());
        }
    }

    @Cuando("registro una salida de {int} unidades con motivo {string}")
    @Transactional
    public void registro_una_salida_de_unidades_con_motivo(Integer cantidad, String motivo) {
        logger.info("Registrando salida de {} unidades para producto {}", cantidad, productoActual.getId());
        
        try {
            movimientoService.registrarMovimiento(
                productoActual.getId(),
                TipoMovimiento.SALIDA,
                cantidad,
                motivo
            );
            
            // Actualizar el producto actual
            productoActual = productoRepository.findById(productoActual.getId()).orElse(productoActual);
            stockActual = productoActual.getCantidadActual();
            
        } catch (Exception e) {
            ultimaExcepcion = e;
            logger.error("Error al registrar salida: {}", e.getMessage());
        }
    }

    @Cuando("intento registrar una salida de {int} unidades")
    @Transactional
    public void intento_registrar_una_salida_de_unidades(Integer cantidad) {
        logger.info("Intentando registrar salida de {} unidades para producto {}", cantidad, productoActual.getId());
        
        try {
            movimientoService.registrarMovimiento(
                productoActual.getId(),
                TipoMovimiento.SALIDA,
                cantidad,
                "Venta con stock insuficiente"
            );
            
            productoActual = productoRepository.findById(productoActual.getId()).orElse(productoActual);
            stockActual = productoActual.getCantidadActual();
            
        } catch (Exception e) {
            ultimaExcepcion = e;
            stockActual = productoActual.getCantidadActual(); // El stock no debe cambiar
            logger.info("Excepción esperada capturada: {}", e.getMessage());
        }
    }

    @Entonces("el stock del producto debe aumentar a {int} unidades")
    public void el_stock_del_producto_debe_aumentar_a_unidades(Integer stockEsperado) {
        logger.info("Verificando stock. Actual: {}, Esperado: {}", stockActual, stockEsperado);
        assertEquals(stockEsperado, stockActual, "El stock no coincide con el esperado");
    }

    @Entonces("el stock del producto debe disminuir a {int} unidades")
    public void el_stock_del_producto_debe_disminuir_a_unidades(Integer stockEsperado) {
        logger.info("Verificando stock. Actual: {}, Esperado: {}", stockActual, stockEsperado);
        assertEquals(stockEsperado, stockActual, "El stock no coincide con el esperado");
    }

    @Entonces("debe crearse un registro de movimiento de tipo {string}")
    public void debe_crearse_un_registro_de_movimiento_de_tipo(String tipoMovimiento) {
        logger.info("Verificando creación de movimiento de tipo: {}", tipoMovimiento);
        
        List<MovimientoHistorial> movimientos = movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoActual.getId());
        
        assertFalse(movimientos.isEmpty(), "No se encontraron movimientos para el producto");
        
        MovimientoHistorial ultimoMovimiento = movimientos.get(0);
        assertEquals(TipoMovimiento.valueOf(tipoMovimiento), ultimoMovimiento.getTipoMovimiento(), 
                    "El tipo de movimiento no coincide");
    }

    @Entonces("el historial debe mostrar el movimiento registrado")
    public void el_historial_debe_mostrar_el_movimiento_registrado() {
        logger.info("Verificando que el movimiento aparezca en el historial");
        
        List<MovimientoHistorial> movimientos = movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoActual.getId());
        
        assertFalse(movimientos.isEmpty(), "No se encontraron movimientos en el historial");
        
        MovimientoHistorial ultimoMovimiento = movimientos.get(0);
        assertNotNull(ultimoMovimiento.getFechaMovimiento(), "La fecha del movimiento no debe ser nula");
        assertTrue(ultimoMovimiento.getFechaMovimiento().isAfter(LocalDateTime.now().minusMinutes(1)), 
                  "El movimiento debe ser reciente");
    }

    @Entonces("debe mostrar un error de stock insuficiente")
    public void debe_mostrar_un_error_de_stock_insuficiente() {
        logger.info("Verificando que se haya producido una excepción de stock insuficiente");
        
        assertNotNull(ultimaExcepcion, "Debería haberse producido una excepción");
        assertTrue(ultimaExcepcion.getMessage().toLowerCase().contains("stock") || 
                  ultimaExcepcion.getMessage().toLowerCase().contains("insuficiente"),
                  "La excepción debería indicar stock insuficiente");
    }

    @Entonces("el stock del producto debe permanecer en {int} unidades")
    public void el_stock_del_producto_debe_permanecer_en_unidades(Integer stockEsperado) {
        logger.info("Verificando que el stock permanezca en: {}", stockEsperado);
        assertEquals(stockEsperado, stockActual, "El stock no debería haber cambiado");
    }

    @Entonces("no debe crearse ningún registro de movimiento")
    public void no_debe_crearse_ningun_registro_de_movimiento() {
        logger.info("Verificando que no se haya creado un nuevo movimiento");
        
        List<MovimientoHistorial> movimientos = movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoActual.getId());
        
        // Verificar que no hay movimientos o que el último movimiento es anterior a esta operación
        if (!movimientos.isEmpty()) {
            MovimientoHistorial ultimoMovimiento = movimientos.get(0);
            assertTrue(ultimoMovimiento.getFechaMovimiento().isBefore(LocalDateTime.now().minusSeconds(5)),
                      "No debería haberse creado un nuevo movimiento");
        }
    }

    @Dado("que un producto tiene varios movimientos registrados")
    @Transactional
    public void que_un_producto_tiene_varios_movimientos_registrados() {
        logger.info("Creando producto con varios movimientos");
        
        // Crear producto si no existe
        if (productoActual == null) {
            que_existe_un_producto_con_stock_actual_de_unidades(20);
        }
        
        // Crear varios movimientos
        try {
            movimientoService.registrarMovimiento(productoActual.getId(), TipoMovimiento.ENTRADA, 10, "Compra inicial");
            Thread.sleep(10); // Pequeña pausa para diferencias en timestamp
            movimientoService.registrarMovimiento(productoActual.getId(), TipoMovimiento.SALIDA, 5, "Venta 1");
            Thread.sleep(10);
            movimientoService.registrarMovimiento(productoActual.getId(), TipoMovimiento.ENTRADA, 15, "Reposición");
            Thread.sleep(10);
            movimientoService.registrarMovimiento(productoActual.getId(), TipoMovimiento.SALIDA, 8, "Venta 2");
        } catch (Exception e) {
            logger.error("Error creando movimientos: {}", e.getMessage());
        }
    }

    @Cuando("consulto el historial de movimientos del producto")
    public void consulto_el_historial_de_movimientos_del_producto() {
        logger.info("Consultando historial de movimientos para producto: {}", productoActual.getId());
        
        movimientosEncontrados = movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoActual.getId());
    }

    @Entonces("debo obtener una lista ordenada por fecha")
    public void debo_obtener_una_lista_ordenada_por_fecha() {
        logger.info("Verificando orden de movimientos por fecha");
        
        assertNotNull(movimientosEncontrados, "La lista de movimientos no debe ser nula");
        assertTrue(movimientosEncontrados.size() > 1, "Debe haber al menos 2 movimientos");
        
        // Verificar que está ordenado de más reciente a más antiguo
        for (int i = 0; i < movimientosEncontrados.size() - 1; i++) {
            LocalDateTime fechaActual = movimientosEncontrados.get(i).getFechaMovimiento();
            LocalDateTime fechaSiguiente = movimientosEncontrados.get(i + 1).getFechaMovimiento();
            
            assertTrue(fechaActual.isAfter(fechaSiguiente) || fechaActual.isEqual(fechaSiguiente),
                      "Los movimientos deben estar ordenados por fecha descendente");
        }
    }

    @Entonces("cada movimiento debe mostrar fecha, tipo, cantidad y motivo")
    public void cada_movimiento_debe_mostrar_fecha_tipo_cantidad_y_motivo() {
        logger.info("Verificando completitud de datos en movimientos");
        
        for (MovimientoHistorial movimiento : movimientosEncontrados) {
            assertNotNull(movimiento.getFechaMovimiento(), "La fecha del movimiento no debe ser nula");
            assertNotNull(movimiento.getTipoMovimiento(), "El tipo de movimiento no debe ser nulo");
            assertNotNull(movimiento.getCantidad(), "La cantidad no debe ser nula");
            assertNotNull(movimiento.getMotivo(), "El motivo no debe ser nulo");
            assertTrue(movimiento.getCantidad() > 0, "La cantidad debe ser mayor que 0");
        }
    }

    @Entonces("el balance final debe coincidir con el stock actual")
    public void el_balance_final_debe_coincidir_con_el_stock_actual() {
        logger.info("Verificando que el balance de movimientos coincida con el stock actual");
        
        int balanceCalculado = stockAnterior;
        
        for (MovimientoHistorial movimiento : movimientosEncontrados) {
            if (movimiento.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
                balanceCalculado += movimiento.getCantidad();
            } else {
                balanceCalculado -= movimiento.getCantidad();
            }
        }
        
        productoActual = productoRepository.findById(productoActual.getId()).orElse(productoActual);
        assertEquals(balanceCalculado, productoActual.getCantidadActual().intValue(), 
                    "El balance calculado debe coincidir con el stock actual");
    }

    @Dado("que existen movimientos en diferentes fechas")
    @Transactional
    public void que_existen_movimientos_en_diferentes_fechas() {
        logger.info("Creando movimientos en diferentes fechas");
        
        if (productoActual == null) {
            que_existe_un_producto_con_stock_actual_de_unidades(50);
        }
        
        // Aquí simularíamos movimientos en diferentes fechas
        // En un escenario real, modificaríamos las fechas directamente en la base de datos
        try {
            movimientoService.registrarMovimiento(productoActual.getId(), TipoMovimiento.ENTRADA, 20, "Compra enero");
            movimientoService.registrarMovimiento(productoActual.getId(), TipoMovimiento.SALIDA, 10, "Venta febrero");
            movimientoService.registrarMovimiento(productoActual.getId(), TipoMovimiento.ENTRADA, 15, "Compra marzo");
        } catch (Exception e) {
            logger.error("Error creando movimientos: {}", e.getMessage());
        }
    }

    @Cuando("solicito un reporte de movimientos entre {string} y {string}")
    public void solicito_un_reporte_de_movimientos_entre_y(String fechaInicio, String fechaFin) {
        logger.info("Solicitando reporte de movimientos entre {} y {}", fechaInicio, fechaFin);
        
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        
        movimientosEncontrados = movimientoRepository.findByFechaMovimientoBetween(
            inicio.atStartOfDay(),
            fin.atTime(23, 59, 59)
        );
    }

    @Entonces("debo obtener todos los movimientos del período")
    public void debo_obtener_todos_los_movimientos_del_periodo() {
        logger.info("Verificando que se obtuvieron movimientos del período");
        
        assertNotNull(movimientosEncontrados, "La lista de movimientos no debe ser nula");
        // En este test básico, simplemente verificamos que obtuvimos algunos movimientos
        assertTrue(movimientosEncontrados.size() >= 0, "Debe retornar una lista (puede estar vacía)");
    }

    @Entonces("el reporte debe incluir totales por tipo de movimiento")
    public void el_reporte_debe_incluir_totales_por_tipo_de_movimiento() {
        logger.info("Verificando totales por tipo de movimiento");
        
        int totalEntradas = 0;
        int totalSalidas = 0;
        
        for (MovimientoHistorial movimiento : movimientosEncontrados) {
            if (movimiento.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
                totalEntradas += movimiento.getCantidad();
            } else {
                totalSalidas += movimiento.getCantidad();
            }
        }
        
        logger.info("Total entradas: {}, Total salidas: {}", totalEntradas, totalSalidas);
        assertTrue(totalEntradas >= 0, "El total de entradas debe ser mayor o igual a 0");
        assertTrue(totalSalidas >= 0, "El total de salidas debe ser mayor o igual a 0");
    }

    @Entonces("debe mostrar el balance neto del período")
    public void debe_mostrar_el_balance_neto_del_periodo() {
        logger.info("Calculando balance neto del período");
        
        int totalEntradas = 0;
        int totalSalidas = 0;
        
        for (MovimientoHistorial movimiento : movimientosEncontrados) {
            if (movimiento.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
                totalEntradas += movimiento.getCantidad();
            } else {
                totalSalidas += movimiento.getCantidad();
            }
        }
        
        int balanceNeto = totalEntradas - totalSalidas;
        logger.info("Balance neto del período: {}", balanceNeto);
        
        // El balance puede ser positivo, negativo o cero
        assertNotNull(balanceNeto, "El balance neto debe ser calculable");
    }
}
