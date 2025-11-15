package com.logitrack.controller;

import com.logitrack.dto.ProductoDTO;
import com.logitrack.model.Producto;
import com.logitrack.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<ProductoDTO> getAll() {
        return productoService.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(p -> ResponseEntity.ok(toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> create(@Valid @RequestBody ProductoDTO dto) {
        Producto p = toEntity(dto);
        Producto saved = productoService.save(p);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> update(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        return productoService.findById(id)
                .map(p -> {
                    p.setNombre(dto.getNombre());
                    p.setCategoria(dto.getCategoria());
                    p.setStock(dto.getStock());
                    p.setPrecio(dto.getPrecio().doubleValue());
                    Producto updated = productoService.save(p);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (productoService.findById(id).isPresent()) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/stock-bajo")
    public List<Producto> productosConStockBajo() {
        return productoService.findProductosConStockBajo(10);
    }

    private ProductoDTO toDto(Producto p) {
        return new ProductoDTO(p.getNombre(), p.getCategoria(), p.getStock(), BigDecimal.valueOf(p.getPrecio()));
    }

    private Producto toEntity(ProductoDTO dto) {
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setCategoria(dto.getCategoria());
        p.setStock(dto.getStock());
        p.setPrecio(dto.getPrecio().doubleValue());
        return p;
    }
}