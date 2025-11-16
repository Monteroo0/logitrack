package com.logitrack.controller;

import com.logitrack.dto.BodegaDTO;
import com.logitrack.model.Bodega;
import com.logitrack.model.Usuario;
import com.logitrack.repository.UsuarioRepository;
import com.logitrack.service.BodegaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bodegas")
public class BodegaController {

    @Autowired
    private BodegaService bodegaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<BodegaDTO> getAllBodegas() {
        return bodegaService.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/list-raw")
    public List<java.util.Map<String,Object>> getAllRaw() {
        return bodegaService.findAll().stream().map(b -> {
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("id", b.getId());
            m.put("nombre", b.getNombre());
            m.put("ubicacion", b.getUbicacion());
            m.put("capacidad", b.getCapacidad());
            m.put("encargadoId", b.getEncargado() != null ? b.getEncargado().getId() : null);
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BodegaDTO> getBodegaById(@PathVariable Long id) {
        return bodegaService.findById(id)
                .map(bodega -> ResponseEntity.ok(toDto(bodega)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BodegaDTO> createBodega(@Valid @RequestBody BodegaDTO dto) {
        Bodega bodega = toEntity(dto);
        Bodega saved = bodegaService.save(bodega);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BodegaDTO> updateBodega(@PathVariable Long id, @Valid @RequestBody BodegaDTO dto) {
        return bodegaService.findById(id)
                .map(bodega -> {
                    bodega.setNombre(dto.getNombre());
                    bodega.setUbicacion(dto.getUbicacion());
                    bodega.setCapacidad(dto.getCapacidad());
                    if (dto.getEncargadoId() != null) {
                        Optional<Usuario> encargado = Optional.ofNullable(usuarioRepository.findById(dto.getEncargadoId()).orElse(null));
                        encargado.ifPresent(bodega::setEncargado);
                    } else {
                        bodega.setEncargado(null);
                    }
                    Bodega updated = bodegaService.save(bodega);
                    return ResponseEntity.ok(toDto(updated));
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

    private BodegaDTO toDto(Bodega bodega) {
        Long encargadoId = bodega.getEncargado() != null ? bodega.getEncargado().getId() : null;
        return new BodegaDTO(bodega.getNombre(), bodega.getUbicacion(), bodega.getCapacidad(), encargadoId);
    }

    private Bodega toEntity(BodegaDTO dto) {
        Bodega bodega = new Bodega();
        bodega.setNombre(dto.getNombre());
        bodega.setUbicacion(dto.getUbicacion());
        bodega.setCapacidad(dto.getCapacidad());
        if (dto.getEncargadoId() != null) {
            Usuario encargado = usuarioRepository.findById(dto.getEncargadoId()).orElse(null);
            bodega.setEncargado(encargado);
        }
        return bodega;
    }
}