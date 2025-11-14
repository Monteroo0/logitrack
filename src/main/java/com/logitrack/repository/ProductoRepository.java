package com.logitrack.repository;

import com.logitrack.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockLessThan(Integer stock);

    @Query("SELECT p FROM Producto p WHERE p.stock < :umbral")
    List<Producto> findProductosConStockBajo(@Param("umbral") int umbral);
}