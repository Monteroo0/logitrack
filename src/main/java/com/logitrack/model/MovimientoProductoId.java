package com.logitrack.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoProductoId implements Serializable {
    private Long movimientoId;
    private Long productoId;
}