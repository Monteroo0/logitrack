package com.logitrack.controller;

import com.logitrack.model.Auditoria;
import com.logitrack.service.AuditoriaService;
import com.logitrack.repository.MovimientoRepository;
import com.logitrack.repository.ProductoRepository;
import com.logitrack.repository.BodegaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @GetMapping
    public List<Auditoria> getAll() {
        return auditoriaService.findAll();
    }

    @GetMapping("/usuario/{username}")
    public List<Auditoria> auditoriasPorUsuario(@PathVariable String username) {
        return auditoriaService.findByUsuarioUsername(username);
    }

    @GetMapping("/tipo/{tipoOperacion}")
    public List<Auditoria> auditoriasPorTipo(@PathVariable String tipoOperacion) {
        return auditoriaService.findByTipoOperacion(tipoOperacion);
    }

    @GetMapping("/filtrar")
    public List<Auditoria> filtrar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin
    ) {
        List<Auditoria> base = auditoriaService.findAll();
        java.util.stream.Stream<Auditoria> stream = base.stream();
        if (usuario != null && !usuario.isBlank()) {
            stream = stream.filter(a -> a.getUsuario() != null && usuario.equalsIgnoreCase(a.getUsuario().getUsername()));
        }
        if (tipo != null && !tipo.isBlank()) {
            stream = stream.filter(a -> tipo.equalsIgnoreCase(a.getTipoOperacion()));
        }
        if (inicio != null && fin != null) {
            java.time.LocalDateTime start = inicio.atStartOfDay();
            java.time.LocalDateTime end = fin.atTime(23,59,59);
            stream = stream.filter(a -> a.getFecha() != null && (a.getFecha().isEqual(start) || a.getFecha().isAfter(start)) && (a.getFecha().isEqual(end) || a.getFecha().isBefore(end)));
        }
        return stream.sorted((a,b) -> b.getFecha().compareTo(a.getFecha())).toList();
    }

    @GetMapping("/consolidado")
    public List<java.util.Map<String,Object>> consolidado(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin
    ) {
        List<Auditoria> base = auditoriaService.findAll();
        java.util.stream.Stream<Auditoria> stream = base.stream();
        if (usuario != null && !usuario.isBlank()) {
            stream = stream.filter(a -> a.getUsuario() != null && usuario.equalsIgnoreCase(a.getUsuario().getUsername()));
        }
        if (tipo != null && !tipo.isBlank()) {
            stream = stream.filter(a -> tipo.equalsIgnoreCase(a.getTipoOperacion()));
        }
        if (inicio != null && fin != null) {
            java.time.LocalDateTime start = inicio.atStartOfDay();
            java.time.LocalDateTime end = fin.atTime(23,59,59);
            stream = stream.filter(a -> a.getFecha() != null && (a.getFecha().isEqual(start) || a.getFecha().isAfter(start)) && (a.getFecha().isEqual(end) || a.getFecha().isBefore(end)));
        }
        List<Auditoria> list = stream.sorted((a,b) -> b.getFecha().compareTo(a.getFecha())).toList();
        List<java.util.Map<String,Object>> result = new java.util.ArrayList<>();
        for (Auditoria a : list) {
            if (!"Movimiento".equalsIgnoreCase(a.getEntidad())) continue;
            Long movId = null;
            String tipoMov = null;
            try {
                java.util.Map<String,Object> v = a.getValoresNuevos()!=null ? objectMapper.readValue(a.getValoresNuevos(), java.util.Map.class) : java.util.Map.of();
                Object mId = v.get("movimientoId");
                Object t = v.get("tipo");
                if (mId != null) movId = Long.valueOf(String.valueOf(mId));
                if (t != null) tipoMov = String.valueOf(t);
            } catch (Exception ignored) {}
            if (movId == null) continue;
            java.util.Optional<com.logitrack.model.Movimiento> movOpt = movimientoRepository.findById(movId);
            if (movOpt.isEmpty()) continue;
            com.logitrack.model.Movimiento mov = movOpt.get();
            String tipoTxt = tipoMov!=null?tipoMov.toUpperCase(): (mov.getTipo()!=null?mov.getTipo().toUpperCase():"");
            String origen = mov.getBodegaOrigen()!=null?mov.getBodegaOrigen().getNombre():null;
            String destino = mov.getBodegaDestino()!=null?mov.getBodegaDestino().getNombre():null;
            if (mov.getProductos()!=null) {
                for (com.logitrack.model.MovimientoProducto mp : mov.getProductos()) {
                    String prodNombre = mp.getProducto()!=null?mp.getProducto().getNombre():String.valueOf(mp.getProducto()!=null?mp.getProducto().getId():"");
                    Integer cant = mp.getCantidad();
                    String resumen;
                    if ("ENTRADA".equals(tipoTxt)) {
                        resumen = "Se añadieron " + cant + " " + prodNombre + (destino!=null?(" a "+destino):"");
                    } else if ("SALIDA".equals(tipoTxt)) {
                        resumen = "Se retiraron " + cant + " " + prodNombre + (origen!=null?(" de "+origen):"");
                    } else if ("TRANSFERENCIA".equals(tipoTxt)) {
                        resumen = "Se transfirieron " + cant + " " + prodNombre + (origen!=null?(" de "+origen):"") + (destino!=null?(" a "+destino):"");
                    } else {
                        resumen = tipoTxt + ": " + cant + " " + prodNombre;
                    }
                    java.util.Map<String,Object> item = new java.util.HashMap<>();
                    item.put("id", a.getId());
                    item.put("fecha", a.getFecha());
                    item.put("usuario", a.getUsuario()!=null?a.getUsuario().getUsername():null);
                    item.put("tipo", tipoTxt);
                    item.put("entidad", a.getEntidad());
                    item.put("movimientoId", movId);
                    item.put("resumen", resumen);
                    result.add(item);
                }
            }
        }
        return result;
    }
}