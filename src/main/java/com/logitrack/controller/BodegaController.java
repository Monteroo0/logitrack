package com.logitrack.controller;

import com.logitrack.model.Bodega;
import com.logitrack.service.BodegaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodegas")
public class BodegaController {

    @Autowired
    private BodegaService bodegaService;

    @GetMapping
    public List<Bodega> getAllBodegas() {
        return bodegaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bodega> getBodegaById(@PathVariable Long id) {
        return bodegaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Bodega createBodega(@RequestBody Bodega bodega) {
        return bodegaService.save(bodega);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bodega> updateBodega(@PathVariable Long id, @RequestBody Bodega bodegaDetails) {
        return bodegaService.findById(id)
                .map(bodega -> {
                    bodega.setNombre(bodegaDetails.getNombre());
                    bodega.setUbicacion(bodegaDetails.getUbicacion());
                    return ResponseEntity.ok(bodegaService.save(bodega));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBodega(@PathVariable Long id) {
        if (bodegaService.findById(id).isPresent()) {
            bodegaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}