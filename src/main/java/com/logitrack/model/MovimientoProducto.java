package com.logitrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movimiento_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoProducto {
    @EmbeddedId
    private MovimientoProductoId id;

    @ManyToOne
    @MapsId("movimientoId")
    @JoinColumn(name = "movimiento_id")
    private Movimiento movimiento;

    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;
}