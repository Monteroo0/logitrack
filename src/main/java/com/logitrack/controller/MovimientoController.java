package com.logitrack.controller;

import com.logitrack.dto.MovimientoDTO;
import com.logitrack.model.Movimiento;
import com.logitrack.model.MovimientoProducto;
import com.logitrack.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public List<MovimientoDTO> getAll() {
        return movimientoService.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> getById(@PathVariable Long id) {
        return movimientoService.findById(id)
                .map(m -> ResponseEntity.ok(toDto(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MovimientoDTO dto) {
        return movimientoService.createFromDto(dto)
                .map(m -> ResponseEntity.ok(toDto(m)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDTO> update(@PathVariable Long id, @Valid @RequestBody MovimientoDTO dto) {
        return movimientoService.findById(id)
                .map(mov -> {
                    mov.setTipo(dto.getTipo());
                    if (dto.getFecha() != null) {
                        mov.setFecha(dto.getFecha());
                    }
                    Movimiento updated = movimientoService.save(mov);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (movimientoService.findById(id).isPresent()) {
            movimientoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/por-fecha")
    public List<Movimiento> movimientosPorFechas(@RequestParam LocalDate inicio, @RequestParam LocalDate fin) {
        LocalDateTime startDateTime = inicio.atStartOfDay();
        LocalDateTime endDateTime = fin.atTime(23, 59, 59);
        return movimientoService.findByFechaBetween(startDateTime, endDateTime);
    }

    private MovimientoDTO toDto(Movimiento m) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setFecha(m.getFecha());
        dto.setTipo(m.getTipo());
        dto.setUsuarioId(m.getUsuario() != null ? m.getUsuario().getId() : null);
        dto.setBodegaOrigenId(m.getBodegaOrigen() != null ? m.getBodegaOrigen().getId() : null);
        dto.setBodegaDestinoId(m.getBodegaDestino() != null ? m.getBodegaDestino().getId() : null);
        if (m.getProductos() != null) {
            dto.setProductos(m.getProductos().stream()
                    .map(mp -> new MovimientoDTO.Item(
                            mp.getProducto() != null ? mp.getProducto().getId() : null,
                            mp.getCantidad()))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}