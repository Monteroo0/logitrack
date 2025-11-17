# LogiTrack — Sistema de Gestión y Auditoría de Bodegas

## Descripción del Proyecto
LogiTrack S.A. administra bodegas distribuidas en varias ciudades y gestiona movimientos de inventario (entradas, salidas y transferencias). Este proyecto implementa un backend centralizado con Spring Boot que:
- Controla movimientos entre bodegas
- Registra cambios de forma automática (auditoría)
- Protege la información con autenticación JWT
- Expone endpoints REST seguros y documentados

## Objetivo General
Desarrollar un sistema de gestión y auditoría que registre transacciones de inventario y genere reportes auditables por usuario.

## Requisitos Funcionales
1. Gestión de Bodegas: CRUD completo. Campos: `id`, `nombre`, `ubicacion`, `capacidad`, `encargado`.
2. Gestión de Productos: CRUD completo. Campos: `id`, `nombre`, `categoria`, `stock`, `precio`.
3. Movimientos: Registrar `ENTRADA`, `SALIDA`, `TRANSFERENCIA` con: `fecha`, `tipo`, `usuario`, `bodegaOrigen/destino`, `productos` y `cantidades`, `observaciones` (opcional, máx. 500).
4. Auditoría: Entidad `Auditoria` con `tipoOperacion (INSERT/UPDATE/DELETE)`, `fecha`, `usuario`, `entidad`, `valoresAnteriores`, `valoresNuevos`. Auditoría automática en movimientos y usuarios.
5. Seguridad (Spring Security + JWT): `/auth/login`, `/auth/register`. Rutas seguras: `/api/bodegas`, `/api/productos`, `/api/movimientos`. Roles: `ADMIN` y `EMPLEADO`.
6. Reportes: 
   - Productos con stock bajo (< 10)
   - Movimientos por rango de fechas
   - Auditorías por usuario/tipo
   - Resumen general (JSON): stock total, totales, KPIs y productos más movidos
   - Consolidado de movimientos (en módulo Reportes) con exportación Excel/CSV
7. Documentación: Swagger/OpenAPI 3
8. Excepciones y Validaciones: `@ControllerAdvice`, `@NotNull`, `@Size`, `@Min`, etc.
9. Despliegue: MySQL, scripts SQL `schema.sql`, `data.sql`, Tomcat embebido, frontend básico HTML/CSS/JS.

## Arquitectura y Estructura
```
src/main/java/com/logitrack/
├─ controller/      # Endpoints REST (Auth, Bodega, Producto, Movimiento, Reporte, Auditoría)
├─ service/         # Lógica de negocio y agregaciones
├─ repository/      # Repositorios JPA
├─ model/           # Entidades JPA (Usuario, Rol, Bodega, Producto, Movimiento, Auditoria)
├─ config/          # Configuración de Security, CORS
├─ security/        # JWT util y filtro
└─ exception/       # (Opcional) Manejo global de errores
```
Frontend básico: `src/main/resources/static/` con HTML/CSS/JS.

## Instalación
Requisitos:
- Java 17
- Maven 3.9+
- MySQL 8+

Pasos:
1. Configura MySQL y credenciales en `application.properties`.
2. Crea la base y carga scripts:
   - `src/main/resources/schema.sql`
   - `src/main/resources/data.sql`
3. Compila e instala:
   - `mvn clean install -DskipTests`

## Ejecución
Desarrollo:
- `mvn spring-boot:run`

Producción (jar):
- `java -jar target/logitrack-0.0.1-SNAPSHOT.jar`

Servidor por defecto: `http://localhost:8080/`

## Autenticación JWT
- Login: `POST /auth/login` `{username, password}` → `{token}`
- Usa `Authorization: Bearer <token>` en llamadas a `/api/**`
- Roles: `ADMIN` tiene acceso a `/api/auditoria/**` y `/api/usuarios/**`

## Endpoints Principales
- Bodegas: `GET/POST/PUT/DELETE /api/bodegas/**`
- Productos: `GET/POST/PUT/DELETE /api/productos/**`
- Movimientos:
  - `POST /api/movimientos` (ENTRADA/SALIDA/TRANSFERENCIA)
  - `GET /api/movimientos/por-fecha?inicio=YYYY-MM-DD&fin=YYYY-MM-DD`
- Reportes:
  - `GET /api/reportes/resumen`
  - `GET /api/reportes/movimientos/export.xlsx?inicio&fin&tipo&orden`
  - Consolidado: `GET /api/reportes/consolidado`, `GET /api/reportes/consolidado/resumen`
  - Export: `GET /api/reportes/consolidado/export.xlsx`, `GET /api/reportes/consolidado/export.csv`
- Auditoría:
  - `GET /api/auditoria` (ADMIN)
  - Filtros mixtos: `GET /api/auditoria/filtrar-mixto?usuario&operacion&entidad&inicio&fin&tipoMovimiento`
  - Export: `GET /api/auditoria/export.xlsx`, `GET /api/auditoria/export.pdf` (ADMIN)

## Validaciones y Errores
- Validaciones en DTOs (`@Size`, `@Min`, etc.)
- Respuestas error JSON: 400 (validación), 401 (auth), 403 (rol), 404 (no encontrado), 500 (interno)

## Swagger / OpenAPI
- Habilitar Swagger UI (si configurado): `/swagger-ui/index.html`
- Documentación OpenAPI: `/v3/api-docs`

## Base de Datos
- `schema.sql`: tablas `rol`, `usuario`, `bodega`, `producto`, `movimiento`, `movimiento_producto`, `auditoria`
- `data.sql`: datos iniciales de roles, usuarios, bodegas y productos

## Frontend Básico
- Páginas estáticas para login y módulos (HTML/CSS/JS): `static/`
- Usa JWT en `localStorage` y llama a endpoints con `fetch`/`api.js`
- Favicon en todas las páginas: `img/favicon.ico`

## Seguridad
- Autenticación JWT y autorización por rol
- Filtro `JwtAuthenticationFilter` integrado en la cadena de seguridad
- CORS permitido para métodos y headers comunes

## Capturas y Pruebas
- Swagger: UI de endpoints
- Pruebas manuales con Postman/HTTPie usando JWT
- Exportaciones Excel/PDF para auditoría y consolidado de reportes

## Despliegue
- Configura `spring.datasource.*` para MySQL
- Ajusta puerto si 8080 está en uso: `server.port=8081`
- Empaquetado jar con `spring-boot-maven-plugin`

## Criterios de Evaluación
1. Dominio y manejo del código
2. Modelo JPA
3. Controladores y Servicios REST
4. Manejo de Excepciones y Validaciones
5. Auditoría Automática
6. Seguridad con JWT
7. Reportes y Consultas Avanzadas
8. Despliegue y README
9. Frontend Básico (HTML/JS)

## Notas
- Probar con usuario `admin` (rol ADMIN) y `empleado` (rol EMPLEADO).