package com.logitrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/util")
public class UtilController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/hash")
    public Map<String, String> hash(@RequestParam String pwd) {
        String hash = passwordEncoder.encode(pwd);
        return Map.of("pwd", pwd, "hash", hash);
    }

    @GetMapping("/check")
    public Map<String, Object> check(@RequestParam String username, @RequestParam String pwd) {
        boolean exists = false;
        boolean matches = false;
        String note = "";
        try {
            com.logitrack.model.Usuario u = userRepo().findByUsername(username);
            exists = (u != null);
            if (u != null && u.getPassword() != null) {
                matches = passwordEncoder.matches(pwd, u.getPassword());
            } else {
                note = "Usuario sin contraseña definida";
            }
        } catch (Exception e) {
            note = "Error: " + e.getMessage();
        }
        return Map.of("exists", exists, "matches", matches, "note", note);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.logitrack.repository.UsuarioRepository usuarioRepository;
    private com.logitrack.repository.UsuarioRepository userRepo(){return usuarioRepository;}

    @org.springframework.beans.factory.annotation.Autowired
    private com.logitrack.security.JwtUtil jwtUtil;

    @GetMapping("/token")
    public Map<String, Object> token(@RequestParam String username) {
        try {
            String t = jwtUtil.generateToken(username, Map.of("role","EMPLEADO","name",username));
            return Map.of("token", t);
        } catch (Exception e) {
            return Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage());
        }
    }

    @GetMapping("/token2")
    public Map<String, Object> token2(@RequestParam String username) {
        try {
            javax.crypto.SecretKey key = io.jsonwebtoken.security.Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
            String t = io.jsonwebtoken.Jwts.builder().setSubject(username).signWith(key).compact();
            return Map.of("token", t);
        } catch (Exception e) {
            return Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage());
        }
    }
}