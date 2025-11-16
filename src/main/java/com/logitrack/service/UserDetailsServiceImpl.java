package com.logitrack.service;

import com.logitrack.model.Usuario;
import com.logitrack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByUsername(username);
        if (u == null) throw new UsernameNotFoundException("Usuario no encontrado");
        String roleName = u.getRol() != null ? u.getRol().getNombre() : "EMPLEADO";
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase());
        return new User(u.getUsername(), u.getPassword(), List.of(authority));
    }
}