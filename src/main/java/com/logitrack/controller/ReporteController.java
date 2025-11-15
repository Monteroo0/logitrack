package com.logitrack.controller;

import com.logitrack.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/resumen")
    public Map<String, Object> obtenerResumen(@RequestParam(required = false, defaultValue = "Central") String bodega) {
        return reporteService.generarResumenReporte(bodega);
    }
}