package com.logitrack.controller;

import com.logitrack.service.ReporteService;
import com.logitrack.repository.MovimientoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @GetMapping("/resumen")
    public Map<String, Object> obtenerResumen(@RequestParam(required = false, defaultValue = "Central") String bodega) {
        return reporteService.generarResumenReporte(bodega);
    }

    @GetMapping(value = "/movimientos/export.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void exportMovimientosXlsx(
            @RequestParam String inicio,
            @RequestParam String fin,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false, defaultValue = "desc") String orden,
            jakarta.servlet.http.HttpServletResponse response
    ) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=reporte_movimientos.xlsx");
        reporteService.exportarMovimientosXlsx(inicio, fin, tipo, orden, response.getOutputStream());
    }
}