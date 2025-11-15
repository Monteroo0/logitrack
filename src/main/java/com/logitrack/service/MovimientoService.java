package com.logitrack.service;

import com.logitrack.dto.MovimientoDTO;
import com.logitrack.model.*;
import com.logitrack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
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
        for (MovimientoDTO.Item item : dto.getProductos()) {
            Optional<Producto> prodOpt = productoRepository.findById(item.getProductoId());
            if (prodOpt.isEmpty()) return Optional.empty();
            MovimientoProducto mp = new MovimientoProducto();
            mp.setId(new MovimientoProductoId());
            mp.setMovimiento(movimiento);
            mp.setProducto(prodOpt.get());
            mp.setCantidad(item.getCantidad());
            items.add(mp);
        }
        movimiento.setProductos(items);
        Movimiento saved = movimientoRepository.save(movimiento);
        return Optional.of(saved);
    }

    public List<Movimiento> findByFechaBetween(LocalDateTime start, LocalDateTime end) {
        return movimientoRepository.findByFechaBetween(start, end);
    }
}