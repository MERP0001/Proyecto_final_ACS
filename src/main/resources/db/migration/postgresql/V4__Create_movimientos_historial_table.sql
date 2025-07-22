CREATE TABLE movimientos_historial (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    tipo_movimiento VARCHAR(255) NOT NULL,
    cantidad INT NOT NULL,
    fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (user_id) REFERENCES users(id)
); 