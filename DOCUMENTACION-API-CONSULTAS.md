# 📊 Documentación de la API de Consultas Avanzadas y Reportes de LogiTrack

## 🎯 Objetivo
Implementar endpoints para consultas analíticas y reportes de inventario, aprovechando los repositorios existentes.

## 🔧 Endpoints Implementados

### 1. Productos con Stock Bajo
**Endpoint:** `GET /api/productos/stock-bajo`

**Descripción:** Obtiene todos los productos con stock menor a 10 unidades.

**Respuesta Exitosa:**
```json
[
  {
    "id": 1,
    "nombre": "Laptop HP",
    "categoria": "Electrónica",
    "stock": 5,
    "precio": 1200.00
  },
  {
    "id": 2,
    "nombre": "Mouse Logitech",
    "categoria": "Accesorios",
    "stock": 3,
    "precio": 25.50
  }
]
```

**Código de Estado:** 200 OK

---

### 2. Movimientos por Rango de Fechas
**Endpoint:** `GET /api/movimientos/por-fecha?inicio={fecha}&fin={fecha}`

**Descripción:** Obtiene todos los movimientos dentro de un rango de fechas específico.

**Parámetros de Consulta:**
- `inicio` (obligatorio): Fecha de inicio en formato YYYY-MM-DD
- `fin` (obligatorio): Fecha de fin en formato YYYY-MM-DD

**Ejemplo de Uso:** `GET /api/movimientos/por-fecha?inicio=2024-01-01&fin=2024-12-31`

**Respuesta Exitosa:**
```json
[
  {
    "id": 1,
    "fecha": "2024-03-15T10:30:00",
    "tipo": "ENTRADA",
    "usuario": {
      "id": 1,
      "username": "admin"
    },
    "bodegaOrigen": null,
    "bodegaDestino": {
      "id": 1,
      "nombre": "Central Bogotá"
    },
    "productos": [
      {
        "productoId": 1,
        "cantidad": 50
      }
    ]
  }
]
```

**Código de Estado:** 200 OK

---

### 3. Auditorías por Usuario
**Endpoint:** `GET /api/auditoria/usuario/{username}`

**Descripción:** Obtiene todas las auditorías realizadas por un usuario específico.

**Parámetros de Ruta:**
- `username` (obligatorio): Nombre de usuario del empleado

**Ejemplo de Uso:** `GET /api/auditoria/usuario/admin`

**Respuesta Exitosa:**
```json
[
  {
    "id": 1,
    "tipoOperacion": "CREAR_PRODUCTO",
    "fecha": "2024-03-15T14:20:30",
    "usuario": {
      "id": 1,
      "username": "admin",
      "nombre": "Administrador"
    },
    "entidad": "Producto",
    "valoresAnteriores": null,
    "valoresNuevos": "{\"nombre\":\"Laptop HP\",\"stock\":100}"
  }
]
```

**Código de Estado:** 200 OK

---

### 4. Reporte General de Inventario
**Endpoint:** `GET /api/reportes/resumen`

**Descripción:** Genera un reporte general del inventario con información resumida.

**Parámetros de Consulta Opcionales:**
- `bodega` (opcional): Nombre de la bodega para filtrar (por defecto: "Central")

**Ejemplos de Uso:**
- `GET /api/reportes/resumen`
- `GET /api/reportes/resumen?bodega=Central`

**Respuesta Exitosa:**
```json
{
  "bodega": "Central Bogotá",
  "stockTotal": 450,
  "productosMasMovidos": ["Laptop", "Mouse", "Teclado", "Monitor", "Impresora"]
}
```

**Código de Estado:** 200 OK

---

## 🧪 Pruebas con cURL

### 1. Productos con Stock Bajo
```bash
curl -X GET http://localhost:8080/api/productos/stock-bajo
```

### 2. Movimientos por Fecha
```bash
curl -X GET "http://localhost:8080/api/movimientos/por-fecha?inicio=2024-01-01&fin=2024-12-31"
```

### 3. Auditorías por Usuario
```bash
curl -X GET http://localhost:8080/api/auditoria/usuario/admin
```

### 4. Reporte General
```bash
curl -X GET http://localhost:8080/api/reportes/resumen
```

### 5. Reporte por Bodega Específica
```bash
curl -X GET "http://localhost:8080/api/reportes/resumen?bodega=Central"
```

---

## 📈 Características Implementadas

✅ **Consultas Analíticas:**
- Productos con stock bajo (umbral: 10 unidades)
- Movimientos filtrados por rango de fechas
- Auditorías por usuario específico

✅ **Reportes JSON:**
- Resumen general de inventario
- Productos más movidos en el último mes
- Stock total por bodega

✅ **Integración con Repositorios Existentes:**
- Utiliza los repositorios ya implementados
- Aprovecha las consultas personalizadas existentes
- Mantiene la consistencia con la arquitectura actual

---

## 🔧 Notas Técnicas

- Todos los endpoints siguen el patrón RESTful
- Las fechas se manejan en formato ISO 8601
- Los reportes se generan en tiempo real
- La respuesta es en formato JSON estándar
- Se mantiene la seguridad y validación de Spring Boot

---

## 📋 Archivos Modificados/Creados

### Controladores:
- ✅ `ProductoController.java` - Agregado endpoint `/stock-bajo`
- ✅ `MovimientoController.java` - Agregado endpoint `/por-fecha`
- ✅ `AuditoriaController.java` - Nuevo controlador completo
- ✅ `ReporteController.java` - Controlador para reportes generales

### Servicios:
- ✅ `MovimientoService.java` - Método `findByFechaBetween()`
- ✅ `AuditoriaService.java` - Servicio para consultas de auditoría
- ✅ `ReporteService.java` - Servicio para generación de reportes

### Utilidades:
- ✅ `test-endpoints.bat` - Script de pruebas automatizadas
- ✅ `API-REPORTS-DOCUMENTATION.md` - Esta documentación

## 🎯 Resultado Esperado

✅ **Endpoints REST funcionales** con filtros y reportes JSON
✅ **Consultas correctamente mapeadas** y optimizadas
✅ **Pruebas manuales** disponibles con curl y Swagger
✅ **Sistema de reportes** integrado y operativo