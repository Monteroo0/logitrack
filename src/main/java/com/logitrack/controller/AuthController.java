package com.logitrack.controller;

import com.logitrack.model.Rol;
import com.logitrack.model.Usuario;
import com.logitrack.repository.RolRepository;
import com.logitrack.repository.UsuarioRepository;
import com.logitrack.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username != null) username = username.trim();
        if (password != null) password = password.trim();
        Usuario user = usuarioRepository.findByUsername(username);
        if (user == null || user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
        String role = user.getRol() != null ? user.getRol().getNombre() : "EMPLEADO";
        try {
            String token = jwtUtil.generateToken(username, Map.of("role", role, "name", user.getNombre()));
            return ResponseEntity.ok(Map.of("token", token, "username", username));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error","Token generation failed","message", ex.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String nombre = body.getOrDefault("nombre", username);
        String rolNombre = body.getOrDefault("rol", "EMPLEADO");

        if (usuarioRepository.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario ya existe"));
        }

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setNombre(nombre);
        Optional<Rol> rolOpt = rolRepository.findAll().stream().filter(r -> r.getNombre().equalsIgnoreCase(rolNombre)).findFirst();
        rolOpt.ifPresent(u::setRol);
        Usuario saved = usuarioRepository.save(u);
        return ResponseEntity.ok(Map.of("username", saved.getUsername(), "rol", rolNombre));
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of("status","ok"));
    }
}