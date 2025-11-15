package com.logitrack.service;

import com.logitrack.model.Bodega;
import com.logitrack.model.Movimiento;
import com.logitrack.model.MovimientoProducto;
import com.logitrack.model.Producto;
import com.logitrack.repository.BodegaRepository;
import com.logitrack.repository.MovimientoRepository;
import com.logitrack.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    public Map<String, Object> generarResumenReporte(String bodegaNombre) {
        Map<String, Object> reporte = new HashMap<>();
        
        // Obtener bodega por nombre o usar la primera disponible
        Bodega bodega = bodegaRepository.findAll().stream()
                .filter(b -> b.getNombre().equalsIgnoreCase(bodegaNombre))
                .findFirst()
                .orElse(bodegaRepository.findAll().stream().findFirst().orElse(null));
        
        if (bodega != null) {
            reporte.put("bodega", bodega.getNombre() + " " + bodega.getUbicacion());
        } else {
            reporte.put("bodega", "No disponible");
        }
        
        // Calcular stock total
        List<Producto> productos = productoRepository.findAll();
        int stockTotal = productos.stream()
                .mapToInt(Producto::getStock)
                .sum();
        reporte.put("stockTotal", stockTotal);
        
        // Obtener productos más movidos en el último mes
        LocalDateTime haceUnMes = LocalDateTime.now().minusMonths(1);
        List<Movimiento> movimientosRecientes = movimientoRepository.findByFechaBetween(haceUnMes, LocalDateTime.now());
        
        Map<String, Integer> productoMovimientos = new HashMap<>();
        for (Movimiento movimiento : movimientosRecientes) {
            if (movimiento.getProductos() != null) {
                for (MovimientoProducto mp : movimiento.getProductos()) {
                    if (mp.getProducto() != null) {
                        String nombreProducto = mp.getProducto().getNombre();
                        productoMovimientos.put(nombreProducto, 
                            productoMovimientos.getOrDefault(nombreProducto, 0) + mp.getCantidad());
                    }
                }
            }
        }
        
        // Obtener los 5 productos más movidos
        List<String> productosMasMovidos = productoMovimientos.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        reporte.put("productosMasMovidos", productosMasMovidos);
        
        return reporte;
    }
}