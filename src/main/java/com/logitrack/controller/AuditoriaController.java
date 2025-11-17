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

    @GetMapping("/consolidado")
    public List<java.util.Map<String,Object>> consolidado(
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
        List<Auditoria> list = stream.sorted((a,b) -> b.getFecha().compareTo(a.getFecha())).toList();
        List<java.util.Map<String,Object>> result = new java.util.ArrayList<>();
        for (Auditoria a : list) {
            if (!"Movimiento".equalsIgnoreCase(a.getEntidad())) continue;
            Long movId = null;
            String tipoMov = null;
            try {
                java.util.Map<String,Object> v = a.getValoresNuevos()!=null ? objectMapper.readValue(a.getValoresNuevos(), java.util.Map.class) : java.util.Map.of();
                Object mId = v.get("movimientoId");
                Object t = v.get("tipo");
                if (mId != null) movId = Long.valueOf(String.valueOf(mId));
                if (t != null) tipoMov = String.valueOf(t);
            } catch (Exception ignored) {}
            if (movId == null) continue;
            java.util.Optional<com.logitrack.model.Movimiento> movOpt = movimientoRepository.findById(movId);
            if (movOpt.isEmpty()) continue;
            com.logitrack.model.Movimiento mov = movOpt.get();
            String tipoTxt = tipoMov!=null?tipoMov.toUpperCase(): (mov.getTipo()!=null?mov.getTipo().toUpperCase():"");
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
                    item.put("id", a.getId());
                    item.put("fecha", a.getFecha());
                    item.put("usuario", a.getUsuario()!=null?a.getUsuario().getUsername():null);
                    item.put("tipo", tipoTxt);
                    item.put("entidad", a.getEntidad());
                    item.put("movimientoId", movId);
                    item.put("resumen", resumen);
                    result.add(item);
                }
            }
        }
        return result;
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
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin,
            jakarta.servlet.http.HttpServletResponse response
    ) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=auditoria.xlsx");
        java.util.List<com.logitrack.model.Auditoria> list = filtrar(usuario, tipo, inicio, fin);
        org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sh = wb.createSheet("Auditoria");
        int r = 0;
        org.apache.poi.ss.usermodel.Row header = sh.createRow(r++);
        String[] cols = {"ID","Fecha","Usuario","Operación","Entidad","Valores Anteriores","Valores Nuevos"};
        for (int i=0;i<cols.length;i++) header.createCell(i).setCellValue(cols[i]);
        for (com.logitrack.model.Auditoria a : list) {
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
        wb.write(response.getOutputStream());
        wb.close();
    }

    @GetMapping(value = "/export.pdf", produces = "application/pdf")
    public void exportAuditoriaPdf(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin,
            jakarta.servlet.http.HttpServletResponse response
    ) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=auditoria.pdf");
        java.util.List<com.logitrack.model.Auditoria> list = filtrar(usuario, tipo, inicio, fin);
        com.lowagie.text.Document doc = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());
        com.lowagie.text.pdf.PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();
        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD);
        doc.add(new com.lowagie.text.Paragraph("Reporte de Auditoría", titleFont));
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
        doc.close();
    }
}