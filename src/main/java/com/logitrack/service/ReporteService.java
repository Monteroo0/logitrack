package com.logitrack.service;

import com.logitrack.model.Bodega;
import com.logitrack.model.Movimiento;
import com.logitrack.model.MovimientoProducto;
import com.logitrack.model.Producto;
import com.logitrack.repository.BodegaRepository;
import com.logitrack.repository.MovimientoRepository;
import com.logitrack.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    public Map<String, Object> generarResumenReporte(String bodegaNombre) {
        Map<String, Object> reporte = new HashMap<>();
        Bodega bodega = bodegaRepository.findAll().stream()
                .filter(b -> b.getNombre().equalsIgnoreCase(bodegaNombre))
                .findFirst()
                .orElse(bodegaRepository.findAll().stream().findFirst().orElse(null));
        reporte.put("bodega", bodega != null ? (bodega.getNombre() + " " + bodega.getUbicacion()) : "No disponible");
        List<Bodega> bodegas = bodegaRepository.findAll();
        List<Producto> productos = productoRepository.findAll();
        int stockTotal = productos.stream().mapToInt(Producto::getStock).sum();
        int totalProductos = productos.size();
        int totalBodegas = bodegas.size();
        int productosBajoMinimo = (int) productos.stream().filter(p -> p.getStock() < 10).count();
        LocalDateTime hoyInicio = java.time.LocalDate.now().atStartOfDay();
        LocalDateTime hoyFin = java.time.LocalDate.now().atTime(23,59,59);
        int movimientosHoy = movimientoRepository.findByFechaBetween(hoyInicio, hoyFin).size();
        LocalDateTime haceUnMes = LocalDateTime.now().minusMonths(1);
        List<Movimiento> movimientosRecientes = movimientoRepository.findByFechaBetween(haceUnMes, LocalDateTime.now());
        Map<String, Integer> productoMovimientos = new HashMap<>();
        for (Movimiento movimiento : movimientosRecientes) {
            if (movimiento.getProductos() != null) {
                for (MovimientoProducto mp : movimiento.getProductos()) {
                    if (mp.getProducto() != null) {
                        String nombreProducto = mp.getProducto().getNombre();
                        productoMovimientos.put(nombreProducto, productoMovimientos.getOrDefault(nombreProducto, 0) + mp.getCantidad());
                    }
                }
            }
        }
        List<String> productosMasMovidos = productoMovimientos.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        reporte.put("stockTotal", stockTotal);
        reporte.put("totalProductos", totalProductos);
        reporte.put("totalBodegas", totalBodegas);
        reporte.put("productosBajoMinimo", productosBajoMinimo);
        reporte.put("movimientosHoy", movimientosHoy);
        reporte.put("productosMasMovidos", productosMasMovidos);
        return reporte;
    }

    public void exportarMovimientosXlsx(String inicio, String fin, String tipo, String orden, java.io.OutputStream os) throws Exception {
        java.time.LocalDate start = java.time.LocalDate.parse(inicio);
        java.time.LocalDate end = java.time.LocalDate.parse(fin);
        java.time.LocalDateTime startDt = start.atStartOfDay();
        java.time.LocalDateTime endDt = end.atTime(23,59,59);
        java.util.List<com.logitrack.model.Movimiento> list = movimientoRepository.findByFechaBetween(startDt, endDt);
        if (tipo != null && !tipo.isBlank()) {
            list = list.stream().filter(m -> tipo.equalsIgnoreCase(m.getTipo())).toList();
        }
        list = new java.util.ArrayList<>(list);
        list.sort((a,b) -> {
            int cmp = java.util.Objects.compare(a.getFecha(), b.getFecha(), java.time.LocalDateTime::compareTo);
            return "asc".equalsIgnoreCase(orden) ? cmp : -cmp;
        });
        org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sh = wb.createSheet("Movimientos");
        int r = 0;
        org.apache.poi.ss.usermodel.Row header = sh.createRow(r++);
        String[] cols = {"Fecha","Tipo","UsuarioId","UsuarioNombre","BodegaOrigenId","BodegaDestinoId","ProductoId","Cantidad","Observaciones"};
        for (int i=0;i<cols.length;i++) header.createCell(i).setCellValue(cols[i]);
        for (com.logitrack.model.Movimiento m : list) {
            if (m.getProductos()==null || m.getProductos().isEmpty()) {
                org.apache.poi.ss.usermodel.Row row = sh.createRow(r++);
                fillRow(row, m, null);
            } else {
                for (com.logitrack.model.MovimientoProducto mp : m.getProductos()) {
                    org.apache.poi.ss.usermodel.Row row = sh.createRow(r++);
                    fillRow(row, m, mp);
                }
            }
        }
        for (int i=0;i<cols.length;i++) sh.autoSizeColumn(i);
        wb.write(os);
        wb.close();
    }

    private void fillRow(org.apache.poi.ss.usermodel.Row row, com.logitrack.model.Movimiento m, com.logitrack.model.MovimientoProducto mp) {
        int c=0;
        row.createCell(c++).setCellValue(m.getFecha()!=null?m.getFecha().toString():"");
        row.createCell(c++).setCellValue(m.getTipo()!=null?m.getTipo():"");
        row.createCell(c++).setCellValue(m.getUsuario()!=null?m.getUsuario().getId():0);
        row.createCell(c++).setCellValue(m.getUsuario()!=null?m.getUsuario().getNombre():"");
        row.createCell(c++).setCellValue(m.getBodegaOrigen()!=null?m.getBodegaOrigen().getId():0);
        row.createCell(c++).setCellValue(m.getBodegaDestino()!=null?m.getBodegaDestino().getId():0);
        row.createCell(c++).setCellValue(mp!=null && mp.getProducto()!=null?mp.getProducto().getId():0);
        row.createCell(c++).setCellValue(mp!=null && mp.getCantidad()!=null?mp.getCantidad():0);
        row.createCell(c++).setCellValue(m.getObservaciones()!=null?m.getObservaciones():"");
    }
}