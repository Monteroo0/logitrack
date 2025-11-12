package com.logitrack.repository;

import com.logitrack.model.MovimientoProducto;
import com.logitrack.model.MovimientoProductoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoProductoRepository extends JpaRepository<MovimientoProducto, MovimientoProductoId> {
}