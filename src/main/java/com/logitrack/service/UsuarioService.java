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
        return usuarioRepository.save(usuario);
    }

    private boolean isBcrypt(String p) {
        if (p == null) return false;
        return p.startsWith("$2a$") || p.startsWith("$2b$") || p.startsWith("$2y$");
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}