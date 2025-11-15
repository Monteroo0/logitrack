# 📊 LogiTrack Advanced Queries & Reports API Documentation

## 🎯 New Endpoints Implemented

### 1. Productos con Stock Bajo
**Endpoint:** `GET /api/productos/stock-bajo`

**Descripción:** Obtiene todos los productos con stock menor a 10 unidades.

**Respuesta:**
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

---

### 2. Movimientos por Rango de Fechas
**Endpoint:** `GET /api/movimientos/por-fecha?inicio={fecha}&fin={fecha}`

**Descripción:** Obtiene todos los movimientos dentro de un rango de fechas específico.

**Parámetros:**
- `inicio` (required): Fecha de inicio en formato YYYY-MM-DD
- `fin` (required): Fecha de fin en formato YYYY-MM-DD

**Ejemplo:** `GET /api/movimientos/por-fecha?inicio=2024-01-01&fin=2024-12-31`

**Respuesta:**
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

---

### 3. Auditorías por Usuario
**Endpoint:** `GET /api/auditoria/usuario/{username}`

**Descripción:** Obtiene todas las auditorías realizadas por un usuario específico.

**Parámetros:**
- `username` (required): Nombre de usuario del empleado

**Ejemplo:** `GET /api/auditoria/usuario/admin`

**Respuesta:**
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

---

### 4. Reporte General de Inventario
**Endpoint:** `GET /api/reportes/resumen`

**Descripción:** Genera un reporte general del inventario con información resumida.

**Parámetros opcionales:**
- `bodega` (optional): Nombre de la bodega para filtrar (default: "Central")

**Ejemplos:**
- `GET /api/reportes/resumen`
- `GET /api/reportes/resumen?bodega=Central`

**Respuesta:**
```json
{
  "bodega": "Central Bogotá",
  "stockTotal": 450,
  "productosMasMovidos": ["Laptop", "Mouse", "Teclado", "Monitor", "Impresora"]
}
```

---

## 🧪 Testing con cURL

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