package com.logitrack.service;

import com.logitrack.model.Usuario;
import com.logitrack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.logitrack.repository.AuditoriaRepository auditoriaRepository;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        String pwd = usuario.getPassword();
        if (pwd != null && !pwd.isBlank() && !isBcrypt(pwd)) {
            usuario.setPassword(passwordEncoder.encode(pwd));
        }
        boolean exists = usuario.getId()!=null && usuarioRepository.findById(usuario.getId()).isPresent();
        Usuario saved = usuarioRepository.save(usuario);
        try {
            com.logitrack.model.Auditoria a = new com.logitrack.model.Auditoria();
            a.setTipoOperacion(exists?"UPDATE":"INSERT");
            a.setFecha(java.time.LocalDateTime.now());
            a.setEntidad("Usuario");
            String actorUsername = null;
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                Object principal = auth.getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails u) {
                    actorUsername = u.getUsername();
                } else if (principal != null) {
                    actorUsername = String.valueOf(principal);
                }
            }
            com.logitrack.model.Usuario actor = null;
            if (actorUsername != null) {
                actor = usuarioRepository.findByUsername(actorUsername);
            }
            a.setUsuario(actor != null ? actor : saved);
            a.setValoresNuevos(objectMapper.writeValueAsString(java.util.Map.of(
                    "id", saved.getId(),
                    "username", saved.getUsername(),
                    "nombre", saved.getNombre(),
                    "rol", saved.getRol()!=null?saved.getRol().getNombre():null
            )));
            auditoriaRepository.save(a);
        } catch (Exception ignored) {}
        return saved;
    }

    private boolean isBcrypt(String p) {
        if (p == null) return false;
        return p.startsWith("$2a$") || p.startsWith("$2b$") || p.startsWith("$2y$");
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}