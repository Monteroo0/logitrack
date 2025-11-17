package com.logitrack.service;

import com.logitrack.dto.MovimientoDTO;
import com.logitrack.model.*;
import com.logitrack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public List<Movimiento> findAll() {
        return movimientoRepository.findAll();
    }

    public Optional<Movimiento> findById(Long id) {
        return movimientoRepository.findById(id);
    }

    public Movimiento save(Movimiento movimiento) {
        return movimientoRepository.save(movimiento);
    }

    public void deleteById(Long id) {
        movimientoRepository.deleteById(id);
    }

    public Optional<Movimiento> createFromDto(MovimientoDTO dto) {
        Optional<Usuario> usuarioOpt = Optional.empty();
        if (dto.getUsuarioId() != null) {
            usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
        }
        if (usuarioOpt.isEmpty()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                try { System.out.println("MovimientoService auth username: " + auth.getName()); } catch (Exception ignored) {}
                Usuario u = usuarioRepository.findByUsername(auth.getName());
                if (u != null) usuarioOpt = Optional.of(u);
            }
        }
        if (usuarioOpt.isEmpty()) return Optional.empty();

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now());
        movimiento.setTipo(dto.getTipo());
        movimiento.setUsuario(usuarioOpt.get());

        if (dto.getBodegaOrigenId() != null) {
            bodegaRepository.findById(dto.getBodegaOrigenId()).ifPresent(movimiento::setBodegaOrigen);
        }
        if (dto.getBodegaDestinoId() != null) {
            bodegaRepository.findById(dto.getBodegaDestinoId()).ifPresent(movimiento::setBodegaDestino);
        }

        List<MovimientoProducto> items = new ArrayList<>();
        List<java.util.Map<String,Object>> auditBefore = new java.util.ArrayList<>();
        List<java.util.Map<String,Object>> auditAfter = new java.util.ArrayList<>();
        for (MovimientoDTO.Item item : dto.getProductos()) {
            Optional<Producto> prodOpt = productoRepository.findById(item.getProductoId());
            if (prodOpt.isEmpty()) return Optional.empty();
            Producto producto = prodOpt.get();
            int cantidad = item.getCantidad() != null ? item.getCantidad() : 0;

            java.util.Map<String,Object> before = new java.util.HashMap<>();
            before.put("productoId", producto.getId());
            before.put("stock", producto.getStock());
            auditBefore.add(before);

            String tipo = dto.getTipo() != null ? dto.getTipo().toUpperCase() : "";
            if ("SALIDA".equals(tipo) || "TRANSFERENCIA".equals(tipo)) {
                if (producto.getStock() == null || producto.getStock() < cantidad) {
                    return Optional.empty();
                }
            }
            if ("ENTRADA".equals(tipo)) {
                producto.setStock((producto.getStock() == null ? 0 : producto.getStock()) + cantidad);
            } else if ("SALIDA".equals(tipo)) {
                producto.setStock(producto.getStock() - cantidad);
            } else if ("TRANSFERENCIA".equals(tipo)) {
                // Stock global no cambia en transferencia
            }
            productoRepository.save(producto);

            java.util.Map<String,Object> after = new java.util.HashMap<>();
            after.put("productoId", producto.getId());
            after.put("stock", producto.getStock());
            auditAfter.add(after);

            MovimientoProducto mp = new MovimientoProducto();
            mp.setId(new MovimientoProductoId());
            mp.setMovimiento(movimiento);
            mp.setProducto(producto);
            mp.setCantidad(cantidad);
            items.add(mp);
        }
        movimiento.setProductos(items);
        Movimiento saved = movimientoRepository.save(movimiento);
        try { System.out.println("Movimiento creado id=" + saved.getId() + ", tipo=" + saved.getTipo()); } catch (Exception ignored) {}

        try {
            Auditoria audit = new Auditoria();
            audit.setTipoOperacion("INSERT");
            audit.setFecha(java.time.LocalDateTime.now());
            audit.setEntidad("Movimiento");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                Usuario u = usuarioRepository.findByUsername(auth.getName());
                if (u != null) audit.setUsuario(u);
            }
            String prevJson = objectMapper.writeValueAsString(java.util.Map.of(
                    "tipo", dto.getTipo(),
                    "bodegaOrigenId", dto.getBodegaOrigenId(),
                    "bodegaDestinoId", dto.getBodegaDestinoId(),
                    "stocks", auditBefore
            ));
            String newJson = objectMapper.writeValueAsString(java.util.Map.of(
                    "movimientoId", saved.getId(),
                    "tipo", saved.getTipo(),
                    "bodegaOrigenId", saved.getBodegaOrigen() != null ? saved.getBodegaOrigen().getId() : null,
                    "bodegaDestinoId", saved.getBodegaDestino() != null ? saved.getBodegaDestino().getId() : null,
                    "stocks", auditAfter
            ));
            audit.setValoresAnteriores(prevJson);
            audit.setValoresNuevos(newJson);
            auditoriaRepository.save(audit);
        } catch (Exception ignored) {}
        return Optional.of(saved);
    }

    public List<Movimiento> findByFechaBetween(LocalDateTime start, LocalDateTime end) {
        return movimientoRepository.findByFechaBetween(start, end);
    }
}