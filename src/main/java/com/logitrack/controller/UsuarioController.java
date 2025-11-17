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

    @Autowired
    private com.logitrack.repository.UsuarioRepository usuarioRepository;

    private boolean isAdmin(Boolean admin) {
        return admin != null && admin;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAll(@RequestParam(required = false) Boolean admin) {
        if (!isAdmin(admin)) return ResponseEntity.status(403).build();
        List<UsuarioDTO> list = usuarioService.findAll().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/list-raw")
    public ResponseEntity<List<java.util.Map<String,Object>>> getAllRaw(@RequestParam(required = false) Boolean admin) {
        if (!isAdmin(admin)) return ResponseEntity.status(403).build();
        List<java.util.Map<String,Object>> list = usuarioService.findAll().stream().map(u -> {
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("nombre", u.getNombre());
            m.put("rolId", u.getRol()!=null?u.getRol().getId():null);
            m.put("rol", u.getRol()!=null?u.getRol().getNombre():null);
            m.put("activo", true);
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Long id, @RequestParam(required = false) Boolean admin) {
        if (!isAdmin(admin)) return ResponseEntity.status(403).build();
        return usuarioService.findById(id)
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<java.util.Map<String,Object>> getByUsername(@PathVariable String username) {
        com.logitrack.model.Usuario u = usuarioRepository.findByUsername(username);
        if (u == null) return ResponseEntity.notFound().build();
        Long rolId = u.getRol() != null ? u.getRol().getId() : null;
        String rolNombre = u.getRol() != null ? u.getRol().getNombre() : null;
        java.util.Map<String,Object> m = new java.util.HashMap<>();
        m.put("id", u.getId());
        m.put("username", u.getUsername());
        m.put("nombre", u.getNombre());
        m.put("rolId", rolId);
        m.put("rol", rolNombre);
        return ResponseEntity.ok(m);
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
                    if (dto.getPassword()!=null && !dto.getPassword().isBlank()) {
                        u.setPassword(dto.getPassword());
                    }
                    u.setNombre(dto.getNombre());
                    rolRepository.findById(dto.getRolId()).ifPresent(u::setRol);
                    Usuario updated = usuarioService.save(u);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rehash-plaintext")
    public ResponseEntity<java.util.Map<String,Object>> rehashPlaintext(@RequestParam(required = false) Boolean admin) {
        if (!isAdmin(admin)) return ResponseEntity.status(403).build();
        int total = 0;
        int changed = 0;
        for (Usuario u : usuarioService.findAll()) {
            total++;
            String p = u.getPassword();
            if (p != null && !p.isBlank() && !(p.startsWith("$2a$")||p.startsWith("$2b$")||p.startsWith("$2y$"))) {
                u.setPassword(p); // será codificada en usuarioService.save
                usuarioService.save(u);
                changed++;
            }
        }
        java.util.Map<String,Object> res = new java.util.HashMap<>();
        res.put("total", total);
        res.put("rehash", changed);
        return ResponseEntity.ok(res);
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