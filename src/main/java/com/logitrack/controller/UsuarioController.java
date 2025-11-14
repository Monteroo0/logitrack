package com.logitrack.controller;

import com.logitrack.dto.UsuarioDTO;
import com.logitrack.model.Rol;
import com.logitrack.model.Usuario;
import com.logitrack.repository.RolRepository;
import com.logitrack.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    private boolean isAdmin(Boolean admin) {
        return admin != null && admin;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAll(@RequestParam(required = false) Boolean admin) {
        if (!isAdmin(admin)) return ResponseEntity.status(403).build();
        List<UsuarioDTO> list = usuarioService.findAll().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Long id, @RequestParam(required = false) Boolean admin) {
        if (!isAdmin(admin)) return ResponseEntity.status(403).build();
        return usuarioService.findById(id)
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@Valid @RequestBody UsuarioDTO dto, @RequestParam(required = false) Boolean admin) {
        if (!isAdmin(admin)) return ResponseEntity.status(403).build();
        Usuario u = toEntity(dto);
        Optional<Rol> rol = rolRepository.findById(dto.getRolId());
        rol.ifPresent(u::setRol);
        Usuario saved = usuarioService.save(u);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto, @RequestParam(required = false) Boolean admin) {
        if (!isAdmin(admin)) return ResponseEntity.status(403).build();
        return usuarioService.findById(id)
                .map(u -> {
                    u.setUsername(dto.getUsername());
                    u.setPassword(dto.getPassword());
                    u.setNombre(dto.getNombre());
                    rolRepository.findById(dto.getRolId()).ifPresent(u::setRol);
                    Usuario updated = usuarioService.save(u);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) Boolean admin) {
        if (!isAdmin(admin)) return ResponseEntity.status(403).build();
        if (usuarioService.findById(id).isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private UsuarioDTO toDto(Usuario u) {
        Long rolId = u.getRol() != null ? u.getRol().getId() : null;
        return new UsuarioDTO(u.getUsername(), u.getPassword(), u.getNombre(), rolId);
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setNombre(dto.getNombre());
        return u;
    }
}