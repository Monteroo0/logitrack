package com.logitrack.controller;

import com.logitrack.model.Auditoria;
import com.logitrack.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

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
}