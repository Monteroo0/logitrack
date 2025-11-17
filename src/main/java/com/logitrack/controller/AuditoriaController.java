package com.logitrack.controller;

import com.logitrack.model.Auditoria;
import com.logitrack.service.AuditoriaService;
import com.logitrack.repository.MovimientoRepository;
import com.logitrack.repository.ProductoRepository;
import com.logitrack.repository.BodegaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

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

    @GetMapping("/filtrar")
    public List<Auditoria> filtrar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin
    ) {
        List<Auditoria> base = auditoriaService.findAll();
        java.util.stream.Stream<Auditoria> stream = base.stream();
        if (usuario != null && !usuario.isBlank()) {
            stream = stream.filter(a -> a.getUsuario() != null && usuario.equalsIgnoreCase(a.getUsuario().getUsername()));
        }
        if (tipo != null && !tipo.isBlank()) {
            stream = stream.filter(a -> tipo.equalsIgnoreCase(a.getTipoOperacion()));
        }
        if (inicio != null && fin != null) {
            java.time.LocalDateTime start = inicio.atStartOfDay();
            java.time.LocalDateTime end = fin.atTime(23,59,59);
            stream = stream.filter(a -> a.getFecha() != null && (a.getFecha().isEqual(start) || a.getFecha().isAfter(start)) && (a.getFecha().isEqual(end) || a.getFecha().isBefore(end)));
        }
        return stream.sorted((a,b) -> b.getFecha().compareTo(a.getFecha())).toList();
    }

    @GetMapping("/filtrar-mixto")
    public java.util.Map<String,Object> filtrarMixto(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String operacion,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin,
            @RequestParam(required = false) String tipoMovimiento
    ) {
        java.util.List<com.logitrack.model.Auditoria> baseAud = auditoriaService.findAll();
        java.util.stream.Stream<com.logitrack.model.Auditoria> sAud = baseAud.stream();
        if (usuario != null && !usuario.isBlank()) {
            sAud = sAud.filter(a -> a.getUsuario() != null && usuario.equalsIgnoreCase(a.getUsuario().getUsername()));
        }
        if (operacion != null && !operacion.isBlank()) {
            sAud = sAud.filter(a -> operacion.equalsIgnoreCase(a.getTipoOperacion()));
        }
        if (entidad != null && !entidad.isBlank()) {
            sAud = sAud.filter(a -> entidad.equalsIgnoreCase(a.getEntidad()));
        }
        if (inicio != null && fin != null) {
            java.time.LocalDateTime start = inicio.atStartOfDay();
            java.time.LocalDateTime end = fin.atTime(23,59,59);
            sAud = sAud.filter(a -> a.getFecha() != null && (a.getFecha().isEqual(start) || a.getFecha().isAfter(start)) && (a.getFecha().isEqual(end) || a.getFecha().isBefore(end)));
        }
        java.util.List<com.logitrack.model.Auditoria> auditorias = sAud.sorted((a,b) -> b.getFecha().compareTo(a.getFecha())).toList();

        java.util.List<com.logitrack.model.Movimiento> baseMov;
        if (inicio != null && fin != null) {
            java.time.LocalDateTime start = inicio.atStartOfDay();
            java.time.LocalDateTime end = fin.atTime(23,59,59);
            baseMov = movimientoRepository.findByFechaBetween(start, end);
        } else {
            baseMov = movimientoRepository.findAll();
        }
        java.util.stream.Stream<com.logitrack.model.Movimiento> sMov = baseMov.stream();
        if (usuario != null && !usuario.isBlank()) {
            sMov = sMov.filter(m -> m.getUsuario()!=null && usuario.equalsIgnoreCase(m.getUsuario().getUsername()));
        }
        if (tipoMovimiento != null && !tipoMovimiento.isBlank()) {
            sMov = sMov.filter(m -> tipoMovimiento.equalsIgnoreCase(m.getTipo()));
        }
        java.util.List<com.logitrack.model.Movimiento> movimientos = sMov.sorted((a,b) -> b.getFecha().compareTo(a.getFecha())).toList();

        java.util.Map<String,Object> res = new java.util.HashMap<>();
        res.put("auditorias", auditorias);
        java.util.List<java.util.Map<String,Object>> movOut = new java.util.ArrayList<>();
        for (com.logitrack.model.Movimiento m : movimientos) {
            if (m.getProductos()!=null && !m.getProductos().isEmpty()) {
                for (com.logitrack.model.MovimientoProducto mp : m.getProductos()) {
                    java.util.Map<String,Object> row = new java.util.HashMap<>();
                    row.put("id", m.getId());
                    row.put("fecha", m.getFecha());
                    row.put("usuario", m.getUsuario()!=null?m.getUsuario().getUsername():null);
                    row.put("tipo", m.getTipo());
                    row.put("bodegaOrigen", m.getBodegaOrigen()!=null?m.getBodegaOrigen().getNombre():null);
                    row.put("bodegaDestino", m.getBodegaDestino()!=null?m.getBodegaDestino().getNombre():null);
                    row.put("producto", mp.getProducto()!=null?mp.getProducto().getNombre():null);
                    row.put("cantidad", mp.getCantidad());
                    row.put("observaciones", m.getObservaciones());
                    movOut.add(row);
                }
            } else {
                java.util.Map<String,Object> row = new java.util.HashMap<>();
                row.put("id", m.getId());
                row.put("fecha", m.getFecha());
                row.put("usuario", m.getUsuario()!=null?m.getUsuario().getUsername():null);
                row.put("tipo", m.getTipo());
                row.put("bodegaOrigen", m.getBodegaOrigen()!=null?m.getBodegaOrigen().getNombre():null);
                row.put("bodegaDestino", m.getBodegaDestino()!=null?m.getBodegaDestino().getNombre():null);
                row.put("observaciones", m.getObservaciones());
                movOut.add(row);
            }
        }
        res.put("movimientos", movOut);
        return res;
    }

    @GetMapping("/consolidado")
    public List<java.util.Map<String,Object>> consolidado(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin
    ) {
        java.util.List<com.logitrack.model.Movimiento> base = movimientoRepository.findAll();
        java.util.stream.Stream<com.logitrack.model.Movimiento> stream = base.stream();
        if (usuario != null && !usuario.isBlank()) {
            stream = stream.filter(m -> m.getUsuario()!=null && usuario.equalsIgnoreCase(m.getUsuario().getUsername()));
        }
        if (tipo != null && !tipo.isBlank()) {
            stream = stream.filter(m -> tipo.equalsIgnoreCase(m.getTipo()));
        }
        if (inicio != null && fin != null) {
            java.time.LocalDateTime start = inicio.atStartOfDay();
            java.time.LocalDateTime end = fin.atTime(23,59,59);
            stream = stream.filter(m -> m.getFecha()!=null && (m.getFecha().isEqual(start) || m.getFecha().isAfter(start)) && (m.getFecha().isEqual(end) || m.getFecha().isBefore(end)));
        }
        java.util.List<com.logitrack.model.Movimiento> list = stream.sorted((a,b) -> b.getFecha().compareTo(a.getFecha())).toList();
        java.util.List<java.util.Map<String,Object>> result = new java.util.ArrayList<>();
        for (com.logitrack.model.Movimiento mov : list) {
            String tipoTxt = mov.getTipo()!=null?mov.getTipo().toUpperCase():"";
            String origen = mov.getBodegaOrigen()!=null?mov.getBodegaOrigen().getNombre():null;
            String destino = mov.getBodegaDestino()!=null?mov.getBodegaDestino().getNombre():null;
            if (mov.getProductos()!=null) {
                for (com.logitrack.model.MovimientoProducto mp : mov.getProductos()) {
                    String prodNombre = mp.getProducto()!=null?mp.getProducto().getNombre():"";
                    Integer cant = mp.getCantidad();
                    String resumen;
                    if ("ENTRADA".equals(tipoTxt)) {
                        resumen = "Se añadieron " + cant + " " + prodNombre + (destino!=null?(" a "+destino):"");
                    } else if ("SALIDA".equals(tipoTxt)) {
                        resumen = "Se retiraron " + cant + " " + prodNombre + (origen!=null?(" de "+origen):"");
                    } else if ("TRANSFERENCIA".equals(tipoTxt)) {
                        resumen = "Se transfirieron " + cant + " " + prodNombre + (origen!=null?(" de "+origen):"") + (destino!=null?(" a "+destino):"");
                    } else {
                        resumen = tipoTxt + ": " + cant + " " + prodNombre;
                    }
                    java.util.Map<String,Object> item = new java.util.HashMap<>();
                    item.put("fecha", mov.getFecha());
                    item.put("usuario", mov.getUsuario()!=null?mov.getUsuario().getUsername():null);
                    item.put("tipo", tipoTxt);
                    item.put("entidad", "Movimiento");
                    item.put("movimientoId", mov.getId());
                    item.put("resumen", resumen);
                    result.add(item);
                }
            }
        }
        return result;
    }

    @GetMapping("/consolidado/resumen")
    public java.util.Map<String,Object> consolidadoResumen(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin
    ) {
        java.util.List<com.logitrack.model.Movimiento> base = movimientoRepository.findAll();
        java.util.stream.Stream<com.logitrack.model.Movimiento> stream = base.stream();
        if (usuario != null && !usuario.isBlank()) {
            stream = stream.filter(m -> m.getUsuario()!=null && usuario.equalsIgnoreCase(m.getUsuario().getUsername()));
        }
        if (tipo != null && !tipo.isBlank()) {
            stream = stream.filter(m -> tipo.equalsIgnoreCase(m.getTipo()));
        }
        if (inicio != null && fin != null) {
            java.time.LocalDateTime start = inicio.atStartOfDay();
            java.time.LocalDateTime end = fin.atTime(23,59,59);
            stream = stream.filter(m -> m.getFecha()!=null && (m.getFecha().isEqual(start) || m.getFecha().isAfter(start)) && (m.getFecha().isEqual(end) || m.getFecha().isBefore(end)));
        }
        java.util.List<com.logitrack.model.Movimiento> list = stream.toList();
        java.util.Map<String,Integer> porTipo = new java.util.HashMap<>();
        java.util.Map<java.time.LocalDate,Integer> porDia = new java.util.TreeMap<>();
        int totalMovs = 0;
        for (com.logitrack.model.Movimiento m : list) {
            totalMovs++;
            java.time.LocalDate d = m.getFecha()!=null ? m.getFecha().toLocalDate() : null;
            if (d != null) porDia.put(d, porDia.getOrDefault(d, 0) + 1);
            String tipoMov = m.getTipo()!=null ? m.getTipo().toUpperCase() : "OTRO";
            porTipo.put(tipoMov, porTipo.getOrDefault(tipoMov, 0) + 1);
        }
        int dias = porDia.size();
        int max = porDia.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        int min = porDia.values().stream().mapToInt(Integer::intValue).min().orElse(0);
        double prom = dias>0 ? porDia.values().stream().mapToInt(Integer::intValue).average().orElse(0) : 0;
        java.util.List<java.util.Map<String,Object>> serie = new java.util.ArrayList<>();
        for (java.util.Map.Entry<java.time.LocalDate,Integer> e : porDia.entrySet()) {
            java.util.Map<String,Object> it = new java.util.HashMap<>();
            it.put("periodo", e.getKey().toString());
            it.put("total", e.getValue());
            serie.add(it);
        }
        java.util.Map<String,Object> res = new java.util.HashMap<>();
        res.put("totalesPorTipo", porTipo);
        res.put("totalMovimientos", totalMovs);
        res.put("porPeriodo", serie);
        res.put("indicadores", java.util.Map.of(
                "promedioDia", prom,
                "maxDia", max,
                "minDia", min
        ));
        return res;
    }

    @GetMapping(value = "/consolidado/export.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void exportConsolidadoXlsx(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin,
            jakarta.servlet.http.HttpServletResponse response
    ) throws Exception {
        java.util.Map<String,Object> r = consolidadoResumen(usuario, tipo, inicio, fin);
        org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sh1 = wb.createSheet("Totales");
        int r1 = 0;
        org.apache.poi.ss.usermodel.Row h1 = sh1.createRow(r1++);
        h1.createCell(0).setCellValue("Tipo");
        h1.createCell(1).setCellValue("Total");
        java.util.Map<String,Integer> porTipo = (java.util.Map<String,Integer>) r.get("totalesPorTipo");
        for (java.util.Map.Entry<String,Integer> e : porTipo.entrySet()) {
            org.apache.poi.ss.usermodel.Row row = sh1.createRow(r1++);
            row.createCell(0).setCellValue(e.getKey());
            row.createCell(1).setCellValue(e.getValue());
        }
        org.apache.poi.ss.usermodel.Sheet sh2 = wb.createSheet("Serie");
        int r2 = 0;
        org.apache.poi.ss.usermodel.Row h2 = sh2.createRow(r2++);
        h2.createCell(0).setCellValue("Periodo");
        h2.createCell(1).setCellValue("Total");
        java.util.List<java.util.Map<String,Object>> serie = (java.util.List<java.util.Map<String,Object>>) r.get("porPeriodo");
        for (java.util.Map<String,Object> it : serie) {
            org.apache.poi.ss.usermodel.Row row = sh2.createRow(r2++);
            row.createCell(0).setCellValue(String.valueOf(it.get("periodo")));
            row.createCell(1).setCellValue(Double.valueOf(String.valueOf(it.get("total"))));
        }
        response.setHeader("Content-Disposition", "attachment; filename=consolidado_auditoria.xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    @GetMapping(value = "/consolidado/export.csv", produces = "text/csv")
    public void exportConsolidadoCsv(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin,
            jakarta.servlet.http.HttpServletResponse response
    ) throws Exception {
        java.util.Map<String,Object> r = consolidadoResumen(usuario, tipo, inicio, fin);
        response.setHeader("Content-Disposition", "attachment; filename=consolidado_auditoria.csv");
        java.io.PrintWriter pw = response.getWriter();
        pw.println("Tipo,Total");
        java.util.Map<String,Integer> porTipo = (java.util.Map<String,Integer>) r.get("totalesPorTipo");
        for (java.util.Map.Entry<String,Integer> e : porTipo.entrySet()) {
            pw.println(e.getKey()+","+e.getValue());
        }
        pw.println();
        pw.println("Periodo,Total");
        java.util.List<java.util.Map<String,Object>> serie = (java.util.List<java.util.Map<String,Object>>) r.get("porPeriodo");
        for (java.util.Map<String,Object> it : serie) {
            pw.println(String.valueOf(it.get("periodo"))+","+String.valueOf(it.get("total")));
        }
        pw.flush();
    }

    @GetMapping("/paged")
    public java.util.Map<String,Object> paged(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        java.util.List<com.logitrack.model.Auditoria> list = filtrar(usuario, tipo, inicio, fin);
        int total = list.size();
        int from = Math.max(0, page*size);
        int to = Math.min(total, from+size);
        java.util.List<com.logitrack.model.Auditoria> items = from<to ? list.subList(from, to) : java.util.List.of();
        java.util.Map<String,Object> res = new java.util.HashMap<>();
        res.put("total", total);
        res.put("page", page);
        res.put("size", size);
        res.put("items", items);
        return res;
    }

    @GetMapping(value = "/export.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void exportAuditoriaXlsx(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String operacion,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin,
            @RequestParam(required = false) String tipoMovimiento,
            jakarta.servlet.http.HttpServletResponse response
    ) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=auditoria.xlsx");
        org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        // Hoja Auditorías
        java.util.List<com.logitrack.model.Auditoria> auds = filtrar(usuario, operacion, inicio, fin);
        org.apache.poi.ss.usermodel.Sheet sh = wb.createSheet("Auditorias");
        int r = 0;
        org.apache.poi.ss.usermodel.Row header = sh.createRow(r++);
        String[] cols = {"ID","Fecha","Usuario","Operación","Entidad","Valores Anteriores","Valores Nuevos"};
        for (int i=0;i<cols.length;i++) header.createCell(i).setCellValue(cols[i]);
        for (com.logitrack.model.Auditoria a : auds) {
            org.apache.poi.ss.usermodel.Row row = sh.createRow(r++);
            int c=0;
            row.createCell(c++).setCellValue(a.getId()!=null?a.getId():0);
            row.createCell(c++).setCellValue(a.getFecha()!=null?a.getFecha().toString():"");
            row.createCell(c++).setCellValue(a.getUsuario()!=null?a.getUsuario().getUsername():"");
            row.createCell(c++).setCellValue(a.getTipoOperacion()!=null?a.getTipoOperacion():"");
            row.createCell(c++).setCellValue(a.getEntidad()!=null?a.getEntidad():"");
            row.createCell(c++).setCellValue(a.getValoresAnteriores()!=null?a.getValoresAnteriores():"");
            row.createCell(c++).setCellValue(a.getValoresNuevos()!=null?a.getValoresNuevos():"");
        }
        for (int i=0;i<cols.length;i++) sh.autoSizeColumn(i);
        // Hoja Movimientos
        java.util.Map<String,Object> mix = filtrarMixto(usuario, operacion, entidad, inicio, fin, tipoMovimiento);
        java.util.List<java.util.Map<String,Object>> movs = (java.util.List<java.util.Map<String,Object>>) mix.getOrDefault("movimientos", java.util.List.of());
        org.apache.poi.ss.usermodel.Sheet sh2 = wb.createSheet("Movimientos");
        int r2 = 0;
        org.apache.poi.ss.usermodel.Row h2 = sh2.createRow(r2++);
        String[] cols2 = {"ID","Fecha","Usuario","Tipo","Origen","Destino","Producto","Cantidad","Observaciones"};
        for (int i=0;i<cols2.length;i++) h2.createCell(i).setCellValue(cols2[i]);
        for (java.util.Map<String,Object> m : movs) {
            org.apache.poi.ss.usermodel.Row row = sh2.createRow(r2++);
            int c=0;
            row.createCell(c++).setCellValue(m.get("id")!=null?Double.valueOf(String.valueOf(m.get("id"))):0);
            row.createCell(c++).setCellValue(m.get("fecha")!=null?String.valueOf(m.get("fecha")):"");
            row.createCell(c++).setCellValue(m.get("usuario")!=null?String.valueOf(m.get("usuario")):"");
            row.createCell(c++).setCellValue(m.get("tipo")!=null?String.valueOf(m.get("tipo")):"");
            row.createCell(c++).setCellValue(m.get("bodegaOrigen")!=null?String.valueOf(m.get("bodegaOrigen")):"");
            row.createCell(c++).setCellValue(m.get("bodegaDestino")!=null?String.valueOf(m.get("bodegaDestino")):"");
            row.createCell(c++).setCellValue(m.get("producto")!=null?String.valueOf(m.get("producto")):"");
            row.createCell(c++).setCellValue(m.get("cantidad")!=null?Double.valueOf(String.valueOf(m.get("cantidad"))):0);
            row.createCell(c++).setCellValue(m.get("observaciones")!=null?String.valueOf(m.get("observaciones")):"");
        }
        for (int i=0;i<cols2.length;i++) sh2.autoSizeColumn(i);
        wb.write(response.getOutputStream());
        wb.close();
    }

    @GetMapping(value = "/export.pdf", produces = "application/pdf")
    public void exportAuditoriaPdf(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String operacion,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin,
            @RequestParam(required = false) String tipoMovimiento,
            jakarta.servlet.http.HttpServletResponse response
    ) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=auditoria.pdf");
        java.util.List<com.logitrack.model.Auditoria> list = filtrar(usuario, operacion, inicio, fin);
        com.lowagie.text.Document doc = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());
        com.lowagie.text.pdf.PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();
        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD);
        doc.add(new com.lowagie.text.Paragraph("Reporte de Auditoría — Auditorías", titleFont));
        doc.add(new com.lowagie.text.Paragraph(" "));
        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(7);
        table.setWidthPercentage(100);
        String[] cols = {"ID","Fecha","Usuario","Operación","Entidad","Valores Anteriores","Valores Nuevos"};
        for (String c : cols) table.addCell(new com.lowagie.text.Phrase(c));
        for (com.logitrack.model.Auditoria a : list) {
            table.addCell(String.valueOf(a.getId()!=null?a.getId():0));
            table.addCell(a.getFecha()!=null?a.getFecha().toString():"");
            table.addCell(a.getUsuario()!=null?a.getUsuario().getUsername():"");
            table.addCell(a.getTipoOperacion()!=null?a.getTipoOperacion():"");
            table.addCell(a.getEntidad()!=null?a.getEntidad():"");
            table.addCell(a.getValoresAnteriores()!=null?a.getValoresAnteriores():"");
            table.addCell(a.getValoresNuevos()!=null?a.getValoresNuevos():"");
        }
        doc.add(table);
        doc.add(new com.lowagie.text.Paragraph(" "));
        doc.add(new com.lowagie.text.Paragraph("Reporte de Auditoría — Movimientos", titleFont));
        doc.add(new com.lowagie.text.Paragraph(" "));
        java.util.Map<String,Object> mix = filtrarMixto(usuario, operacion, entidad, inicio, fin, tipoMovimiento);
        java.util.List<java.util.Map<String,Object>> movs = (java.util.List<java.util.Map<String,Object>>) mix.getOrDefault("movimientos", java.util.List.of());
        com.lowagie.text.pdf.PdfPTable table2 = new com.lowagie.text.pdf.PdfPTable(9);
        table2.setWidthPercentage(100);
        String[] cols2 = {"ID","Fecha","Usuario","Tipo","Origen","Destino","Producto","Cantidad","Observaciones"};
        for (String c : cols2) table2.addCell(new com.lowagie.text.Phrase(c));
        for (java.util.Map<String,Object> m : movs) {
            table2.addCell(String.valueOf(m.get("id")!=null?m.get("id"):""));
            table2.addCell(String.valueOf(m.get("fecha")!=null?m.get("fecha"):""));
            table2.addCell(String.valueOf(m.get("usuario")!=null?m.get("usuario"):""));
            table2.addCell(String.valueOf(m.get("tipo")!=null?m.get("tipo"):""));
            table2.addCell(String.valueOf(m.get("bodegaOrigen")!=null?m.get("bodegaOrigen"):""));
            table2.addCell(String.valueOf(m.get("bodegaDestino")!=null?m.get("bodegaDestino"):""));
            table2.addCell(String.valueOf(m.get("producto")!=null?m.get("producto"):""));
            table2.addCell(String.valueOf(m.get("cantidad")!=null?m.get("cantidad"):""));
            table2.addCell(String.valueOf(m.get("observaciones")!=null?m.get("observaciones"):""));
        }
        doc.add(table2);
        doc.close();
    }
}