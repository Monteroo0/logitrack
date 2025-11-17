## Cambios en Módulo Movimientos
- Observaciones obligatorias:
  - Backend: añadir campo `observaciones` a `Movimiento` y `MovimientoDTO`, validar longitud 10–500, persistir en `MovimientoService`.
    - `src/main/java/com/logitrack/model/Movimiento.java` (nueva columna `observaciones`)
    - `src/main/java/com/logitrack/dto/MovimientoDTO.java` (nuevo campo)
    - `src/main/java/com/logitrack/controller/MovimientoController.java:45-63` (validación y paso del campo)
    - `src/main/java/com/logitrack/service/MovimientoService.java:67-121` (persistencia del campo)
  - Front-end: añadir `<textarea>` de observaciones en los 3 formularios (Entrada/Salida/Transferencia) con validación de longitud antes de enviar.
    - `src/main/resources/static/movimientos.html` (formularios)
- Eliminar historial duplicado:
  - Quitar la sección “Historial de Movimientos” de `movimientos.html`; añadir un botón “Ver historial” que redirige a `reportes.html`.

## Cambios en Módulo Reportes
- Mostrar ID y nombre de usuario en movimientos por rango:
  - Backend: ampliar `MovimientoDTO` para incluir `usuarioNombre` (derivado de `Movimiento.usuario.nombre`).
  - Front-end: renderizar `Usuario` como “<id> - <nombre>”.
- Ordenamiento y preferencia:
  - Front-end: añadir selector “Orden” (Más reciente → Más antiguo | Más antiguo → Más reciente); aplicar orden y guardar preferencia en `localStorage` por `username`.
  - Render: colorear filas según tipo: ENTRADA (verde #4CAF50 texto blanco), SALIDA (rojo #F44336 texto blanco), TRANSFERENCIA (amarillo #FFEB3B texto negro).
- Exportación real a Excel:
  - Backend: crear endpoint `GET /api/reportes/movimientos/export.xlsx?inicio&fin&tipo&orden` que genere `.xlsx` con Apache POI (poi-ooxml) incluyendo las columnas visibles.
  - Front-end: los botones de exportación llamarán ese endpoint y dispararán descarga.
- Eliminar apartado de auditoría en reportes:
  - Remover la sección “Auditorías” de `reportes.html`; añadir enlace al módulo Auditoría.

## Cambios en Módulo Auditoría
- Registro completo:
  - Movimientos: ya se registran (`src/main/java/com/logitrack/service/MovimientoService.java:123-149`). Mantener.
  - Usuarios: auditar `INSERT/UPDATE` al crear/actualizar usuario en `UsuarioService.save` o en `UsuarioController`.
    - Crear util `AuditHelper` o lógica directa para `AuditoriaRepository.save` con fecha, usuario y acción.
- Exportación real:
  - Backend: endpoints `GET /api/auditoria/export.xlsx` y `GET /api/auditoria/export.pdf` (Apache POI para Excel; OpenPDF para PDF) con filtros.
  - Front-end: reemplazar funciones stub para invocar los endpoints y descargar.
- Visualización y filtros/paginación:
  - Backend: añadir `GET /api/auditoria/paged?usuario&tipo&inicio&fin&page&size` usando `Pageable`.
  - Front-end: actualizar `auditoria.html` para usar paginación y mantener filtros actuales. Asegurar estilos responsive.

## Dependencias nuevas
- `pom.xml`:
  - Apache POI: `org.apache.poi:poi-ooxml` para `.xlsx`.
  - OpenPDF: `com.github.librepdf:openpdf` para `.pdf`.

## Validación
- Movimientos: probar registro con observaciones válidas/inválidas; confirmar que se persisten y se muestran en reportes.
- Reportes: filtrar por rango/tipo; ver usuario “ID - Nombre”; aplicar orden y persistencia en `localStorage`; colores por tipo.
- Exportaciones: descargar Excel/PDF con >1000 registros, abrir en Excel/lector PDF.
- Auditoría: crear/actualizar usuario y verificar registro en tabla Auditoría.

¿Quieres que proceda con estos cambios y añada las dependencias para las exportaciones servidor-side?